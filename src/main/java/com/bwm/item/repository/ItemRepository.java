package com.bwm.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bwm.item.entity.Item;

/**
 * Item 엔티티에 대한 DB 접근 인터페이스.
 *
 * Spring Data JPA가 애플리케이션 실행 시점에 이 인터페이스를 보고
 * save(), findById(), findAll() 같은 기본 CRUD 구현체를 자동으로 만들어서 등록해준다.
 * 우리가 직접 SQL/JPQL을 안 짜도 기본 CRUD는 이미 다 제공되는 것.
 *
 * 나중에 "판매자 id로 상품 목록 조회" 같은 게 필요해지면
 * 아래처럼 메서드 이름 규칙(쿼리 메서드)으로 선언만 하면 Spring이 알아서 구현해준다.
 *   List<Item> findBySeller_UserId(Long sellerId);
 *   List<Item> findByStatus(ItemStatus status);
 */

public interface ItemRepository extends JpaRepository<Item, Long> {

    

}
