package com.bwm.auth.service;

import com.bwm.auth.dto.LoginRequest;
import com.bwm.auth.dto.LoginResponse;
import com.bwm.user.entity.User;
import com.bwm.user.entity.UserRole;
import com.bwm.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        if (!request.getPassword().equals(user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        String roleStr = user.getRoles().stream()
                .map(UserRole::getUserRole)
                .findFirst()
                .orElse("ROLE_USER");

        // 임시 token생성
        return LoginResponse.builder()
                .accessToken("임시_AccessToken_JWT_미적용")
                .refreshToken("임시_RefreshToken_JWT_미적용")
                .nickname(user.getNickname())
                .role(roleStr)
                .build();
    }
}
