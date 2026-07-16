package com.bwm.item.service;

import com.bwm.item.dto.request.ItemCreateRequest;
import com.bwm.item.dto.response.ItemResponse;
import com.bwm.item.entity.Item;
import com.bwm.item.exception.ItemNotFoundException;
import com.bwm.item.repository.ItemRepository;
import com.bwm.user.User;            // TODO: 실제 패키지 경로에 맞게 수정 (인증 파트 담당자에게 확인)
import com.bwm.user.UserRepository;  // TODO: 인증 파트에서 만든 Repository로 교체
import lombok.RequiredArgsConstructor;
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

    @Override
    @Transactional
    public ItemResponse createItem(Long sellerId, ItemCreateRequest request){

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
}
