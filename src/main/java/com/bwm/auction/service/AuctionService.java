package com.bwm.auction.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bwm.auction.dto.AuctionCloseResponse;
import com.bwm.auction.dto.SoldAuctionResponse;
import com.bwm.auction.dto.WinningAuctionResponse;
import com.bwm.auction.exception.AuctionAlreadyClosedException;
import com.bwm.auction.exception.AuctionPermissionDeniedException;
import com.bwm.item.exception.ItemNotFoundException;
import com.bwm.item.entity.Item;
import com.bwm.item.entity.ItemStatus;
import com.bwm.item.repository.ItemRepository;
import com.bwm.user.entity.User;
import com.bwm.user.repository.UserRepository;
import com.bwm.wallet.service.WalletService;
import com.bwm.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

/**
 * 경매 종료 및 낙찰 내역 관련 비즈니스 로직을 담당하는 서비스
 *
 * 판매자가 직접 요청하는 수동 종료와
 * 스케줄러가 수행하는 자동 종료를 모두 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class AuctionService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;

    /**
     * 판매자의 요청으로 진행 중인 경매를 즉시 종료합니다.
     *
     * 마감 시간 이전에도 종료할 수 있으며, 이를 통해 조기 종료가 가능합니다.
     * 마감 시간이 지난 경매는 스케줄러가 자동으로 처리하며,
     * 이 API는 판매자의 의도적인 조기 종료 목적으로 사용합니다.
     *
     * JWT 인증 정보에서 추출한 사용자 UUID로 로그인 사용자를 조회하고,
     * 해당 사용자가 상품 판매자인지 검증합니다.
     *
     * 최고 입찰자가 있으면 SOLD, 없으면 UNSOLD로 처리됩니다.
     * 입찰자 없이 취소를 원하는 경우에는 cancel API를 사용해야 합니다.
     *
     * 비관적 쓰기 락을 사용하여 자동 종료(스케줄러)와 수동 종료가 동시에
     * 실행돼도 한 트랜잭션만 종료 및 판매대금 지급을 수행하도록 합니다.
     *
     * @param itemId 종료할 상품 ID
     * @param requesterUuid 종료를 요청한 로그인 사용자의 UUID
     * @return 경매 종료 결과
     */
    @Transactional
    public AuctionCloseResponse closeAuction(
            Integer itemId,
            String requesterUuid
    ) {
        /*
         * 기존 findById() 대신 비관적 쓰기 락 조회를 사용합니다.
         *
         * 동일 상품에 대해 판매자 수동 종료와 스케줄러 자동 종료가
         * 동시에 실행될 경우, 먼저 락을 획득한 트랜잭션만 처리합니다.
         *
         * 나중에 락을 획득한 트랜잭션은 변경된 상품 상태를 읽으므로
         * 중복 종료 및 판매대금 중복 지급이 방지됩니다.
         */
        Item item = itemRepository.findByIdForUpdate(itemId)
                .orElseThrow(() ->
                        new ItemNotFoundException(ErrorCode.ITEM_NOT_FOUND, "존재하지 않는 상품입니다. id = " + itemId));

        User requester = getUserByUuid(requesterUuid);
        Integer requesterId = requester.getUserId();

        validateSeller(item, requesterId);
        validateOpenStatus(item);

        closeItem(item);

        return AuctionCloseResponse.from(item);
    }

    /**
     * 로그인 사용자의 낙찰 내역을 조회합니다.
     *
     * JWT 인증 정보에서 추출한 사용자 UUID로 사용자를 조회한 뒤,
     * 해당 사용자가 최고 입찰자이고 경매 상태가 SOLD인 상품만 조회합니다.
     *
     * @param userUuid 로그인 사용자의 UUID
     * @return 최신 낙찰순으로 정렬된 낙찰 내역
     */
    @Transactional(readOnly = true)
    public List<WinningAuctionResponse> getMyWinningAuctions(
            String userUuid
    ) {
        User user = getUserByUuid(userUuid);
        Integer userId = user.getUserId();

        return itemRepository
                .findAllByHighestBidderUserIdAndStatusOrderByAuctionEndAtDesc(
                        userId,
                        ItemStatus.SOLD
                )
                .stream()
                .map(WinningAuctionResponse::from)
                .toList();
    }

    /**
     * 로그인 사용자의 판매 완료 내역을 조회합니다.
     *
     * JWT 인증 정보에서 추출한 사용자 UUID로 사용자를 조회한 뒤,
     * 해당 사용자가 판매자이고 경매 상태가 SOLD인 상품만 조회합니다.
     *
     * @param userUuid 로그인 사용자의 UUID
     * @return 최신 판매 완료순으로 정렬된 상품 목록
     */
    @Transactional(readOnly = true)
    public List<SoldAuctionResponse> getMySoldAuctions(
            String userUuid
    ) {
        User user = getUserByUuid(userUuid);
        Integer userId = user.getUserId();

        return itemRepository
                .findAllBySellerUserIdAndStatusOrderByAuctionEndAtDesc(
                        userId,
                        ItemStatus.SOLD
                )
                .stream()
                .map(SoldAuctionResponse::from)
                .toList();
    }

    /**
     * 현재 시각을 기준으로 자동 마감 대상 상품 ID를 조회합니다.
     *
     * 이 메서드는 종료 대상 ID만 조회하며 실제 경매 종료는 수행하지 않습니다.
     * 실제 종료 시에는 closeExpiredAuction()에서 상품을 다시 조회하면서
     * 비관적 쓰기 락을 획득합니다.
     */
    @Transactional(readOnly = true)
    public List<Integer> findExpiredAuctionIds() {
        LocalDateTime now = LocalDateTime.now();

        return itemRepository
                .findAllByStatusAndAuctionEndAtLessThanEqualOrderByAuctionEndAtAsc(
                        ItemStatus.OPEN,
                        now
                )
                .stream()
                .map(Item::getItemId)
                .toList();
    }

    /**
     * 스케줄러가 특정 만료 경매 한 건을 자동으로 종료합니다.
     *
     * 자동 마감은 HTTP 요청이나 로그인 사용자에 의해 실행되지 않으므로
     * Authentication 또는 사용자 UUID를 받지 않습니다.
     *
     * 각 경매 종료를 독립적인 트랜잭션으로 처리하며,
     * 비관적 쓰기 락을 사용하여 수동 종료 또는 다른 스케줄러 실행과
     * 동시에 처리되는 것을 방지합니다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void closeExpiredAuction(Integer itemId) {
        /*
         * 기존 findById() 대신 비관적 쓰기 락 조회를 사용합니다.
         *
         * 같은 상품을 수동 종료 요청이나 다른 서버의 스케줄러가
         * 동시에 처리하려 해도 하나의 트랜잭션만 먼저 처리할 수 있습니다.
         */
        Item item = itemRepository.findByIdForUpdate(itemId)
                .orElseThrow(() ->
                        new ItemNotFoundException(ErrorCode.ITEM_NOT_FOUND, "존재하지 않는 상품입니다. id = " + itemId));

        /*
         * 락을 획득한 뒤 상태를 다시 검사해야 합니다.
         *
         * 락을 기다리는 동안 다른 트랜잭션이 이미 경매를 종료했다면
         * 여기서 SOLD 또는 UNSOLD 상태를 확인하고 종료합니다.
         */
        if (item.getStatus() != ItemStatus.OPEN) {
            return;
        }

        /*
         * findExpiredAuctionIds()에서 만료 상품을 조회한 이후
         * 실제 종료 메서드가 실행될 때까지 시간이 흐를 수 있으므로
         * 종료 시각을 다시 검증합니다.
         */
        if (LocalDateTime.now().isBefore(item.getAuctionEndAt())) {
            return;
        }

        closeItem(item);
    }

    /**
     * JWT subject의 UUID를 기준으로 로그인 사용자 엔티티를 조회합니다.
     */
    private User getUserByUuid(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException(
                    "인증된 사용자 식별자가 없습니다."
            );
        }

        return userRepository.findByUserUuid(uuid)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "사용자를 찾을 수 없습니다. uuid="
                                        + uuid
                        )
                );
    }

    /**
     * 요청자가 해당 상품의 판매자인지 검증합니다.
     */
    private void validateSeller(
            Item item,
            Integer requesterId
    ) {
        Integer sellerId = item.getSeller().getUserId();

        if (!Objects.equals(sellerId, requesterId)) {
            throw new AuctionPermissionDeniedException(
                    ErrorCode.AUCTION_PERMISSION_DENIED,
                    item.getItemId(),
                    requesterId
            );
        }
    }

    /**
     * 현재 상품이 진행 중인 경매인지 검증합니다.
     */
    private void validateOpenStatus(Item item) {
        if (item.getStatus() != ItemStatus.OPEN) {
            throw new AuctionAlreadyClosedException(
                    ErrorCode.AUCTION_ALREADY_CLOSED,
                    item.getItemId(),
                    item.getStatus()
            );
        }
    }



    /**
     * 최고 입찰자 존재 여부에 따라 경매 상태를 변경합니다.
     *
     * 최고 입찰자 있음:
     * - 상품 상태를 SOLD로 변경
     * - 판매자에게 최종 낙찰 금액 지급
     *
     * 최고 입찰자 없음:
     * - 상품 상태를 UNSOLD로 변경
     *
     * 이 메서드는 closeAuction() 또는 closeExpiredAuction()이
     * 상품 비관적 락을 획득한 상태에서 호출됩니다.
     */
    private void closeItem(Item item) {
        if (item.getHighestBidder() != null) {
            item.closeAsSold();

            walletService.depositSalesRevenue(
                    item.getSeller().getUserId(),
                    item.getItemId(),
                    item.getCurrentPrice()
            );

            return;
        }

        item.closeAsUnsold();
    }
}