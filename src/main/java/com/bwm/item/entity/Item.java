package com.bwm.item.entity;

import java.time.LocalDateTime;

import com.bwm.item.exception.ItemStateConflictException;
import com.bwm.user.entity.User;
import com.bwm.global.exception.ErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity
@Table(name = "item")
@Getter
@NoArgsConstructor
public class Item {

    private static final int MINIMUM_BID_INCREMENT = 100;
    private static final int EXTENSION_TRIGGER_MINUTES = 10;
    private static final int EXTENSION_MINUTES = 1;

    // PK. 자동증가(IDENTITY) - DB AUTO_INCREMENT와 동일 전략
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;

    // 판매자. FK(item.seller_id -> user.user_id)
    // NOT NULL이므로 상품 등록 시 항상 지정되어야 함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    // 현재 최고 입찰자. 입찰이 없으면 null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "highest_bidder_id")
    private User highestBidder;

    // 상품명. 최대 100자
    @Column(name = "title", length = 100, nullable = false)
    private String title;

    // 카테고리. 최대 45자
    @Column(name = "category", length = 45, nullable = false)
    private String category;

    // 경매 시작가. 입찰이 시작되기 전까지만 수정 가능
    @Column(name = "start_price", nullable = false)
    private Integer startPrice;

    // 현재가. 등록 시점에는 startPrice와 동일하고 입찰마다 갱신
    @Column(name = "current_price", nullable = false)
    private Integer currentPrice;

    // 경매 마감 시각. 이 시각 이후 입찰 불가
    @Column(name = "auction_end_at", nullable = false)
    private LocalDateTime auctionEndAt;

    // 경매 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ItemStatus status;

    // 등록 시각. 한 번 저장되면 수정되지 않음
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 상품 설명. 선택 입력. 최대 255자
    @Column(name = "description", length = 255)
    private String description;

    /**
     * 생성자는 private + Builder 조합으로 제한합니다.
     * 외부에서는 create() 정적 팩토리 메서드를 통해 생성합니다.
     */
    @Builder
    private Item(
            User seller,
            String title,
            String category,
            Integer startPrice,
            LocalDateTime auctionEndAt,
            String description
    ) {
        this.seller = seller;
        this.title = title;
        this.category = category;
        this.startPrice = startPrice;
        this.currentPrice = startPrice;
        this.auctionEndAt = auctionEndAt;
        this.status = ItemStatus.OPEN;
        this.createdAt = LocalDateTime.now();
        this.description = description;
    }

    /**
     * 상품 등록용 정적 팩토리 메서드입니다.
     *
     * @param seller 판매자
     * @param title 상품명
     * @param category 카테고리
     * @param startPrice 시작가
     * @param auctionEndAt 경매 마감 시각
     * @param description 상품 설명
     * @return 아직 저장되지 않은 Item 인스턴스
     */
    public static Item create(
            User seller,
            String title,
            String category,
            Integer startPrice,
            LocalDateTime auctionEndAt,
            String description
    ) {
        return Item.builder()
                .seller(seller)
                .title(title)
                .category(category)
                .startPrice(startPrice)
                .auctionEndAt(auctionEndAt)
                .description(description)
                .build();
    }

    /**
     * JPA가 INSERT 쿼리를 실행하기 직전에 호출되는 콜백입니다.
     */
    @PrePersist
    private void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    /**
     * 현재 상품의 최소 입찰 가능 금액을 반환합니다.
     *
     * 입찰자가 아직 없다면 시작가부터 입찰할 수 있습니다.
     * 입찰자가 이미 있다면 현재가보다 최소 100P 높은 금액부터 입찰할 수 있습니다.
     *
     * 예시
     * - 시작가 5,000P, 입찰 없음: 최소 입찰가 5,000P
     * - 현재가 5,000P, 입찰 있음: 최소 입찰가 5,100P
     *
     * @return 현재 시점의 최소 입찰 가능 금액
     */
    public int getMinimumBidAmount() {
        if (this.highestBidder == null) {
            return this.startPrice;
        }

        return this.currentPrice + MINIMUM_BID_INCREMENT;
    }

    /**
     * 최고 입찰자가 존재하는 경매를 낙찰 완료 상태로 변경합니다.
     */
    public void closeAsSold() {
        if (this.status != ItemStatus.OPEN) {
            throw new ItemStateConflictException(ErrorCode.ITEM_STATE_CONFLICT,
                    "OPEN 상태의 경매만 SOLD로 변경할 수 있습니다."
            );
        }

        if (this.highestBidder == null) {
            throw new ItemStateConflictException(ErrorCode.ITEM_STATE_CONFLICT,
                    "최고 입찰자가 없는 경매는 SOLD로 변경할 수 없습니다."
            );
        }

        this.status = ItemStatus.SOLD;
    }

