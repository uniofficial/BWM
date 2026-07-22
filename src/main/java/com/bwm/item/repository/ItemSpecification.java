package com.bwm.item.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.bwm.item.dto.request.ItemSearchCondition;
import com.bwm.item.entity.Item;
import com.bwm.item.entity.ItemStatus;

/**
 * ItemSearchCondition을 JPA Specification으로 변환하는 유틸리티 클래스.
 *
 * 전달된 검색 조건만 동적으로 WHERE 절에 추가한다.
 */
public final class ItemSpecification {

    private ItemSpecification() {
    }

    /**
     * 검색 조건을 Specification으로 변환한다.
     *
     * status가 전달되지 않은 경우 기본적으로 OPEN 상태만 조회한다.
     */
    public static Specification<Item> from(
            ItemSearchCondition condition
    ) {
        List<Specification<Item>> specifications =
                new ArrayList<>();

        /*
         * @ModelAttribute를 사용하면 일반적으로 빈 조건 객체가 만들어지지만,
         * 서비스 단위 테스트나 다른 내부 호출에서 null이 전달될 가능성까지 방어한다.
         */
        if (condition == null) {
            specifications.add(statusEquals(ItemStatus.OPEN));
            return Specification.allOf(specifications);
        }

        addIfPresent(
                specifications,
                keywordContains(condition.keyword())
        );

        addIfPresent(
                specifications,
                categoryEquals(condition.category())
        );

        /*
         * status를 명시하지 않으면 목록 화면의 기본 정책인 OPEN을 적용한다.
         * status를 명시하면 OPEN 대신 전달받은 상태를 적용한다.
         */
        ItemStatus status = condition.status() == null
                ? ItemStatus.OPEN
                : condition.status();

        addIfPresent(
                specifications,
                statusEquals(status)
        );

        addIfPresent(
                specifications,
                priceGreaterThanOrEqual(condition.minPrice())
        );

        addIfPresent(
                specifications,
                priceLessThanOrEqual(condition.maxPrice())
        );

        return Specification.allOf(specifications);
    }

    /**
     * null이 아닌 조건만 목록에 추가한다.
     */
    private static void addIfPresent(
            List<Specification<Item>> specifications,
            Specification<Item> specification
    ) {
        if (specification != null) {
            specifications.add(specification);
        }
    }

    /**
     * 상품명에 검색어가 포함되는지 검사한다.
     *
     * 영문 검색은 대소문자를 구분하지 않도록 양쪽 모두 소문자로 변환한다.
     */
    private static Specification<Item> keywordContains(
            String keyword
    ) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        String normalizedKeyword =
                keyword.trim().toLowerCase();

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + normalizedKeyword + "%"
                );
    }

    /**
     * 카테고리가 정확히 일치하는지 검사한다.
     */
    private static Specification<Item> categoryEquals(
            String category
    ) {
        if (category == null || category.isBlank()) {
            return null;
        }

        String normalizedCategory = category.trim();

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("category"),
                        normalizedCategory
                );
    }

    /**
     * 상품 상태가 일치하는지 검사한다.
     */
    private static Specification<Item> statusEquals(
            ItemStatus status
    ) {
        if (status == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("status"),
                        status
                );
    }

    /**
     * 현재가가 최소 가격 이상인지 검사한다.
     */
    private static Specification<Item> priceGreaterThanOrEqual(
            Integer minPrice
    ) {
        if (minPrice == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("currentPrice"),
                        minPrice
                );
    }

    /**
     * 현재가가 최대 가격 이하인지 검사한다.
     */
    private static Specification<Item> priceLessThanOrEqual(
            Integer maxPrice
    ) {
        if (maxPrice == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("currentPrice"),
                        maxPrice
                );
    }
}