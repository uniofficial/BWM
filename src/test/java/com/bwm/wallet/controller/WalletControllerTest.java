package com.bwm.wallet.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import com.bwm.support.ControllerTestSupport;
import com.bwm.wallet.dto.WalletResponseDto;
import com.bwm.wallet.dto.PointChargeRequestDto;
import com.bwm.wallet.dto.WalletChargeRequestResponseDto;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import com.bwm.global.config.SecurityConfig;

import java.util.List;

@WebMvcTest(WalletController.class)
@Import(SecurityConfig.class)
public class WalletControllerTest extends ControllerTestSupport {

    @Test
    @WithMockUser(username = "uuid-1234")
    @DisplayName("내 지갑 잔액 조회 시 200 OK를 반환한다.")
    void getMyWallet_success() throws Exception {
        // given
        WalletResponseDto responseDto = new WalletResponseDto(50000);
        given(walletService.getMyWallet("uuid-1234")).willReturn(responseDto);

        // when & then
        mockMvc.perform(get("/api/wallets/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.balance").value(50000));
    }

    @Test
    @WithMockUser(username = "uuid-1234")
    @DisplayName("포인트 내역 조회 시 200 OK를 반환한다.")
    void getMyWalletHistories_success() throws Exception {
        // given
        java.util.List<com.bwm.wallet.dto.WalletHistoryResponseDto> histories = java.util.Collections.emptyList();
        given(walletHistoryService.getMyHistory("uuid-1234")).willReturn(histories);

        // when & then
        mockMvc.perform(get("/api/wallets/me/histories"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "uuid-1234")
    @DisplayName("유저 포인트 충전 요청 시 200 OK를 반환한다.")
    void requestPointCharge_success() throws Exception {
        // given
        PointChargeRequestDto requestDto = new PointChargeRequestDto(50000);
        WalletChargeRequestResponseDto responseDto = WalletChargeRequestResponseDto.builder()
                .chargeRequestId(1)
                .userId(1)
                .userEmail("uuid-1234")
                .amount(50000)
                .status("PENDING")
                .createdAt(java.time.LocalDateTime.now())
                .build();

        given(walletService.requestPointCharge(eq("uuid-1234"), eq(50000))).willReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/wallets/charge/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.amount").value(50000));
    }

    @Test
    @WithMockUser(username = "uuid-1234")
    @DisplayName("유저 충전 요청 목록 조회 시 200 OK를 반환한다.")
    void getMyChargeRequests_success() throws Exception {
        // given
        List<WalletChargeRequestResponseDto> responses = List.of(
                WalletChargeRequestResponseDto.builder()
                        .chargeRequestId(1)
                        .userId(1)
                        .userEmail("uuid-1234")
                        .amount(50000)
                        .status("PENDING")
                        .createdAt(java.time.LocalDateTime.now())
                        .build()
        );

        given(walletService.getMyChargeRequests("uuid-1234")).willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/wallets/charge/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].amount").value(50000));
    }
}
