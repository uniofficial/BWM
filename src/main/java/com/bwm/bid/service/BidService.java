package com.bwm.bid.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bwm.bid.dto.request.BidCreateRequest;
import com.bwm.bid.dto.response.BidResponse;
import com.bwm.bid.entity.Bid;
import com.bwm.bid.exception.AlreadyHighestBidderException;
import com.bwm.bid.exception.BidAmountTooLowException;
import com.bwm.bid.exception.BidAuctionEndedException;
import com.bwm.bid.exception.BidItemNotFoundException;
import com.bwm.bid.exception.BidNotOpenException;
import com.bwm.bid.exception.SellerCannotBidException;
import com.bwm.bid.repository.BidRepository;
import com.bwm.item.entity.Item;
import com.bwm.item.entity.ItemStatus;
import com.bwm.item.repository.ItemRepository;
import com.bwm.user.entity.User;
import com.bwm.user.repository.UserRepository;
import com.bwm.wallet.service.WalletService;

import lombok.RequiredArgsConstructor;

/**
 * 입찰 관련 비즈니스 로직을 담당하는 서비스 
 */
@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;

    /**
     * 상품에 새로운 입찰 등록
     *
     * 처리 순서:
     * 1. 상품을 비관적 락으로 조회(동시 입찰 방지) 
     * 2. 입찰자 조회
     * 3. 경매 상태 및 마감 시간 검증
     * 4. 판매자 본인 입찰 여부 검증
     * 5. 현재 최고 입찰자 중복 입찰 여부 검증
     * 6. 입찰 금액 검증
     * 7. 새 입찰자의 포인트 차감
     * 8. 기존 최고 입찰자의 포인트 환불
     * 9. Bid 저장
     * 10. Item의 최고 입찰자 및 현재가 갱신
     *
     * 모든 작업은 하나의 트랜잭션으로 처리 
     * 중간에 예외가 발생하면 지갑·입찰·상품 변경이 모두 롤백됨 
     *
     * @param itemId 입찰 대상 상품 ID
     * @param bidderId 입찰자 사용자 ID
     * @param request 입찰 요청 정보
     * @return 등록된 입찰 정보
     */
    @Transactional
    public BidResponse createBid(
            Integer itemId,
            Integer bidderId,
            BidCreateRequest request
    ) {
        // 1. 동시 입찰 방지를 위해 상품 행에 비관적 쓰기 락을 적용합니다.
        Item item = itemRepository.findByIdForUpdate(itemId)
                .orElseThrow(() -> new BidItemNotFoundException(itemId));

        // 2. 입찰자 정보를 조회합니다.
        User bidder = userRepository.findById(bidderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "입찰자를 찾을 수 없습니다. userId=" + bidderId
                        )
                );

        // 3. 경매 상태를 검증합니다.
        validateOpenStatus(item);

        // 4. 경매 마감 시간을 검증합니다.
        validateAuctionEndTime(item);

        // 5. 판매자가 자신의 상품에 입찰하는 것을 방지합니다.
        validateSellerCannotBid(item, bidderId);

        // 6. 현재 최고 입찰자가 다시 입찰하는 것을 방지합니다.
        validateAlreadyHighestBidder(item, bidderId);

        // 7. 입찰 금액이 현재가보다 높은지 검증합니다.
        validateBidAmount(item, request.bidAmount());

        /*
         * 기존 최고 입찰자와 기존 최고 입찰 금액을
         * Item 변경 전에 별도로 보관합니다.
         */
        User previousHighestBidder = item.getHighestBidder();
        Integer previousHighestBidAmount = item.getCurrentPrice();

        /*
         * 8. 새 입찰자의 지갑에서 새 입찰 금액 전액을 차감합니다.
         *
         * 잔액이 부족하면 Wallet.deduct()에서 예외가 발생하고
         * 전체 트랜잭션이 롤백됩니다.
         */
        walletService.deductBidPoint(
                bidderId,
                itemId,
                request.bidAmount()
        );

        /*
         * 9. 기존 최고 입찰자가 있다면 기존 입찰 금액을 환불합니다.
         *
         * 최초 입찰인 경우 previousHighestBidder가 null이므로
         * 환불 처리를 실행하지 않습니다.
         */
        if (previousHighestBidder != null) {
            walletService.refundBidPoint(
                    previousHighestBidder.getUserId(),
                    itemId,
                    previousHighestBidAmount
            );
        }

        // 10. 입찰 기록을 생성하고 저장합니다.
        Bid bid = Bid.builder()
                .item(item)
                .bidder(bidder)
                .bidAmount(request.bidAmount())
                .build();

        Bid savedBid = bidRepository.save(bid);

        // 11. 상품의 최고 입찰자와 현재가를 갱신합니다.
        item.updateHighestBidder(
                bidder,
                request.bidAmount()
        );

        /*
         * Item은 영속 상태이므로 트랜잭션 종료 시
         * 변경 감지에 의해 최고 입찰자와 현재가가 DB에 반영됩니다.
         */

        return BidResponse.from(savedBid);
    }

    /**
     * 현재 경매가 진행 중인지 검증합니다.
     */
    private void validateOpenStatus(Item item) {
        if (item.getStatus() != ItemStatus.OPEN) {
            throw new BidNotOpenException(
                    item.getItemId(),
                    item.getStatus()
            );
        }
    }

    /**
     * 아직 경매 마감 시간이 지나지 않았는지 검증합니다.
     *
     * 현재 시각이 마감 시각과 같거나 더 늦으면 입찰할 수 없습니다.
     */
    private void validateAuctionEndTime(Item item) {
        LocalDateTime now = LocalDateTime.now();

        if (!now.isBefore(item.getAuctionEndAt())) {
            throw new BidAuctionEndedException(
                    item.getItemId(),
                    item.getAuctionEndAt()
            );
        }
    }

    /**
     * 판매자가 자신의 상품에 입찰하지 못하도록 검증합니다.
     */
    private void validateSellerCannotBid(
            Item item,
            Integer bidderId
    ) {
        Integer sellerId = item.getSeller().getUserId();

        if (Objects.equals(sellerId, bidderId)) {
            throw new SellerCannotBidException(
                    item.getItemId(),
                    sellerId
            );
        }
    }

    /**
     * 현재 최고 입찰자가 같은 상품에 다시 입찰하지 못하도록 검증합니다.
     */
    private void validateAlreadyHighestBidder(
            Item item,
            Integer bidderId
    ) {
        User highestBidder = item.getHighestBidder();

        if (highestBidder != null
                && Objects.equals(
                        highestBidder.getUserId(),
                        bidderId
                )) {
            throw new AlreadyHighestBidderException(
                    item.getItemId(),
                    bidderId
            );
        }
    }

    /**
     * 새 입찰 금액이 최소 입찰 가능 금액 이상인지 검증합니다.
     *
     * 최소 입찰 단위는 100P이므로,
     * 현재가보다 최소 100P 이상 높은 금액만 입찰할 수 있습니다.
     */
    private void validateBidAmount(
            Item item,
            Integer bidAmount
    ) {
        final int minimumBidIncrement = 100;
        int minimumBidAmount =
                item.getCurrentPrice() + minimumBidIncrement;

        if (bidAmount == null || bidAmount < minimumBidAmount) {
            throw new BidAmountTooLowException(
                    item.getCurrentPrice(),
                    bidAmount,
                    minimumBidAmount
            );
        }
    }
    
}