package com.bwm.item.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.bwm.item.dto.request.ItemSearchCondition;
import com.bwm.item.entity.Item;
import com.bwm.item.entity.ItemStatus;

/**
 * ItemSearchCondition의 각 필드를 동적 WHERE 조건(Specification)으로 변환하는 유틸리티.
 *
 * 왜 쿼리 메서드 이름 방식(findByTitleContainingAndCategory...) 대신 이 방식을 쓰냐면:
 * 검색 조건 5개가 전부 "있을 수도, 없을 수도" 있는 조합이라, 쿼리 메서드로 다 커버하려면
 * 이론상 2^5 = 32개 메서드 조합이 필요하다. Specification은 조건을 하나씩 만들어서
 * "있는 것만" 런타임에 AND로 이어붙이는 방식이라 이런 조합 폭발을 피할 수 있다.
 */

public class ItemSpecification {
    // 정적 메서드만 제공하는 유틸리티 클래스. new ItemSpecification() 하는 걸 막으려고
    // 생성자를 private으로 숨김 (Item, ItemImage 엔티티의 private 생성자와는 다른 이유:
    // 여긴 상태를 가진 객체를 만드는 게 아니라 그냥 정적 메서드 모음이라서 인스턴스 자체가 필요 없음)
    private ItemSpecification() {
        // 정적 메서드만 제공하는 유틸리티 클래스라 인스턴스화 막아둠
    }

    public static Specification<Item> from(ItemSearchCondition condition) {
        // 지금 쓰는 Spring Data JPA 버전은 .and(null)을 호출하면
        // "Other specification must not be null" 예외를 던진다 (예전엔 null-safe였는데 동작이 바뀜).
        // 그래서 .and()로 체이닝하는 대신, null이 아닌 조건만 리스트에 모아서
        // Specification.allOf(...)로 한 번에 AND 조합한다. 리스트가 비어있으면
        // allOf가 자동으로 "조건 없음(전체 매칭)" Specification을 반환해준다.
        List<Specification<Item>> specs = new ArrayList<>();

        addIfPresent(specs, keywordContains(condition.keyword()));
        addIfPresent(specs, categoryEquals(condition.category()));
        addIfPresent(specs, statusEquals(condition.status()));
        addIfPresent(specs, priceGreaterThanOrEqual(condition.minPrice()));
        addIfPresent(specs, priceLessThanOrEqual(condition.maxPrice()));

        return Specification.allOf(specs);
    }

    private static void addIfPresent(List<Specification<Item>> specs, Specification<Item> spec) {
        if (spec != null) {
            specs.add(spec);
        }
    }

// Specification<Item>의 람다 파라미터 3개 의미:
    //   root  - "Item 테이블"을 가리키는 참조. root.get("필드명")으로 컬럼에 접근
    //   query - 생성 중인 쿼리 자체 (여기선 안 씀, distinct/orderBy 등 커스텀할 때 필요)
    //   cb    - CriteriaBuilder. like/equal/greaterThan 같은 조건 연산자를 만들어주는 도구


    private static Specification<Item> keywordContains(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null; // 검색어 없으면 이 조건 자체를 안 건다.
        }
        return (root, query, cb) -> cb.like(root.get("title"), "%" + keyword + "%");

    }

    private static Specification<Item> categoryEquals(String category) {
        if (category == null || category.isBlank()){
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }

    private static Specification<Item> statusEquals(ItemStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    private static Specification<Item> priceGreaterThanOrEqual(Integer minPrice) {
        if(minPrice == null){
            return null;
        }

        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("currentPrice"), minPrice);
    }

    private static Specification<Item> priceLessThanOrEqual(Integer maxPrice) {
        if(maxPrice == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("currentPrice"), maxPrice);
    }
}
