package com.bwm.item.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bwm.item.dto.response.ItemImageResponse;
import com.bwm.item.entity.Item;
import com.bwm.item.entity.ItemImage;
import com.bwm.item.entity.ItemStatus;
import com.bwm.item.exception.ItemAccessDeniedException;
import com.bwm.item.exception.ItemNotFoundException;
import com.bwm.item.exception.ItemStateConflictException;
import com.bwm.item.repository.ItemImageRepository;
import com.bwm.item.repository.ItemRepository;
import com.bwm.user.entity.User;
import com.bwm.user.repository.UserRepository;
import com.bwm.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

/**
 * ItemImageService의 실제 구현체.
 * 이미지 파일 자체는 별도 스토리지(S3 등) 없이 로컬 디스크(uploadDir)에 저장하고,
 * DB에는 그 파일에 접근할 수 있는 URL만 저장한다.
 */
@Service
@RequiredArgsConstructor
public class ItemImageServiceImpl implements ItemImageService {

    private final ItemRepository itemRepository;
    private final ItemImageRepository itemImageRepository;
    private final UserRepository userRepository;

    // application.properties의 file.upload-dir 값 주입 (예: uploads/items)
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public List<ItemImageResponse> uploadItemImages(
            Integer itemId,
            String sellerUuid,
            List<MultipartFile> images,
            Integer representativeIndex) {

        // #1. 이미지가 하나도 없으면 처리할게 없으므로 예외
        if (images == null || images.isEmpty()) {
            throw new IllegalArgumentException("업로드할 이미지가 없습니다.");
        }

        // representativeIndex를 안보내면(null) 첫번째 이미지를 대표 취급
        int repIndex = (representativeIndex == null) ? 0 : representativeIndex;
        if (repIndex < 0 || repIndex >= images.size()) {
            throw new IllegalArgumentException("representativeIndex가 이미지 목록 범위를 벗어났습니다.");
        }

        // #2. 상품 조회 - 존재하지 않는 itemId가 넘어오면 예외
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new ItemNotFoundException(ErrorCode.ITEM_NOT_FOUND, "존재하지 않는 상품입니다. id = " + itemId);
        });

        // #3. 권한 체크 - 상품을 등록한 판매자 본인만 이미지 추가 가능
        Integer sellerId = getUserByUuid(sellerUuid).getUserId();
        if (!item.getSeller().getUserId().equals(sellerId)) {
            throw new ItemAccessDeniedException(ErrorCode.ITEM_ACCESS_DENIED, "본인이 등록한 상품에만 이미지를 추가할 수 있습니다.");
        }

        // #3-1. 상태 체크 - 진행 중(OPEN)인 상품에만 이미지 추가 가능
        if (item.getStatus() != ItemStatus.OPEN) {
            throw new ItemStateConflictException(ErrorCode.ITEM_STATE_CONFLICT, "진행 중인 상품에만 이미지를 추가할 수 있습니다.");
        }

        List<ItemImage> savedImages = new ArrayList<>();

        // #4. 이미지 파일들을 순서대로 디스크에 저장 + DB에 저장
        // i == repIndex인 것만 대표 이미지(true)로 표시하고 나머지는 false
        for (int i = 0; i < images.size(); i++) {
            MultipartFile file = images.get(i);
            if (file.isEmpty()) {
                throw new IllegalArgumentException(" 빈 파일은 업로드할 수 없습니다");
            }

            String imageUrl = storeFile(itemId, file);
            ItemImage itemImage = ItemImage.create(item, imageUrl, i == repIndex);
            savedImages.add(itemImageRepository.save(itemImage));
        }

        // #5. 엔티티를 그대로 반환하지 않고 DTO 목록으로 변환해 반환
        return savedImages.stream()
                .map(ItemImageResponse::from)
                .toList();
    }

    /**
     * 업로드된 파일 하나를 "uploadDir/itemId/uuid.확장자" 경로에 저장하고,
     * 정적 리소스 핸들러(StaticResourceConfig)가 서빙해줄 URL을 만들어서 반환한다.
     *
     * 원본 파일명을 그대로 쓰지 않고 UUID로 바꾸는 이유: 같은 이름 파일이 덮어써지는 것 방지 + 경로 조작(path traversal)
     * 방지
     */

    private String storeFile(Integer itemId, MultipartFile file) {
        try {
            // 원본 파일명에서 확장자만 추출
            String originalFilename = file.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String storedFilename = UUID.randomUUID() + ext;

            // 상품별로 디렉토리 나눠서 저장
            Path itemDir = Paths.get(uploadDir, String.valueOf(itemId));
            Files.createDirectories(itemDir);
            Path targetPath = itemDir.resolve(storedFilename);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // StaticResourceConfig에서 "/images/items/**" -> "file:{uploadDir}/" 로 매핑해뒀으므로
            // 이 경로 그대로가 외부에서 접근 가능한 URL이 된다.
            return "/images/items/" + itemId + "/" + storedFilename;
        } catch (IOException e) {
            throw new IllegalStateException("이미지 저장 중 오류가 발생했습니다.", e);

        }

    }

    /**
     * 이메일을 기준으로 로그인 사용자 엔티티를 조회한다.
     *
     * JWT subject에는 로그인 사용자의 이메일이 저장되어 있으므로
     * SecurityContext에서 얻은 이메일을 이 메서드에 전달한다.
     */
    private User getUserByUuid(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException("인증된 사용자 식별자가 없습니다.");
        }

        return userRepository.findByUserUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. uuid=" + uuid));
    }
}
