package com.bwm.item.service;

import com.bwm.bid.dto.response.ItemBidHistoryResponse;
import com.bwm.bid.service.BidService;
import com.bwm.item.dto.request.ItemCreateRequest;
import com.bwm.item.dto.request.ItemSearchCondition;
import com.bwm.item.dto.request.ItemUpdateRequest;
import com.bwm.item.dto.response.ItemDetailResponse;
import com.bwm.item.dto.response.ItemResponse;
import com.bwm.item.dto.response.ItemSummaryResponse;
import com.bwm.item.entity.Item;
import com.bwm.item.entity.ItemImage;
import com.bwm.item.exception.ItemAccessDeniedException;
import com.bwm.item.exception.ItemNotFoundException;
import com.bwm.item.repository.ItemImageRepository;
import com.bwm.item.repository.ItemRepository;
import com.bwm.item.repository.ItemSpecification;
import com.bwm.user.entity.User;
import com.bwm.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ItemService의 실제 구현체.
 */
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemImageRepository itemImageRepository;
    private final BidService bidService;

    /**
     * 상품 등록.
     */
    @Override
    @Transactional
    public ItemResponse createItem(
            String sellerUuid,
            ItemCreateRequest request
    ) {
        User seller = getUserByUuid(sellerUuid);

        Item item = Item.create(
                seller,
                request.title(),
                request.category(),
                request.startPrice(),
                request.auctionEndAt(),
                request.description()
        );

        Item saved = itemRepository.save(item);

        return ItemResponse.from(saved);
    }

    /**
     * 상품 목록 및 검색.
     *
     * ItemSearchCondition에 들어온 조건만 동적으로 적용한다.
     * 조건을 보내지 않은 경우 ItemSpecification에서 기본적으로
     * OPEN 상태 조건을 적용한다.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ItemSummaryResponse> getItems(
            ItemSearchCondition condition,
            Pageable pageable
    ) {
        Specification<Item> specification =
                ItemSpecification.from(condition);

        return itemRepository
                .findAll(specification, pageable)
                .map(ItemSummaryResponse::from);
    }

    /**
     * 상품 상세 조회.
     */
    @Override
    @Transactional(readOnly = true)
    public ItemDetailResponse getItem(Integer itemId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ItemNotFoundException(
                                "존재하지 않는 상품입니다. id = " + itemId
                        )
                );

        List<String> imageUrls =
                itemImageRepository
                        .findAllByItem_ItemIdOrderByIsRepresentativeDescCreatedAtAsc(
                                itemId
                        )
                        .stream()
                        .map(ItemImage::getImageUrl)
                        .toList();

        List<ItemBidHistoryResponse> bidHistory =
                bidService.getItemBidHistory(itemId);

        return ItemDetailResponse.from(
                item,
                imageUrls,
                bidHistory
        );
    }

    /**
     * 로그인 사용자가 등록한 상품 목록 조회.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ItemSummaryResponse> getMyItems(
            String sellerUuid,
            Pageable pageable
    ) {
        Integer sellerId =
                getUserByUuid(sellerUuid).getUserId();

        return itemRepository
                .findAllBySeller_UserId(sellerId, pageable)
                .map(ItemSummaryResponse::from);
    }

    /**
     * 상품 수정.
     */
    @Override
    @Transactional
    public ItemResponse updateItem(
            Integer itemId,
            String sellerUuid,
            ItemUpdateRequest request
    ) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ItemNotFoundException(
                                "존재하지 않는 상품입니다. id = " + itemId
                        )
                );

        Integer sellerId =
                getUserByUuid(sellerUuid).getUserId();

        if (!item.getSeller().getUserId().equals(sellerId)) {
            throw new ItemAccessDeniedException(
                    "본인이 등록한 상품만 수정할 수 있습니다."
            );
        }

        item.update(
                request.title(),
                request.category(),
                request.description(),
                request.startPrice(),
                request.auctionEndAt()
        );

        return ItemResponse.from(item);
    }

    /**
     * 상품 취소.
     */
    @Override
    @Transactional
    public ItemResponse cancelItem(
            Integer itemId,
            String sellerUuid
    ) {
        Item item = itemRepository.findByIdForUpdate(itemId)
                .orElseThrow(() ->
                        new ItemNotFoundException(
                                "존재하지 않는 상품입니다. id = " + itemId
                        )
                );

        Integer sellerId =
                getUserByUuid(sellerUuid).getUserId();

        if (!item.getSeller().getUserId().equals(sellerId)) {
            throw new ItemAccessDeniedException(
                    "본인이 등록한 상품만 취소할 수 있습니다."
            );
        }

        item.cancel();

        return ItemResponse.from(item);
    }

    /**
     * JWT에서 전달받은 사용자 UUID로 사용자를 조회한다.
     */
    private User getUserByUuid(String uuid) {
        if (uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException(
                    "인증된 사용자 식별자가 없습니다."
            );
        }

        return userRepository.findByUserUuid(uuid)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "사용자를 찾을 수 없습니다. userUuid=" + uuid
                        )
                );
    }
}