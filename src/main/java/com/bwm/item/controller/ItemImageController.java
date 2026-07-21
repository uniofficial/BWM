package com.bwm.item.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bwm.item.dto.response.ItemImageResponse;
import com.bwm.item.exception.ItemAccessDeniedException;
import com.bwm.item.exception.ItemNotFoundException;
import com.bwm.item.service.ItemImageService;
import com.bwm.user.entity.User;
import com.bwm.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
    private final UserRepository userRepository;
    /**
     * 상품 이미지 등록 API.
     *
     * @param authentication JWT 인증 정보. SecurityContext에서 로그인한 판매자의 email(subject)을 꺼내
     *        UserRepository로 user_id를 조회한다.
     * @param itemId 이미지를 등록할 상품 id (URL 경로 변수)
     * @param images 업로드할 이미지 파일들. multipart/form-data로 같은 필드명("images")에 여러 개 담아서 보냄
     * @param representativeIndex images 중 대표 이미지의 인덱스 (0-based, 기본값 0)
     * @return 201 Created + 등록된 이미지 목록(ItemImageResponse 리스트)
     */

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<List<ItemImageResponse>> uploadImages(
        Authentication authentication,

        @PathVariable
        Integer itemId,

        @RequestParam("images")
        List<MultipartFile> images,

        @RequestParam(value = "representativeIndex", required = false, defaultValue = "0")
        Integer representativeIndex) {
            Integer sellerId = resolveUserId(authentication);

            List<ItemImageResponse> response = itemImageService.uploadItemImages(itemId, sellerId, images, representativeIndex);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        private Integer resolveUserId(Authentication authentication){
            return userRepository.findByEmail(authentication.getName())
                                 .map(User::getUserId)
                                 .orElseThrow(() -> new IllegalArgumentException("인증된 사용자를 찾을 수 없습니다."));
        }
        // representativeIndex 범위 오류 등 잘못된 요청은 400으로 응답

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        // 존재하지 않는 상품 id로 요청하면 404로 응답
        @ExceptionHandler(ItemNotFoundException.class)
        public ResponseEntity<String> handleItemNotFoundException(ItemNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        // 본인 이미지 아닌데 이미지를 추가하려 하면 403으로 응답
        @ExceptionHandler(ItemAccessDeniedException.class)
        public ResponseEntity<String> handleItemAccessDeniedException(ItemAccessDeniedException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    
    
    
}
