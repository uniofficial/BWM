package com.bwm.item.entity;

import java.time.LocalDateTime;

import com.bwm.user.entity.User;

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

    // PK. 자동증가(IDENTITY) - DB AUTO_INCREMENT와 동일 전략
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;

    // 판매자. FK(item .seller_id -> user.user_id) NOT NULL 이므로 상품 등록시 항상 지정되어야함 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    // 현재 최고 입찰자, 입찰 없으면 null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "highest_bidder_id")
    private User highestBidder;

    // 상품명. 최대 100자 
    @Column(name = "title", length = 100, nullable = false)
    private String title;

    // 카테고리. 최대 45자
    @Column(name = "category", length = 45, nullable = false)
    private String category;

    // 경매 시작가. 등록 후 변경 불가
    @Column(name = "start_price", nullable = false)
    private Integer startPrice;

    // 현재가. 등록 시점엔 startPrice와 동일, 입찰마다 갱신
   @Column(name = "current_price", nullable = false)
    private Integer currentPrice;

    // 경매 마감 시각. 이 시각 이후 입찰 불가 
    @Column(name = "auction_end_at", nullable = false)
    private LocalDateTime auctionEndAt;

    // 경매 상태. ItemStatus 참고
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ItemStatus status;

    // 등록 시각. 한 번 저장되면 수정되지 않음
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 상품 설명. 선택 입력. 최대 255
    @Column(name = "description", length = 255)
    private String description;

    /**
     * 생성자는 private + Builder 조합으로 막아둠
     * 외부에서 아래 create() 정적 메서드로만 인스턴스 만들 수 있다. 
     * 
     */

    @Builder
    private Item(User seller, String title, String category, Integer startPrice, LocalDateTime auctionEndAt, String description) {
        this.seller = seller;
        this.title = title;
        this.category = category;
        this.startPrice = startPrice;
        this.currentPrice = startPrice; // 등록 시점엔 현재가 == 시작가
        this.auctionEndAt = auctionEndAt;
        this.status = ItemStatus.OPEN; // 등록 시점엔 항상 OPEN
        this.createdAt = LocalDateTime.now();
        this.description = description;
    }


    /**
     * 상품 등록용 정적 팩토리 메서드
     * 
     * ItemServiceImpl 에서 이 메서드를 통해서만 Item 생성
     * 
     * @param seller 판매자
     * @param title 상품명
     * @param category 카테고리
     * @param startPrice 시작가
     * @param auctionEndAt 경매 마감 시각
     * @param description 상품 설명
     * @return  아직 저장 전 상태의 Item 인스턴스
     * 
     *  */

    public static Item create(User seller, String title, String category, Integer startPrice, LocalDateTime auctionEndAt, String description) {
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
     * JPA가 실제로 insert 쿼리를 날리기 직전 자동 호출되는 콜백
     * created_at 을 애플리케이션 레벨에서 채워준다. 
     */
    @PrePersist
    private void prePersist(){
        this.createdAt = LocalDateTime.now();
    }


    /**
     * 최고 입찰자가 존재하는 경매를 낙찰 완료 상태로 변경합니다.
     */
    public void closeAsSold() {
        if (this.status != ItemStatus.OPEN) {
            throw new IllegalStateException(
                    "OPEN 상태의 경매만 SOLD로 변경할 수 있습니다."
            );
        }

        if (this.highestBidder == null) {
            throw new IllegalStateException(
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
            throw new IllegalStateException(
                    "OPEN 상태의 경매만 UNSOLD로 변경할 수 있습니다."
            );
        }

        if (this.highestBidder != null) {
            throw new IllegalStateException(
                    "최고 입찰자가 있는 경매는 UNSOLD로 변경할 수 없습니다."
            );
        }

        this.status = ItemStatus.UNSOLD;
    }

    
    /**
     * 새로운 최고 입찰자와 현재가를 갱신함 
     *
     * @param bidder 새로운 최고 입찰자
     * @param bidAmount 새로운 최고 입찰 금액
     */
    public void updateHighestBidder(User bidder, Integer bidAmount) {

        if (this.status != ItemStatus.OPEN) {
            throw new IllegalStateException(
                    "진행 중인 경매만 최고 입찰자를 변경할 수 있습니다."
            );
        }

        if (bidder == null) {
            throw new IllegalArgumentException(
                    "최고 입찰자는 null일 수 없습니다."
            );
        }

        int minimumBidAmount = this.currentPrice + 100;

        if (bidAmount == null || bidAmount < minimumBidAmount) {
            throw new IllegalArgumentException(
                    "새 입찰 금액은 현재가보다 최소 100P 이상 높아야 합니다."
            );
        }

        this.highestBidder = bidder;
        this.currentPrice = bidAmount;
    }

    /**
     * 판매자가 경매를 직접 취소한다.
     *
     * 취소 가능 조건:
     * - 경매가 OPEN 상태여야 함
     * - 아직 입찰이 한 건도 없어야 함 (currentPrice == startPrice)
     *   입찰자가 있는 상품을 판매자가 임의로 취소해버리면 입찰자에게 불공평하기 때문
     */
    public void cancel() {
        if(this.status != ItemStatus.OPEN) {
            throw new IllegalStateException("진행 중인 경매만 취소할 수 있습니다.");
        }

        if(!this.currentPrice.equals(this.startPrice)){
            throw new IllegalStateException("입찰이 시작된 상품은 취소할 수 없습니다.");
        }

        this.status = ItemStatus.CANCELLED;
    }
}
