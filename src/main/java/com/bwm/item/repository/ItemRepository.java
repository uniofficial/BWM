package com.bwm.item.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bwm.item.entity.Item;
import com.bwm.item.entity.ItemStatus;

import jakarta.persistence.LockModeType;

/**
 * Item 엔티티의 DB 접근을 담당하는 Repository
 */
public interface ItemRepository extends JpaRepository<Item, Integer> {

    /**
     * 자동 마감 대상 상품을 조회합니다.
     */
    List<Item> findAllByStatusAndAuctionEndAtLessThanEqualOrderByAuctionEndAtAsc(
            ItemStatus status,
            LocalDateTime now
    );

    /**
     * 로그인 사용자가 최고 입찰자로 등록된 낙찰 완료 상품을 조회합니다.
     *
     * 최고 입찰자 ID와 상품 상태를 조건으로 조회하며,
     * 경매 마감 시각이 최신인 상품부터 반환합니다.
     *
     * @param userId 최고 입찰자 사용자 ID
     * @param status 조회할 상품 상태
     * @return 낙찰 상품 목록
     */
    List<Item> findAllByHighestBidderUserIdAndStatusOrderByAuctionEndAtDesc(
            Integer userId,
            ItemStatus status
    );

    /**
     * 입찰 처리 시 상품 행에 비관적 쓰기 락을 적용하여 조회합니다.
     *
     * 거의 동시에 여러 사용자가 입찰하더라도
     * 한 트랜잭션이 끝날 때까지 다른 트랜잭션은 해당 상품을 수정하지 못합니다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.itemId = :itemId")
    Optional<Item> findByIdForUpdate(
            @Param("itemId") Integer itemId
    );
}