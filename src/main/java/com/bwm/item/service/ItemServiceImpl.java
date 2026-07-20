package com.bwm.item.service;

import com.bwm.item.dto.request.ItemCreateRequest;
import com.bwm.item.dto.request.ItemSearchCondition;
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
 *
 * @Service : 이 클래스를 Spring이 관리하는 Bean으로 등록. ItemController가 생성자 주입으로
 *            ItemService 타입을 요청하면, 스프링이 이 클래스의 인스턴스를 넣어준다.
 * @RequiredArgsConstructor : final 필드(itemRepository, userRepository)를 매개변수로 받는
 *            생성자를 Lombok이 자동 생성. 이 생성자를 스프링이 보고 의존성을 주입한다.
 */

@Service
@RequiredArgsConstructor    
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository; // TODO: 인증 파트에서 만든 Repository로 교체
    private final ItemImageRepository itemImageRepository;


    @Override
    @Transactional
    public ItemResponse createItem(Integer sellerId, ItemCreateRequest request){

        // #1. 판매자 조회 - 존재하지 않는 유저 id가 넘어오면 예외 발생 (404)
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ItemNotFoundException("존재하지 않는 사용자입니다. id = " + sellerId));

        // #2. Item 엔티티 생성
        Item item = Item.create(
                seller,
                request.title(),
                request.category(),
                request.startPrice(),
                request.auctionEndAt(),
                request.description()
        );

        // #3. DB save
        Item saved = itemRepository.save(item);

        // #4. 엔티티를 그대로 반환하지 않고 DTO로 변환해 반환
        return ItemResponse.from(saved);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ItemSummaryResponse> getItems(Pageable pageable) {
        // ItemRepository는 JpaRepository를 상속하고 있어서 findAll(Pageable)이 기본 제공됨.
        // 페이지 단위로 조회한 Item 엔티티들을 ItemSummaryResponse로 변환해서 반환.
        return itemRepository.findAll(pageable).map(ItemSummaryResponse::from);
    }

    @Override
    @Transactional(readOnly = true) // 조회 전용 트랜잭션. 데이터 변경이 없으므로 읽기 최적화 힌트 제공
    public ItemDetailResponse getItem(Integer itemId){

        // #1. 상품 조회 - 존재하지 않는 itemId면 예외
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("존재하지 않는 상품입니다. id = " + itemId));

        // #2. 이 상품에 등록된 이미지들을 따로 조회(Item 엔티티엔 이미지 목록을 직접 들고있지 않음)
        /// 대표 이미지가 맨 앞에 오도록 정렬된 상태로 가져와서 URL 문자열만 뽑아 리스트로 변환
        List<String> imageUrls = itemImageRepository.findAllByItem_ItemIdOrderByIsRepresentativeDescCreatedAtAsc(itemId)
                                                    .stream()
                                                    .map(ItemImage::getImageUrl)
                                                    .toList();

         // #3. Item 엔티티 + 이미지 URL 목록을 하나의 응답 DTO로 조립해 반환
         return ItemDetailResponse.from(item,imageUrls);
    }

    @Override
    @Transactional(readOnly = true) // 조회만 하고 데이터는 안 바꾸는 API라 읽기 전용 트랜잭션으로 최적화
    public Page<ItemSummaryResponse> searchItems(ItemSearchCondition condition, Pageable pageable) {

        // #1. DTO(검색 조건)을  실제 JPA가 이해하는 WHERE 절 형태로 변환
        Specification<Item> spec = ItemSpecification.from(condition);

        // #2. JpaSpecificationExecutor가 제공하는 findAll(spec, pageable) 로
        // "동적 조건 필터링 + 페이징" 을 한 번의 쿼리로 처리하고
        // 결과로 나온 Item 엔티티들을 곧바로 ItemSummaryResponse 로 변환
        return itemRepository.findAll(spec,pageable).map(ItemSummaryResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemSummaryResponse> getMyItems(Integer sellerId, Pageable pageable) {
        // getItems(전체 목록)와 거의 같은 흐름인데, seller_id 조건만 하나 더 걸려있는 버전
        return itemRepository.findAllBySeller_UserId(sellerId, pageable).map(ItemSummaryResponse::from);
    }

    @Override
    @Transactional
    public ItemResponse cancelItem(Integer itemId, Integer sellerId) {

        // #1. 상품 조회 - 존재하지 않는 itemId면 예외
        Item item = itemRepository.findByIdForUpdate(itemId)
                                  .orElseThrow(() -> new ItemNotFoundException("존재하지 않는 상품입니다. id = " + itemId));

        // #2. 권한 체크 - 본인이 등록한 상품만 취소 가능
        if (!item.getSeller().getUserId().equals(sellerId)) {
            throw new ItemAccessDeniedException("본인이 등록한 상품만 취소할 수 있습니다.");
        }

        // #3. 도메인 메서드에 위임 - 상태/입찰 여부 검증 + 실제  상태 변경은 Item 엔티티가 책임짐
        item.cancel();

        // #4. save() 호출 안 해도 됨 - 영속 상태 엔티티라 커밋 기점에 JPA가 변경 감지해서 자동 UPDATE
        return ItemResponse.from(item);
    }

}
