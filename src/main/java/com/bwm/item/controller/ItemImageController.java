package com.bwm.item.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bwm.item.dto.response.ItemImageResponse;
import com.bwm.item.service.ItemImageService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * 상품 이미지(ItemImage) 관련 REST API 엔드 포인트
 * "상품 이미지 등록" 하나만 구현
 */

@RestController
@RequestMapping("/api/items/{itemId}/images")
@RequiredArgsConstructor
public class ItemImageController {

    private final ItemImageService itemImageService;
    /**
     * 상품 이미지 등록 API.
     *
     * @param authentication JWT 인증 정보. email(subject)을 그대로 서비스에 넘기면
     *        서비스가 내부에서 로그인 사용자를 조회한다.
     * @param itemId 이미지를 등록할 상품 id (URL 경로 변수)
     * @param images 업로드할 이미지 파일들. multipart/form-data로 같은 필드명("images")에 여러 개 담아서 보냄
     * @param representativeIndex images 중 대표 이미지의 인덱스 (0-based, 기본값 0)
     * @return 201 Created + 등록된 이미지 목록(ItemImageResponse 리스트)
     */

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<List<ItemImageResponse>> uploadImages(
        Authentication authentication,

        @PathVariable("itemId") Integer itemId,

        @RequestParam("images")
        List<MultipartFile> images,

        @RequestParam(value = "representativeIndex", required = false, defaultValue = "0")
        Integer representativeIndex) {
            List<ItemImageResponse> response = itemImageService.uploadItemImages(itemId, authentication.getName(), images, representativeIndex);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        // 예외 처리는 GlobalExceptionHandler(전역)에서 일괄 처리한다.
}