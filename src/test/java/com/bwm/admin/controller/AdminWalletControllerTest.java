package com.bwm.admin.controller;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import com.bwm.support.ControllerTestSupport;

@WebMvcTest(AdminWalletController.class)
public class AdminWalletControllerTest extends ControllerTestSupport {

    @Test
    @WithMockUser(username = "admin-uuid", roles = "ADMIN")
    @DisplayName("관리자 권한으로 특정 유저 포인트 충전 성공 시 200 OK를 반환한다.")
    void chargeUserPoint_success() throws Exception {
        // given
        Integer userId = 2;
        String jsonRequest = "{\"amount\": 50000}";

        doNothing().when(walletService).chargeUserPoint(userId, 50000);

        // when & then
        mockMvc.perform(post("/api/admin/users/{userId}/wallet/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin-uuid", roles = "ADMIN")
    @DisplayName("관리자 권한으로 유저 목록 조회 성공 시 200 OK를 반환한다.")
    void getUserList_success() throws Exception {
        // when & then
        mockMvc.perform(get("/api/admin/users/user-list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ADMIN"));
    }
    
    // Note: Security configuration test for 403 Forbidden is typically handled in Integration Tests, 
    // but WebMvcTest also covers it if SecurityFilterChain is properly configured.
    // However, since we mock the filter chain or use @WithMockUser lightly in WebMvcTest, 
    // we focus on the controller's immediate responses here.
}
