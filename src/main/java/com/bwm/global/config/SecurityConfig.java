package com.bwm.global.config;

import com.bwm.global.config.security.CustomAccessDeniedHandler;
import com.bwm.global.config.security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
// TODO: 추후 JwtAuthenticationFilter 추가 시 주석 해제
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    // 프론트에게 code, msg전달 위함(서블릿 이전 exception은 따로 핸들링 필요)
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // 로그인 폼 비활성화(REST구조)
                .formLogin(AbstractHttpConfigurer::disable)
                // JWT사용(세션 제거)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 예외 처리 설정(@RestControllerAdvice는 서블릿 이하만 가능->이전 필터에서 발생할 에러는 여기서 처리(토큰/권한 부족))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint) // AuthenticationException
                        .accessDeniedHandler(accessDeniedHandler)) // AccessDeniedException

                // 권한 규칙 설정
                .authorizeHttpRequests(auth -> auth
                        // .requestMatchers("/api/auth/login", "/api/auth/signup").permitAll()
                        // .requestMatchers("/swagger/**", "/swagger-ui/**",
                        // "/v3/api-docs/**").permitAll()
                        // .anyRequest().authenticated());
                        .anyRequest().permitAll()); // 토큰 발급후 막을 예정
        // .logout은 세션삭제라 jwt토큰기반은 불필요

        // TODO: 추후 JWT 필터 등록 예정
        // .addFilterBefore(jwtAuthenticationFilter,
        // UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
