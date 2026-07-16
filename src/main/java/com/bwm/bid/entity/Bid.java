package com.bwm.bid.entity;

import java.time.LocalDateTime;

import com.bwm.item.entity.Item;
import com.bwm.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 입찰 정보를 저장하는 엔티티
 */
@Entity
@Table(name = "bid")
@Getter
@NoArgsConstructor
public class Bid {

    /**
     * 입찰 PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_id")
    private Integer bidId;

    /**
     * 입찰한 상품
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /**
     * 입찰자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id", nullable = false)
    private User bidder;

    /**
     * 입찰 금액
     */
    @Column(name = "bid_amount", nullable = false)
    private Integer bidAmount;

    /**
     * 입찰 시각
     */
    @Column(name = "bid_at", nullable = false, updatable = false)
    private LocalDateTime bidAt;

    @Builder
    public Bid(
            Item item,
            User bidder,
            Integer bidAmount
    ) {
        this.item = item;
        this.bidder = bidder;
        this.bidAmount = bidAmount;
    }

    /**
     * INSERT 직전에 입찰 시간을 저장
     */
    @PrePersist
    private void prePersist() {
        this.bidAt = LocalDateTime.now();
    }
}