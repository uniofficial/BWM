package com.bwm.auction.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import com.bwm.global.config.SecurityConfig;
import org.springframework.security.test.context.support.WithMockUser;

import com.bwm.auction.dto.AuctionCloseResponse;
import com.bwm.support.ControllerTestSupport;

@WebMvcTest(AuctionController.class)
@Import(SecurityConfig.class)
public class AuctionControllerTest extends ControllerTestSupport {

    @Test
    @WithMockUser(username = "uuid-1234")
    @DisplayName("경매 즉시 마감 성공 시 200 OK를 반환한다.")
    void closeAuction_success() throws Exception {
        // given
        Integer itemId = 1;
        AuctionCloseResponse response = new AuctionCloseResponse(itemId, "SOLD", "bidder", 30000);

        given(auctionService.closeAuction(eq(itemId), eq("uuid-1234"))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/items/{itemId}/close", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(1))
                .andExpect(jsonPath("$.status").value("SOLD"))
                .andExpect(jsonPath("$.currentPrice").value(30000));
    }

    @Test
    @WithMockUser(username = "uuid-1234")
    @DisplayName("내가 낙찰받은 경매 목록 조회 시 200 OK를 반환한다.")
    void getMyWinningAuctions_success() throws Exception {
        // given
        given(auctionService.getMyWinningAuctions("uuid-1234")).willReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/auctions/wins/me"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "uuid-1234")
    @DisplayName("내가 판매 완료한 경매 목록 조회 시 200 OK를 반환한다.")
    void getMySoldAuctions_success() throws Exception {
        // given
        given(auctionService.getMySoldAuctions("uuid-1234")).willReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/auctions/sold/me"))
                .andExpect(status().isOk());
    }
}
