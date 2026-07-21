package com.bwm.bid.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import com.bwm.support.ControllerTestSupport;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import com.bwm.global.config.SecurityConfig;

@WebMvcTest(MyBidController.class)
@Import(SecurityConfig.class)
public class MyBidControllerTest extends ControllerTestSupport {

    @Test
    @WithMockUser(username = "uuid-1234")
    @DisplayName("나의 입찰 내역 조회 시 200 OK를 반환한다.")
    void getMyBidHistory_success() throws Exception {
        // given
        given(bidService.getMyBidHistory("uuid-1234")).willReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/users/me/bids"))
                .andExpect(status().isOk());
    }
}
