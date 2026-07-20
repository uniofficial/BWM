package com.bwm.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.bwm.item.entity.ItemImage;

  /**
     * 특정 상품에 등록된 이미지 목록을 조회합니다.
     * 대표 이미지(isRepresentative=true)가 먼저 오도록 정렬합니다.
     *
     * "Item_ItemId"는 ItemImage.item(연관관계 필드) -> Item.itemId(그 안의 필드)를
     * 타고 들어가서 조건을 거는 Spring Data JPA의 쿼리 메서드 이름 규칙이다.
     * 즉 SQL로 치면 WHERE item_id = ? ORDER BY is_representative DESC, created_at ASC
     */

public interface ItemImageRepository extends JpaRepository<ItemImage, Integer> {
    List<ItemImage> findAllByItem_ItemIdOrderByIsRepresentativeDescCreatedAtAsc(Integer itemId);

}
