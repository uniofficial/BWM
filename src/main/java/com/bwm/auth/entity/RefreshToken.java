package com.bwm.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @Column(name = "jti", length = 36)
    private String jti;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "expiration", nullable = false)
    private LocalDateTime expiration;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Builder
    public RefreshToken(String jti, Integer userId, Long validityDuration, Long nowMillis) {
        this.jti = jti;
        this.userId = userId;
        
        // 외부에서 주입받은 정확한 밀리초(nowMillis)를 사용하여 토큰 내부의 exp와 DB의 expiration을 1ms의 오차도 없이 동기화!
        LocalDateTime now = java.time.LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(nowMillis), 
                java.time.ZoneId.systemDefault()
        );
        this.created = now;
        this.expiration = now.plus(java.time.Duration.ofMillis(validityDuration));
    }
}
