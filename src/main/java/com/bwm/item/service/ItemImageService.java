package com.bwm.item.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.bwm.item.dto.response.ItemImageResponse;

/**
 * 상품 이미지(ItemImage) 관련 비즈니스 로직의 "계약(contract)"만 정의하는 인터페이스.
 * 실제 로직 구현은 ItemImageServiceImpl 클래스에 있다.
 */

public interface ItemImageService {

    /**
     * 상품 이미지를 등록한다 (여러 장 한 번에 처리).
     *
     * 처리 흐름(ItemImageServiceImpl 기준):
     * 1) 요청 값 검증 - 이미지가 1개 이상인지, representativeIndex가 범위 안인지
     * 2) itemId로 Item 조회 - 없으면 ItemNotFoundException
     * 3) 요청자(sellerId)가 그 상품의 실제 판매자인지 확인 - 아니면 ItemAccessDeniedException
     * 4) 이미지 파일들을 순서대로 로컬 디스크에 저장하고, 인덱스가 representativeIndex와 같은 것만 대표로 표시
     * 5) 저장된 이미지들을 DTO로 변환해서 반환
     *
     * @param itemId 이미지가 속할 상품 id
     * @param sellerId 요청자 id.
     *                 (인증 파트 연동 전이라 지금은 컨트롤러가 X-USER-ID 헤더에서 꺼내 넘겨줌 - ItemService와 동일한 임시 방식)
     * @param images 업로드할 이미지 파일 목록 (1개 이상, 순서 = 등록 순서)
     * @param representativeIndex images 중 대표 이미지의 인덱스 (0-based). null이면 0번째를 대표로 지정
     * @return 등록된 이미지 목록 (요청에 넘긴 순서와 동일)
     */

    List<ItemImageResponse> uploadItemImages(Integer itemId, Integer sellerId, List<MultipartFile> images, Integer representativeIndex);
}
