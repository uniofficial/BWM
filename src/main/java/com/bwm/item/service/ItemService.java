package com.bwm.item.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bwm.item.dto.request.ItemCreateRequest;
import com.bwm.item.dto.request.ItemSearchCondition;
import com.bwm.item.dto.request.ItemUpdateRequest;
import com.bwm.item.dto.response.ItemDetailResponse;
import com.bwm.item.dto.response.ItemResponse;
import com.bwm.item.dto.response.ItemSummaryResponse;

/**
 * 상품(Item) 관련 비즈니스 로직의 계약을 정의하는 인터페이스.
 */
public interface ItemService {

    /**
     * 상품을 등록한다.
     *
     * @param sellerEmail 판매자 식별자
     * @param request 상품 등록 요청
     * @return 등록된 상품 정보
     */
    ItemResponse createItem(
            String sellerEmail,
            ItemCreateRequest request
    );

    /**
     * 상품 목록을 검색 조건과 페이지 조건에 따라 조회한다.
     *
     * 검색 조건을 보내지 않으면 OPEN 상태 상품이 조회된다.
     *
     * @param condition 검색 조건
     * @param pageable 페이지 번호, 크기, 정렬 조건
     * @return 검색 조건에 맞는 상품 목록
     */
    Page<ItemSummaryResponse> getItems(
            ItemSearchCondition condition,
            Pageable pageable
    );

    /**
     * 상품 상세 정보를 조회한다.
     *
     * @param itemId 상품 ID
     * @return 상품 상세 정보
     */
    ItemDetailResponse getItem(Integer itemId);

    /**
     * 로그인 사용자가 등록한 상품 목록을 조회한다.
     *
     * @param sellerEmail 로그인 사용자 식별자
     * @param pageable 페이지 조건
     * @return 사용자가 등록한 상품 목록
     */
    Page<ItemSummaryResponse> getMyItems(
            String sellerEmail,
            Pageable pageable
    );

    /**
     * 상품 정보를 수정한다.
     *
     * @param itemId 상품 ID
     * @param sellerEmail 요청자 식별자
     * @param request 수정 요청
     * @return 수정된 상품 정보
     */
    ItemResponse updateItem(
            Integer itemId,
            String sellerEmail,
            ItemUpdateRequest request
    );

    /**
     * 상품 등록을 취소한다.
     *
     * @param itemId 상품 ID
     * @param sellerEmail 요청자 식별자
     * @return 취소된 상품 정보
     */
    ItemResponse cancelItem(
            Integer itemId,
            String sellerEmail
    );
}