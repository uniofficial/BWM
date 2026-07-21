package com.bwm.bid.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import com.bwm.bid.dto.request.BidCreateRequest;
import com.bwm.bid.dto.response.BidResponse;
import com.bwm.support.ControllerTestSupport;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import com.bwm.global.config.SecurityConfig;

@WebMvcTest(BidController.class)
@Import(SecurityConfig.class)
public class BidControllerTest extends ControllerTestSupport {

    @Test
    @WithMockUser(username = "uuid-1234")
    @DisplayName("입찰 성공 시 200 OK를 반환한다.")
    void createBid_success() throws Exception {
        // given
        Integer itemId = 1;
        String jsonRequest = "{\"bidAmount\": 20000}";

        BidResponse response = new BidResponse(1, itemId, "tester", 20000, LocalDateTime.now());

        given(bidService.createBid(eq(itemId), eq("uuid-1234"), any(BidCreateRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/items/{itemId}/bids", itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bidId").value(1))
                .andExpect(jsonPath("$.bidAmount").value(20000));
    }

    @Test
    @WithMockUser(username = "uuid-1234")
    @DisplayName("입찰 시 입찰 금액이 없으면 400 예외가 발생한다.")
    void createBid_fail_validation() throws Exception {
        // given
        Integer itemId = 1;
        String jsonRequest = "{}";

        // when & then
        mockMvc.perform(post("/api/items/{itemId}/bids", itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("상품 입찰 내역 조회 시 200 OK를 반환한다.")
    void getItemBidHistory_success() throws Exception {
        // given
        Integer itemId = 1;

        given(bidService.getItemBidHistory(itemId)).willReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/items/{itemId}/bids", itemId))
                .andExpect(status().isOk());
    }
}
