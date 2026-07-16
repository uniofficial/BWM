package com.bwm.bid.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bwm.bid.entity.Bid;

/**
 * Bid 엔티티의 DB 접근을 담당하는 Repository 
 */
public interface BidRepository extends JpaRepository<Bid, Integer> {

    /**
     * 특정 상품의 전체 입찰 내역을 최신 입찰순으로 조회함 
     *
     * 추후 상품별 입찰 내역 조회 API에서 사용함 
     */
    List<Bid> findByItemItemIdOrderByBidAtDesc(Integer itemId);

    /**
     * 특정 사용자의 전체 입찰 내역을 최신 입찰순으로 조함 
     *
     * 추후 내 입찰 내역 조회 API에서 사용함 
     */
    List<Bid> findByBidderUserIdOrderByBidAtDesc(Integer userId);
}