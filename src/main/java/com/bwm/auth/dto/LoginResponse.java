package com.bwm.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String role;
    private String nickname;

    public static LoginResponse of(LoginResult result) {
        return LoginResponse.builder()
                .role(result.getRole())
                .nickname(result.getNickname())
                .build();
    }
}