    /**
     * 최고 입찰자가 없는 경매를 유찰 상태로 변경합니다.
     */
    public void closeAsUnsold() {
        if (this.status != ItemStatus.OPEN) {
            throw new ItemStateConflictException(ErrorCode.ITEM_STATE_CONFLICT,
                    "OPEN 상태의 경매만 UNSOLD로 변경할 수 있습니다."
            );
        }

        if (this.highestBidder != null) {
            throw new ItemStateConflictException(ErrorCode.ITEM_STATE_CONFLICT,
                    "최고 입찰자가 있는 경매는 UNSOLD로 변경할 수 없습니다."
            );
        }

        this.status = ItemStatus.UNSOLD;
    }

    /**
     * 새로운 최고 입찰자와 현재가를 갱신합니다.
     *
     * 첫 입찰은 시작가와 같은 금액으로 입찰할 수 있습니다.
     * 두 번째 입찰부터는 현재가보다 최소 100P 높은 금액이어야 합니다.
     *
     * @param bidder 새로운 최고 입찰자
     * @param bidAmount 새로운 최고 입찰 금액
     */
    public void updateHighestBidder(
            User bidder,
            Integer bidAmount
    ) {
        if (this.status != ItemStatus.OPEN) {
            throw new ItemStateConflictException(ErrorCode.ITEM_STATE_CONFLICT,
                    "진행 중인 경매만 최고 입찰자를 변경할 수 있습니다."
            );
        }

        if (bidder == null) {
            throw new IllegalArgumentException(
                    "최고 입찰자는 null일 수 없습니다."
            );
        }

        int minimumBidAmount = getMinimumBidAmount();

        if (bidAmount == null || bidAmount < minimumBidAmount) {
            throw new IllegalArgumentException(
                    "입찰 금액은 최소 입찰 가능 금액인 "
                            + minimumBidAmount
                            + "P 이상이어야 합니다."
            );
        }

        this.highestBidder = bidder;
        this.currentPrice = bidAmount;
    }

    /**
     * 상품 정보를 수정합니다.
     *
     * PATCH 방식으로 null인 필드는 기존 값을 유지합니다.
     *
     * 입찰이 없는 상품:
     * - 제목, 카테고리, 설명, 시작가, 마감 시각 수정 가능
     * - 시작가가 변경되면 현재가도 같은 금액으로 변경
     *
     * 입찰이 있는 상품:
     * - 설명만 수정 가능
     *
     * 첫 입찰이 시작가와 같은 금액일 수 있으므로
     * currentPrice와 startPrice를 비교하지 않고
     * highestBidder 존재 여부로 입찰 여부를 판단합니다.
     */
    public void update(
            String title,
            String category,
            String description,
            Integer startPrice,
            LocalDateTime auctionEndAt
    ) {
        if (this.status != ItemStatus.OPEN) {
            throw new ItemStateConflictException(ErrorCode.ITEM_STATE_CONFLICT,
                    "진행 중인 경매만 수정할 수 있습니다."
            );
        }

        boolean hasBid = this.highestBidder != null;

        if (hasBid) {
            if (title != null
                    || category != null
                    || startPrice != null
                    || auctionEndAt != null) {
                throw new ItemStateConflictException(ErrorCode.ITEM_STATE_CONFLICT,
                        "입찰이 시작된 상품은 설명만 수정할 수 있습니다."
                );
            }

            if (description != null) {
                this.description = description;
            }

            return;
        }

        if (title != null) {
            this.title = title;
        }

        if (category != null) {
            this.category = category;
        }

        if (description != null) {
            this.description = description;
        }

        if (startPrice != null) {
            this.startPrice = startPrice;
            this.currentPrice = startPrice;
        }

        if (auctionEndAt != null) {
            this.auctionEndAt = auctionEndAt;
        }
    }

    // 마감 임박 입찰에 대해 새 입찰자 생길 시 시간 연장 
    public void extendAuctionEndTimeIfNeeded(LocalDateTime bidTime) {
        if (this.status != ItemStatus.OPEN) {
            return;
        }

        LocalDateTime extensionTriggerTime =
                this.auctionEndAt.minusMinutes(EXTENSION_TRIGGER_MINUTES);

        boolean isExtensionPeriod =
                !bidTime.isBefore(extensionTriggerTime)
                && bidTime.isBefore(this.auctionEndAt);

        if (isExtensionPeriod) {
            this.auctionEndAt =
                    this.auctionEndAt.plusMinutes(EXTENSION_MINUTES);
        }
    }
    
    /**
     * 판매자가 경매를 직접 취소합니다.
     *
     * 취소 가능 조건
     * - 경매가 OPEN 상태여야 함
     * - 아직 입찰자가 없어야 함
     *
     * 첫 입찰이 시작가와 같은 금액일 수 있으므로
     * currentPrice와 startPrice 비교가 아닌
     * highestBidder 존재 여부로 입찰 여부를 판단합니다.
     */
    public void cancel() {
        if (this.status != ItemStatus.OPEN) {
            throw new ItemStateConflictException(ErrorCode.ITEM_STATE_CONFLICT,
                    "진행 중인 경매만 취소할 수 있습니다."
            );
        }

        if (this.highestBidder != null) {
            throw new ItemStateConflictException(ErrorCode.ITEM_STATE_CONFLICT,
                    "입찰이 시작된 상품은 취소할 수 없습니다."
            );
        }

        this.status = ItemStatus.CANCELLED;
    }
}

