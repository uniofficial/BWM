package com.bwm.global.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateAccessToken(String email, String role) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + accessExpiration);

        return Jwts.builder()
                .subject(email)
                .claim("auth", role)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken() {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + refreshExpiration);

        return Jwts.builder()
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        Object authClaim = claims.get("auth");

        if (authClaim == null) {
            throw new IllegalArgumentException("JWT에 권한 정보가 없습니다.");
        }

        List<SimpleGrantedAuthority> authorities =
                Arrays.stream(authClaim.toString().split(","))
                        .map(String::trim)
                        .filter(role -> !role.isBlank())
                        .map(role -> role.startsWith("ROLE_")
                                ? role
                                : "ROLE_" + role)
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        User principal = new User(
                claims.getSubject(),
                "",
                authorities
        );

        return new UsernamePasswordAuthenticationToken(
                principal,
                token,
                authorities
        );
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;

        } catch (io.jsonwebtoken.security.SecurityException
                 | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");

        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");

        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");

        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 비어 있거나 올바르지 않습니다.");

        } catch (JwtException e) {
            log.info("JWT 토큰 검증에 실패했습니다.");
        }

        return false;
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}