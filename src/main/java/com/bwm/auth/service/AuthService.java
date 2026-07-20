package com.bwm.auth.service;

import com.bwm.auth.dto.LoginRequest;
import com.bwm.auth.dto.LoginResult;
import com.bwm.auth.dto.SignupRequest;
import com.bwm.user.entity.User;
import com.bwm.user.entity.UserRole;
import com.bwm.user.repository.UserRepository;
import com.bwm.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.HashSet;
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
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .build();

        // 기본 권한 부여 (미리 DB에 "USER" 또는 "ROLE_USER" 권한이 있어야 함)
        UserRole userRole = userRoleRepository.findByUserRole("USER")
                .orElseGet(() -> userRoleRepository.save(UserRole.builder().userRole("USER").build()));
        
        user.getRoles().add(userRole);

        userRepository.save(user);
    }

    public LoginResult login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 암호화된 비밀번호 비교로 원복!
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        String roleStr = user.getRoles().stream()
                .map(UserRole::getUserRole)
                .findFirst()
                .orElse("USER");

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
