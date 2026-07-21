package com.bwm.item.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import com.bwm.item.dto.request.ItemCreateRequest;
import com.bwm.item.dto.response.ItemResponse;
import com.bwm.item.entity.ItemStatus;
import com.bwm.support.ControllerTestSupport;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import com.bwm.global.config.SecurityConfig;

@WebMvcTest(ItemController.class)
@Import(SecurityConfig.class)
public class ItemControllerTest extends ControllerTestSupport {

    @Test
    @WithMockUser(username = "uuid-1234")
    @DisplayName("상품 등록 성공 시 201 Created를 반환한다.")
    void createItem_success() throws Exception {
        // given
        String futureTime = LocalDateTime.now().plusDays(1).toString();
        String jsonRequest = String.format(
            "{\"title\":\"Test Item\", \"category\":\"ELECTRONICS\", \"startPrice\":10000, \"auctionEndAt\":\"%s\"}",
            futureTime
        );

        ItemResponse response = new ItemResponse(1, "seller", "Test Item", "ELECTRONICS", 10000, 10000, LocalDateTime.now().plusDays(1), ItemStatus.OPEN, LocalDateTime.now(), "description");

        given(itemService.createItem(eq("uuid-1234"), any(ItemCreateRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemId").value(1))
                .andExpect(jsonPath("$.title").value("Test Item"));
    }

    @Test
    @WithMockUser(username = "uuid-1234")
    @DisplayName("상품 등록 시 과거 마감시간을 입력하면 400 예외가 발생한다.")
    void createItem_fail_past_date() throws Exception {
        // given
        String pastTime = LocalDateTime.now().minusDays(1).toString();
        String jsonRequest = String.format(
            "{\"title\":\"Test Item\", \"category\":\"ELECTRONICS\", \"startPrice\":10000, \"auctionEndAt\":\"%s\"}",
            pastTime
        );

        // when & then
        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("상품 상세 조회 시 200 OK를 반환한다.")
    void getItem_success() throws Exception {
        // given
        Integer itemId = 1;

        // when & then
        // itemService.getItem(itemId)를 Mocking해도 되지만, 기본적으로 isOk() 여부 체크
        mockMvc.perform(get("/api/items/{itemId}", itemId))
                .andExpect(status().isOk());
    }
}
