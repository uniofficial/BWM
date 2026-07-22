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
import org.springframework.http.ResponseCookie;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ResultDto<Void>> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok(ResultDto.success(null));
    }

    @PostMapping("/login")
    public ResponseEntity<ResultDto<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResult result = authService.login(request);
        
        // Access Token은 Header에 담기
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + result.getAccessToken());
        
        // Refresh Token은 HttpOnly Secure 쿠키로 응답
        ResponseCookie cookie = ResponseCookie.from("Refresh-Token", result.getRefreshToken())
                .httpOnly(true)
                .secure(false) // HTTP 테스트를 위해 임시로 false (실서버 HTTPS 적용 시 true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7일
                .sameSite("Strict")
                .build();
        
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

        LoginResponse responseBody = LoginResponse.of(result);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ResultDto.success(responseBody));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ResultDto<LoginResponse>> reissue(
            @CookieValue(value = "Refresh-Token", required = false) String refreshToken) {
        
        if (refreshToken == null) {
            throw new IllegalArgumentException("Refresh Token이 존재하지 않습니다. 다시 로그인해주세요.");
        }

        LoginResult result = authService.reissue(refreshToken);

        // 새로운 Access Token 발급
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + result.getAccessToken());

        // 새로운 Refresh Token 쿠키 설정
        ResponseCookie cookie = ResponseCookie.from("Refresh-Token", result.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();
        
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

        LoginResponse responseBody = LoginResponse.of(result);
        return ResponseEntity.ok()
                .headers(headers)
                .body(ResultDto.success(responseBody));
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<ResultDto<Void>> withdraw(Authentication authentication) {
        String email = authentication.getName();
        authService.withdraw(email);
        return ResponseEntity.ok(ResultDto.success(null));
    }
}
