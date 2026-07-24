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
import com.bwm.item.entity.ItemImage;
import com.bwm.item.entity.ItemStatus;
import com.bwm.item.repository.ItemImageRepository;
import com.bwm.item.repository.ItemRepository;
import com.bwm.user.entity.User;
import com.bwm.user.repository.UserRepository;
import com.bwm.wallet.service.WalletService;
import com.bwm.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

/**
 * 입찰 관련 비즈니스 로직을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final ItemRepository itemRepository;
    private final ItemImageRepository itemImageRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;

    /**
     * 새로운 입찰을 등록합니다.
     *
     * JWT 인증 정보에서 추출한 로그인 사용자의 uuid로
     * 실제 사용자 엔티티를 조회하고 입찰자로 사용합니다.
     *
     * 입찰 처리 과정 전체를 하나의 트랜잭션으로 묶어
     * 입찰 저장, 포인트 차감·환불, 상품 정보 변경 중 하나라도 실패하면
     * 모든 변경 사항을 롤백합니다.
     *
     * @param itemId 입찰 대상 상품 ID
     * @param bidderUuid 로그인한 입찰자의 UUID
     * @param request 입찰 요청 정보
     * @return 등록된 입찰 정보
     */
    @Transactional
    public BidResponse createBid(
            Integer itemId,
            String bidderUuid,
            BidCreateRequest request
    ) {
        Item item = itemRepository.findByIdForUpdate(itemId)
                .orElseThrow(() ->
                        new BidItemNotFoundException(ErrorCode.BID_ITEM_NOT_FOUND, itemId));

        return createBidInternal(
                item,
                bidderUuid,
                request.bidAmount()
        );
    }
    
    @Transactional
    public BidResponse createQuickBid(
            Integer itemId,
            String bidderUuid
    ) {
        Item item = itemRepository.findByIdForUpdate(itemId)
                .orElseThrow(() ->
                        new BidItemNotFoundException(ErrorCode.BID_ITEM_NOT_FOUND, itemId));

        return createBidInternal(
                item,
                bidderUuid,
                item.getMinimumBidAmount()
        );
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
            throw new BidItemNotFoundException(ErrorCode.BID_ITEM_NOT_FOUND, itemId);
        }

        return bidRepository
                .findByItemItemIdOrderByBidAtDesc(itemId)
                .stream()
                .map(ItemBidHistoryResponse::from)
                .toList();
    }

    /**
     * 로그인 사용자의 전체 입찰 내역을 최신순으로 조회합니다.
     *
     * JWT 인증 정보에서 추출한 이메일로 사용자를 조회하므로
     * 클라이언트가 사용자 ID를 전달하지 않습니다.
     *
     * 사용자가 한 번이라도 입찰한 모든 기록을 반환합니다.
     * 같은 상품에 여러 번 입찰한 경우 각각의 입찰 기록이 모두 반환됩니다.
     *
     * 응답에는 상품 정보, 입찰 금액, 입찰 시각과 함께
     * 현재 최고 입찰자인지 여부가 포함됩니다.
     *
     */
    @Transactional(readOnly = true)
    public List<MyBidHistoryResponse> getMyBidHistory(
            String userUuid
    ) {
        User user = getUserByUuid(userUuid);
        Integer userId = user.getUserId();

        return bidRepository
                .findByBidderUserIdOrderByBidAtDesc(userId)
                .stream()
                .map(bid ->
                        MyBidHistoryResponse.from(
                                bid,
                                userId,
                                findRepresentativeImageUrl(bid.getItem().getItemId())
                        ))
                .toList();
    }

    /**
     * 상품의 대표 이미지 URL을 조회합니다. 등록된 이미지가 없으면 null을 반환합니다.
     */
    private String findRepresentativeImageUrl(Integer itemId) {
        return itemImageRepository
                .findAllByItem_ItemIdOrderByIsRepresentativeDescCreatedAtAsc(itemId)
                .stream()
                .findFirst()
                .map(ItemImage::getImageUrl)
                .orElse(null);
    }

    /**
     * uuid 기준으로 로그인 사용자 엔티티를 조회합니다.
     */
    private User getUserByUuid(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException("인증된 사용자 식별자가 없습니다.");
        }

        return userRepository.findByUserUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException(
                        "사용자를 찾을 수 없습니다. uuid="
                                + uuid));
    }

    /**
     * 경매가 현재 진행 중인지 검증합니다.
     */
    private void validateOpenStatus(Item item) {
        if (item.getStatus() != ItemStatus.OPEN) {
            throw new BidNotOpenException(
                    ErrorCode.BID_NOT_OPEN,
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
    private void validateAuctionEndTime(
            Item item,
            LocalDateTime bidTime
    ) {
        if (!bidTime.isBefore(item.getAuctionEndAt())) {
            throw new BidAuctionEndedException(
                    ErrorCode.BID_AUCTION_ENDED,
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
                    ErrorCode.SELLER_CANNOT_BID,
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
                    ErrorCode.ALREADY_HIGHEST_BIDDER,
                    item.getItemId(),
                    bidderId
            );
        }
    }
    
    private BidResponse createBidInternal(
            Item item,
            String bidderUuid,
            Integer bidAmount
    ) {
    	LocalDateTime bidTime = LocalDateTime.now();
    	
        User bidder = getUserByUuid(bidderUuid);
        Integer bidderId = bidder.getUserId();

        validateOpenStatus(item);
        validateAuctionEndTime(item, bidTime);
        validateSellerCannotBid(item, bidderId);
        validateAlreadyHighestBidder(item, bidderId);
        validateBidAmount(item, bidAmount);

        User previousHighestBidder = item.getHighestBidder();
        Integer previousHighestBidAmount = item.getCurrentPrice();

        walletService.deductBidPoint(
                bidderId,
                item.getItemId(),
                bidAmount
        );

        if (previousHighestBidder != null) {
            walletService.refundBidPoint(
                    previousHighestBidder.getUserId(),
                    item.getItemId(),
                    previousHighestBidAmount
            );
        }

        Bid bid = Bid.builder()
                .item(item)
                .bidder(bidder)
                .bidAmount(bidAmount)
                .build();

        Bid savedBid = bidRepository.save(bid);

        item.updateHighestBidder(
                bidder,
                bidAmount
        );
        
        item.extendAuctionEndTimeIfNeeded(bidTime);
        
        return BidResponse.from(savedBid);
    }

    /**
     * 입찰 금액이 최소 입찰 가능 금액 이상인지 검증합니다.
     *
     * 첫 입찰은 시작가부터 가능하며,
     * 기존 입찰자가 있는 경우 현재가보다 100P 높은 금액부터 가능합니다.
     */
    private void validateBidAmount(
            Item item,
            Integer bidAmount
    ) {
        int minimumBidAmount = item.getMinimumBidAmount();

        if (bidAmount == null
                || bidAmount < minimumBidAmount) {
            throw new BidAmountTooLowException(
                    ErrorCode.BID_AMOUNT_TOO_LOW,
                    item.getCurrentPrice(),
                    bidAmount,
                    minimumBidAmount
            );
        }
    }
}