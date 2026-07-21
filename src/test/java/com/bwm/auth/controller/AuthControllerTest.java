package com.bwm.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import com.bwm.auth.dto.LoginRequest;
import com.bwm.auth.dto.LoginResult;
import com.bwm.auth.dto.SignupRequest;
import com.bwm.support.ControllerTestSupport;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import com.bwm.global.config.SecurityConfig;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("회원가입 성공 시 200 OK를 반환한다.")
    void signup_success() throws Exception {
        // given
        SignupRequest request = new SignupRequest();
        // Assuming SignupRequest has setters or we can use reflection. 
        // Actually, if it's a POJO, we'll just send JSON directly and let Jackson deserialize it.
        String jsonRequest = "{\"email\":\"test@test.com\", \"password\":\"123456\", \"nickname\":\"tester\"}";

        doNothing().when(authService).signup(any(SignupRequest.class));

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("회원가입 시 이메일 형식이 안 맞으면 400 Bad Request를 반환한다.")
    void signup_fail_validation() throws Exception {
        // given
        String jsonRequest = "{\"email\":\"invalid-email\", \"password\":\"123456\", \"nickname\":\"tester\"}";

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 성공 시 200 OK와 토큰을 반환한다.")
    void login_success() throws Exception {
        // given
        String jsonRequest = "{\"email\":\"test@test.com\", \"password\":\"123456\"}";
        LoginResult loginResult = LoginResult.builder()
                .accessToken("access-token-123")
                .refreshToken("refresh-token-123")
                .nickname("tester")
                .role("USER")
                .build();

        given(authService.login(any(LoginRequest.class))).willReturn(loginResult);

        // when & then
        mockMvc.perform(post("/api/auth/login").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(header().string("Authorization", "Bearer access-token-123"));
    }
}
