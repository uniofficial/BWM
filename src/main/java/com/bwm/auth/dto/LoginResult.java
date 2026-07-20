package com.bwm.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResult {
    private String accessToken;
    private String refreshToken;
    private String role;
    private String nickname;
}
