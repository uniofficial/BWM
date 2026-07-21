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
 * 상품(Item) 관련 비즈니스 로직의 "계약(contract)"만 정의하는 인터페이스.
 * 실제 로직 구현은 ItemServiceImpl 클래스에 있다.
 *
 * - ItemController는 이 인터페이스 타입으로만 의존하고, 실제 구현체(ItemServiceImpl)가 뭔지 모른다.
 * - 나중에 구현을 바꾸거나(예: 캐싱 로직 추가한 새 구현체), 테스트에서 가짜 구현체(mock)로
 *   바꿔치기하고 싶을 때 ItemController 코드를 전혀 건드릴 필요가 없다.
 */

public interface ItemService {

    /**
     * 상품을 등록한다.
     *
     * 처리 흐름(ItemServiceImpl 기준):
     * 1) sellerEmail로 User(판매자) 조회 - 없으면 예외
     * 2) Item.create(...)로 엔티티 생성 (현재가=시작가, 상태=OPEN 자동 세팅)
     * 3) ItemRepository.save()로 저장
     * 4) 저장된 엔티티를 ItemResponse로 변환해서 반환
     *
     * @param sellerEmail 판매자의 로그인 이메일 (JWT subject, 컨트롤러가 Authentication에서 꺼내 그대로 넘겨줌)
     * @param request  상품 등록 요청 값 (제목/카테고리/시작가/마감시각/설명)
     * @return 등록된 상품 정보 (itemId 포함 - DB에 저장된 후의 결과)
     */
    ItemResponse createItem(String sellerEmail, ItemCreateRequest request);


    /**
     * 상품 목록을 페이지 단위로 조회한다.
     *
     * @param pageable 페이지 번호/크기/정렬 조건 (컨트롤러에서 쿼리 파라미터로 받아 그대로 전달)
     * @return 페이징된 상품 목록 (요약 정보만 포함)
     */
    Page<ItemSummaryResponse> getItems(Pageable pageable);


    /**
     * 상품 상세 정보를 조회한다.
     *
     * 처리 흐름(ItemServiceImpl 기준):
     * 1) itemId로 Item 조회 - 없으면 ItemNotFoundException
     * 2) 그 상품에 딸린 이미지 목록을 별도 조회
     * 3) 엔티티 두 개(Item, 이미지 URL 목록)를 하나의 DTO로 합쳐서 반환
     *
     * @param itemId 조회할 상품 id
     * @return 상품 상세 정보 (이미지 목록, 최고 입찰자 닉네임 포함)
     */
    ItemDetailResponse getItem(Integer itemId);

    /**
     * 검색 조건에 맞는 상품 목록을 페이지 단위로 조회한다.
     *
     * getItems(전체 목록)와 다른 점: 조건 필터링이 추가로 들어감.
     * 조건을 아무것도 안 넣고 호출하면(전부 null) 사실상 getItems와 동일한 결과가 나온다
     * (검색 조건이 하나도 안 걸리기 때문).
     *
     * @param condition 검색 조건 (제목 키워드/카테고리/상태/가격범위, 전부 선택사항이라 일부만 채워도 됨)
     * @param pageable 페이지 번호/크기/정렬 조건
     * @return 조건에 맞는 상품 목록 (요약 정보만 포함, 페이징 메타데이터 포함)
     */
    Page<ItemSummaryResponse> searchItems(ItemSearchCondition condition, Pageable pageable);

    /**
     * 특정 판매자(=로그인한 나)가 등록한 상품 목록을 페이지 단위로 조회한다.
     *
     * @param sellerEmail 조회할 판매자의 로그인 이메일 (JWT subject)
     * @param pageable 페이지 번호/크기/정렬 조건
     * @return 그 판매자가 등록한 상품 목록 (요약 정보만 포함)
     */
    Page<ItemSummaryResponse> getMyItems(String sellerEmail, Pageable pageable);

    /**
     * 상품 정보를 수정한다 (제목/카테고리/설명, 부분 수정).
     *
     * @param itemId 수정할 상품 id
     * @param sellerEmail 요청자의 로그인 이메일. 상품을 등록한 판매자 본인만 수정 가능
     * @param request 수정할 값들 (PATCH이므로 null인 필드는 그대로 유지됨)
     * @return 수정된 상품 정보
     */
    ItemResponse updateItem(Integer itemId, String sellerEmail, ItemUpdateRequest request);

    /**
     * 상품 등록을 취소한다 (상태를 CANCELLED로 변경).
     *
     * @param itemId 취소할 상품 id
     * @param sellerEmail 요청자의 로그인 이메일. 상품을 등록한 판매자 본인만 취소 가능
     * @return 취소된 상품 정보
     */
    ItemResponse cancelItem(Integer itemId, String sellerEmail);
}
