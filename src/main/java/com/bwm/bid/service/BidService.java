package com.bwm.bid.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bwm.bid.dto.request.BidCreateRequest;
import com.bwm.bid.dto.response.BidResponse;
import com.bwm.bid.dto.response.ItemBidHistoryResponse;
import com.bwm.bid.dto.response.MyBidHistoryResponse;
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

    private static final int MINIMUM_BID_INCREMENT = 100;

    private final BidRepository bidRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;

    /**
     * 새로운 입찰을 등록합니다.
     *
     * 입찰 처리 과정 전체를 하나의 트랜잭션으로 묶어
     * 입찰 저장, 포인트 차감·환불, 상품 정보 변경 중 하나라도 실패하면
     * 모든 변경 사항을 롤백합니다.
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
        /*
         * 같은 상품에 여러 요청이 동시에 들어오는 경우를 방지하기 위해
         * 상품 행을 비관적 쓰기 락으로 조회합니다.
         */
        Item item = itemRepository.findByIdForUpdate(itemId)
                .orElseThrow(() ->
                        new BidItemNotFoundException(itemId));

        User bidder = userRepository.findById(bidderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "입찰자를 찾을 수 없습니다. userId="
                                        + bidderId
                        ));

        validateOpenStatus(item);
        validateAuctionEndTime(item);
        validateSellerCannotBid(item, bidderId);
        validateAlreadyHighestBidder(item, bidderId);
        validateBidAmount(item, request.bidAmount());

        /*
         * Item 정보를 변경하기 전에 기존 최고 입찰자와 입찰 금액을 저장합니다.
         * 이후 기존 최고 입찰자에게 환불할 때 사용합니다.
         */
        User previousHighestBidder = item.getHighestBidder();
        Integer previousHighestBidAmount = item.getCurrentPrice();

        /*
         * 새 입찰자의 지갑에서 입찰 금액 전액을 차감합니다.
         */
        walletService.deductBidPoint(
                bidderId,
                itemId,
                request.bidAmount()
        );

        /*
         * 기존 최고 입찰자가 있다면 기존 입찰 금액을 환불합니다.
         */
        if (previousHighestBidder != null) {
            walletService.refundBidPoint(
                    previousHighestBidder.getUserId(),
                    itemId,
                    previousHighestBidAmount
            );
        }

        /*
         * 입찰 이력을 저장합니다.
         */
        Bid bid = Bid.builder()
                .item(item)
                .bidder(bidder)
                .bidAmount(request.bidAmount())
                .build();

        Bid savedBid = bidRepository.save(bid);

        /*
         * 상품의 최고 입찰자와 현재가를 변경합니다.
         *
         * Item은 영속 상태이므로 트랜잭션 종료 시
         * 변경 감지로 데이터베이스에 반영됩니다.
         */
        item.updateHighestBidder(
                bidder,
                request.bidAmount()
        );

        return BidResponse.from(savedBid);
    }

    /**
     * 특정 상품의 전체 입찰 내역을 최신순으로 조회합니다.
     *
     * 사용자 ID나 이메일은 반환하지 않고
     * 화면 표시용 닉네임만 포함된 DTO로 변환합니다.
     *
     * @param itemId 조회할 상품 ID
     * @return 해당 상품의 입찰 내역
     */
    @Transactional(readOnly = true)
    public List<ItemBidHistoryResponse> getItemBidHistory(
            Integer itemId
    ) {
        if (!itemRepository.existsById(itemId)) {
            throw new BidItemNotFoundException(itemId);
        }

        return bidRepository
                .findByItemItemIdOrderByBidAtDesc(itemId)
                .stream()
                .map(ItemBidHistoryResponse::from)
                .toList();
    }

    /**
     * 특정 사용자의 전체 입찰 내역을 최신순으로 조회합니다.
     *
     * 사용자가 한 번이라도 입찰한 모든 기록을 반환합니다.
     * 같은 상품에 여러 번 입찰한 경우 각각의 입찰 기록이 모두 반환됩니다.
     *
     * 응답에는 상품 정보, 입찰 금액, 입찰 시각과 함께
     * 현재 최고 입찰자인지 여부가 포함됩니다.
     *
     * @param userId 조회 사용자 ID
     * @return 사용자의 전체 입찰 내역
     */
    @Transactional(readOnly = true)
    public List<MyBidHistoryResponse> getMyBidHistory(
            Integer userId
    ) {
        /*
         * 존재하지 않는 사용자 ID가 전달된 경우
         * 잘못된 요청이 빈 목록으로 처리되지 않도록 검증합니다.
         */
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException(
                    "사용자를 찾을 수 없습니다. userId=" + userId
            );
        }

        return bidRepository
                .findByBidderUserIdOrderByBidAtDesc(userId)
                .stream()
                .map(bid ->
                        MyBidHistoryResponse.from(
                                bid,
                                userId
                        ))
                .toList();
    }

    /**
     * 경매가 현재 진행 중인지 검증합니다.
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
     * 경매 마감 시간이 지나지 않았는지 검증합니다.
     *
     * 현재 시각이 마감 시각과 같거나 이후라면
     * 입찰할 수 없습니다.
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
     * 입찰 금액이 최소 입찰 가능 금액 이상인지 검증합니다.
     *
     * 최소 입찰 가능 금액은 현재가보다 100P 높은 금액입니다.
     */
    private void validateBidAmount(
            Item item,
            Integer bidAmount
    ) {
        int minimumBidAmount =
                item.getCurrentPrice()
                + MINIMUM_BID_INCREMENT;

        if (bidAmount == null
                || bidAmount < minimumBidAmount) {
            throw new BidAmountTooLowException(
                    item.getCurrentPrice(),
                    bidAmount,
                    minimumBidAmount
            );
        }
    }
}