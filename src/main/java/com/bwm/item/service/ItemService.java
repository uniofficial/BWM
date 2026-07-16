package com.bwm.item.service;

import com.bwm.item.dto.request.ItemCreateRequest;
import com.bwm.item.dto.response.ItemResponse;

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
     * 1) sellerId로 User(판매자) 조회 - 없으면 예외
     * 2) Item.create(...)로 엔티티 생성 (현재가=시작가, 상태=OPEN 자동 세팅)
     * 3) ItemRepository.save()로 저장
     * 4) 저장된 엔티티를 ItemResponse로 변환해서 반환
     *
     * @param sellerId 판매자의 user_id.
     *                 (인증 파트 연동 전이라 지금은 컨트롤러가 X-USER-ID 헤더에서 꺼내 넘겨줌.
     *                  인증 완료되면 로그인한 사용자의 id가 자동으로 여기 들어오게 바뀔 예정)
     * @param request  상품 등록 요청 값 (제목/카테고리/시작가/마감시각/설명)
     * @return 등록된 상품 정보 (itemId 포함 - DB에 저장된 후의 결과)
     */

    ItemResponse createItem(Long sellerId, ItemCreateRequest request);  
}
