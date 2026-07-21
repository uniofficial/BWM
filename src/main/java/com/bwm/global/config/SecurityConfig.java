package com.bwm.global.config;

import com.bwm.global.config.security.CustomAccessDeniedHandler;
import com.bwm.global.config.security.CustomAuthenticationEntryPoint;
import com.bwm.global.config.security.JwtAuthenticationFilter;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .formLogin(AbstractHttpConfigurer::disable)

                .httpBasic(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/signup", "/api/auth/reissue")
                        .permitAll()

                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/index.html")
                        .permitAll()

                        .requestMatchers(
                                "/images/**",
                                "/error")
                        .permitAll()

                        // 관리자 전용
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // 나머지는 로그인 필요
                        .anyRequest()
                        .authenticated())
                // .permitAll())

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}