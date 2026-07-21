package com.bwm.item.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

import com.bwm.support.ControllerTestSupport;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import com.bwm.global.config.SecurityConfig;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest extends ControllerTestSupport {

    @Test
    @WithMockUser(username = "uuid-1234")
    @DisplayName("나의 판매 상품 목록 조회 시 200 OK를 반환한다.")
    void getMyItems_success() throws Exception {
        // given
        given(itemService.getMyItems(eq("uuid-1234"), any(Pageable.class))).willReturn(Page.empty());

        // when & then
        mockMvc.perform(get("/api/users/me/items"))
                .andExpect(status().isOk());
    }
}
