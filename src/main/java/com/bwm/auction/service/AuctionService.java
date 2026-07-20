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
import com.bwm.auction.exception.AuctionNotEndedException;
import com.bwm.auction.exception.AuctionPermissionDeniedException;
import com.bwm.auction.exception.ItemNotFoundException;
import com.bwm.item.entity.Item;
import com.bwm.item.entity.ItemStatus;
import com.bwm.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

/**
 * 경매 종료 및 낙찰 내역 관련 비즈니스 로직을 담당하는 서비스
 *
 * 판매자가 직접 요청하는 수동 종료와
 * 스케줄러가 수행하는 자동 종료를 모두 처리함
 */
@Service
@RequiredArgsConstructor
public class AuctionService {

    private final ItemRepository itemRepository;

    /**
     * 판매자의 요청으로 특정 경매를 종료합니다.
     *
     * 경매 종료 시 포인트를 다시 차감하지 않습니다.
     * 입찰 시점에 최고 입찰자의 포인트가 이미 처리되기 때문입니다.
     *
     * @param itemId 종료할 상품 ID
     * @param requesterId 종료를 요청한 사용자 ID
     * @return 경매 종료 결과
     */
    @Transactional
    public AuctionCloseResponse closeAuction(
            Integer itemId,
            Integer requesterId
    ) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        validateSeller(item, requesterId);
        validateOpenStatus(item);
        validateAuctionEndTime(item);

        closeItem(item);

        return AuctionCloseResponse.from(item);
    }

    /**
     * 로그인 사용자의 낙찰 내역을 조회합니다.
     *
     * 최고 입찰자가 로그인 사용자이고,
     * 경매 상태가 SOLD인 상품만 조회합니다.
     *
     * @param userId 로그인 사용자 ID
     * @return 최신 낙찰순으로 정렬된 낙찰 내역
     */
    @Transactional(readOnly = true)
    public List<WinningAuctionResponse> getMyWinningAuctions(
            Integer userId
    ) {
        if (userId == null) {
            throw new IllegalArgumentException(
                    "사용자 ID는 null일 수 없습니다."
            );
        }

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
     * 판매자가 로그인 사용자이고,
     * 경매 상태가 SOLD인 상품만 조회합니다.
     *
     * @param userId 로그인 사용자 ID
     * @return 최신 판매 완료순으로 정렬된 상품 목록
     */
    @Transactional(readOnly = true)
    public List<SoldAuctionResponse> getMySoldAuctions(
            Integer userId
    ) {
        if (userId == null) {
            throw new IllegalArgumentException(
                    "사용자 ID는 null일 수 없습니다."
            );
        }

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
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void closeExpiredAuction(Integer itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        if (item.getStatus() != ItemStatus.OPEN) {
            return;
        }

        if (LocalDateTime.now().isBefore(item.getAuctionEndAt())) {
            return;
        }

        closeItem(item);
    }

    /**
     * 요청자가 해당 상품의 판매자인지 검증합니다.
     */
    private void validateSeller(Item item, Integer requesterId) {
        Integer sellerId = item.getSeller().getUserId();

        if (!Objects.equals(sellerId, requesterId)) {
            throw new AuctionPermissionDeniedException(
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
                    item.getItemId(),
                    item.getStatus()
            );
        }
    }

    /**
     * 경매 마감 시간이 도달했는지 검증합니다.
     */
    private void validateAuctionEndTime(Item item) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(item.getAuctionEndAt())) {
            throw new AuctionNotEndedException(
                    item.getItemId(),
                    item.getAuctionEndAt()
            );
        }
    }

    /**
     * 최고 입찰자 존재 여부에 따라 경매 상태를 변경합니다.
     *
     * 최고 입찰자 있음: SOLD
     * 최고 입찰자 없음: UNSOLD
     */
    private void closeItem(Item item) {
        if (item.getHighestBidder() != null) {
            item.closeAsSold();
            return;
        }

        item.closeAsUnsold();
    }
}