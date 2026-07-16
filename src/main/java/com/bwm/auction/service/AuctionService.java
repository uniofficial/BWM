package com.bwm.auction.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bwm.auction.dto.AuctionCloseResponse;
import com.bwm.auction.exception.AuctionAlreadyClosedException;
import com.bwm.auction.exception.AuctionNotEndedException;
import com.bwm.auction.exception.AuctionPermissionDeniedException;
import com.bwm.auction.exception.ItemNotFoundException;
import com.bwm.item.entity.Item;
import com.bwm.item.entity.ItemStatus;
import com.bwm.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

/**
 * 경매 종료 비즈니스 로직을 담당하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class AuctionService {

    private final ItemRepository itemRepository;

    /**
     * 판매자의 요청으로 특정 경매를 종료합니다.
     *
     * 경매 종료 시 포인트를 추가 차감하지 않습니다.
     * 입찰 시점에 최고 입찰자의 포인트가 이미 차감되어 있기 때문입니다.
     *
     * @param itemId 종료할 상품 ID
     * @param requesterId 종료를 요청한 사용자 ID
     * @return 경매 종료 결과
     */
    @Transactional
    public AuctionCloseResponse closeAuction(
            Integer itemId,
            Integer requesterId) {

        // 1. 상품 존재 여부 확인
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ItemNotFoundException(itemId));

        // 2. 판매자 본인 여부 확인
        validateSeller(item, requesterId);

        // 3. 현재 경매 상태 확인
        validateOpenStatus(item);

        // 4. 경매 마감 시간이 지났는지 확인
        validateAuctionEndTime(item);

        // 5. 최고 입찰자 존재 여부에 따라 낙찰 또는 유찰 처리
        closeItem(item);

        /*
         * item은 영속 상태이므로 @Transactional의 변경 감지에 의해
         * 트랜잭션 종료 시 UPDATE 쿼리가 자동 실행됩니다.
         * 따라서 itemRepository.save(item)을 다시 호출할 필요는 없습니다.
         */

        // 6. 종료 결과 DTO 반환
        return AuctionCloseResponse.from(item);
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
     * 현재 상품이 경매 진행 중 상태인지 검증합니다.
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

        /*
         * 현재 시각이 auctionEndAt보다 이전이면 아직 종료할 수 없습니다.
         * 현재 시각과 auctionEndAt이 같거나 현재 시각이 더 늦으면 종료 가능합니다.
         */
        if (now.isBefore(item.getAuctionEndAt())) {
            throw new AuctionNotEndedException(
                    item.getItemId(),
                    item.getAuctionEndAt()
            );
        }
    }

    /**
     * 최고 입찰자 존재 여부에 따라 SOLD 또는 UNSOLD로 변경합니다.
     */
    private void closeItem(Item item) {

        if (item.getHighestBidder() != null) {
            item.closeAsSold();
            return;
        }

        item.closeAsUnsold();
    }
}