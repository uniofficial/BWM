package com.bwm.auth.service;

import com.bwm.auth.dto.LoginRequest;
import com.bwm.auth.dto.LoginResult;
import com.bwm.user.entity.User;
import com.bwm.user.entity.UserRole;
import com.bwm.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResult login(LoginRequest request) {
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
        return LoginResult.builder()
                .accessToken("AccessToken_JWT")
                .refreshToken("RefreshToken_JWT")
                .nickname(user.getNickname())
                .role(roleStr)
                .build();
    }
    // @Override
    // public Collection<? extends GrantedAuthority> getAuthorities() {
    // return user.getRoles().stream()
    // .map(userRole -> new SimpleGrantedAuthority("ROLE_" +
    // userRole.getUserRole()))
    // .collect(Collectors.toList());
    // }
}
