package com.bwm.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bwm.item.entity.ItemImage;

/**
 * ItemImage 엔티티에 대한 DB 접근 인터페이스.
 * 지금은 등록(save)만 쓰지만, 나중에 "상품별 이미지 목록 조회" 같은 게 필요해지면
 * List<ItemImage> findByItem_ItemId(Integer itemId); 형태로 쿼리 메서드를 추가하면 된다.
 */

public interface ItemImageRepository extends JpaRepository<ItemImage, Integer> {

}
