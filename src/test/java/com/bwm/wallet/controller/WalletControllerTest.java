package com.bwm.wallet.controller;

import static org.mockito.BDDMockito.given;
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
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import com.bwm.global.config.SecurityConfig;

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
}
