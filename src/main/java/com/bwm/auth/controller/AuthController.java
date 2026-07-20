package com.bwm.auth.controller;

import com.bwm.auth.dto.SignupRequest;
import com.bwm.auth.dto.LoginRequest;
import com.bwm.auth.dto.LoginResponse;
import com.bwm.auth.dto.LoginResult;
import com.bwm.auth.service.AuthService;
import com.bwm.global.dto.ResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ResultDto<Void>> signup(@RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok(ResultDto.success(null));
    }

    @PostMapping("/login")
    // JWT는 Header로 response -> ResponseEntity가 custom header가능
    public ResponseEntity<ResultDto<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResult result = authService.login(request);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + result.getAccessToken());
        // refresh토큰은 HttpOnly Secure 쿠키로 추후 설정
        // HttpCookie cookie = HttpCookie.of(
        // "Refresh-Token", result.getRefreshToken());
        // cookie.setHttpOnly(true);
        // cookie.setSecure(true);
        // cookie.setPath("/");
        // cookie.setMaxAge(org.springframework.util.Duration.ofDays(7));

        LoginResponse responseBody = LoginResponse.of(result);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ResultDto.success(responseBody));
    }
}
