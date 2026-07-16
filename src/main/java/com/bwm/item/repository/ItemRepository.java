package com.bwm.item.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bwm.item.entity.Item;
import com.bwm.item.entity.ItemStatus;

/**
 * Item 엔티티의 DB 접근을 담당하는 Repository입니다.
 *
 * JpaRepository를 상속하므로 save(), findById(), findAll() 등
 * 기본 CRUD 기능을 별도 구현 없이 사용할 수 있습니다.
 */
public interface ItemRepository extends JpaRepository<Item, Integer> {

    /**
     * 특정 상태이면서 마감 시간이 기준 시각 이하인 상품을 조회합니다.
     *
     * 자동 마감 스케줄러에서는 다음 조건으로 사용합니다.
     *
     * status = OPEN
     * auctionEndAt <= 현재 시각
     *
     * 오래전에 만료된 경매부터 처리할 수 있도록
     * auctionEndAt 오름차순으로 정렬합니다.
     *
     * @param status 조회할 상품 상태
     * @param now 기준 시각
     * @return 자동 마감 대상 상품 목록
     */
    List<Item> findAllByStatusAndAuctionEndAtLessThanEqualOrderByAuctionEndAtAsc(
            ItemStatus status,
            LocalDateTime now
    );
}