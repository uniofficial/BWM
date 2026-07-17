package com.bwm.item.entity;

import java.time.LocalDateTime;

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

@Entity
@Table(name = "item_image")
@Getter
@NoArgsConstructor
public class ItemImage {

    // PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;

    // 이 이미지가 속한 상품. FK(item_image.item_id -> item.item_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    // 이미지 파일 URL(or 경로) 최대 512자
    // 로컬 저장 방식 기준 "/images/item/{itemId}/{저장파일명}" 형태로 들어감
    @Column(name = "image_url", length = 512, nullable = false)
    private String imageUrl;

    // 대표 이미지 여부. 상품 하나당 이 값이 true인 이미지는 등록 시점 기준 1개 (요청에서 지정)
    @Column(name = "is_representative", nullable = false)
    private Boolean isRepresentative;

    // 등록 시각. 한 번 저장되면 수정 x
    @Column(name = "created_at", nullable = false, updatable = false) 
    private LocalDateTime createdAt;

    /*
     생성자는 private + Builder 조합으로 막아둠.
     * 외부에서는 아래 create() 정적 메서드로만 인스턴스를 만들 수 있다 (Item 엔티티와 동일한 패턴).
    */

     @Builder
     private ItemImage(Item item, String imageUrl, Boolean isRepresentative) {
        this.item = item;
        this.imageUrl = imageUrl;
        this.isRepresentative = isRepresentative;
     }

     /**
     * 이미지 등록용 정적 팩토리 메서드.
     *
     * @param item 이미지가 속할 상품 (이미 조회/검증된 엔티티여야 함)
     * @param imageUrl 저장된 이미지의 접근 URL
     * @param isRepresentative 대표 이미지 여부 (호출하는 쪽에서 배열 인덱스 비교 등으로 결정해서 넘김)
     * @return 아직 저장 전 상태의 ItemImage 인스턴스
     */

     public static ItemImage create(Item item, String imageUrl, boolean isRepresentative) {
        return ItemImage.builder()
                        .item(item)
                        .imageUrl(imageUrl)
                        .isRepresentative(isRepresentative)
                        .build();
     }

     // JPA 가 insert 쿼리를 날리기 직전 자동 호출되는 콜백. created_at을 애플리케이션 레벨에서 채워줌
     @PrePersist
     private void prePersist() {
        this.createdAt = LocalDateTime.now();
     }
}
