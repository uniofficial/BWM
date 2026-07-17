package com.bwm.auction.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bwm.auction.dto.AuctionCloseResponse;
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
        // 1. 상품 존재 여부 확인
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        // 2. 요청자가 실제 판매자인지 확인
        validateSeller(item, requesterId);

        // 3. 아직 진행 중인 경매인지 확인
        validateOpenStatus(item);

        // 4. 마감 시간이 지났는지 확인
        validateAuctionEndTime(item);

        // 5. 최고 입찰자 존재 여부에 따라 SOLD 또는 UNSOLD 처리
        closeItem(item);

        /*
         * 조회한 Item은 JPA 영속 상태이므로
         * 트랜잭션 종료 시 변경 감지로 UPDATE 쿼리가 실행됩니다.
         */
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
     * 현재 시각을 기준으로 자동 마감 대상 상품 ID를 조회합니다.
     *
     * Entity 목록을 Scheduler까지 직접 넘기지 않고 ID만 반환하여
     * 각 상품을 별도의 트랜잭션으로 처리할 수 있게 합니다.
     *
     * @return 자동 마감 대상 상품 ID 목록
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
     * REQUIRES_NEW를 사용하여 상품마다 새로운 트랜잭션을 시작합니다.
     * 따라서 특정 상품 처리 중 문제가 발생해도
     * 이전에 정상 종료된 다른 상품의 변경 사항은 유지됩니다.
     *
     * @param itemId 자동 종료할 상품 ID
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void closeExpiredAuction(Integer itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        /*
         * 조회 시점과 실제 처리 시점 사이에 다른 요청이 먼저 종료했을 수 있습니다.
         * 이미 OPEN이 아니라면 중복 처리하지 않고 건너뜁니다.
         */
        if (item.getStatus() != ItemStatus.OPEN) {
            return;
        }

        /*
         * 마감 시각을 다시 검증합니다.
         */
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

        /*
         * 현재 시각이 마감 시각보다 이전이면 종료할 수 없습니다.
         * 두 시각이 같거나 현재 시각이 더 늦은 경우에는 종료할 수 있습니다.
         */
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