package com.bwm.auth.repository;

import com.bwm.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    /*
     * [사용 방법 안내]
     * 1. DB에 새로 생성한 Refresh 토큰 넣기 (Save)
     *    - JpaRepository가 기본 제공하는 save() 메서드를 사용합니다.
     *    - 예시: refreshTokenRepository.save(refreshTokenEntity);
     * 
     * 2. DB에서 jti(Refresh 토큰의 고유 식별자)로 추출하기 (Find)
     *    - JTI가 PK(String)이므로 기본 제공되는 findById()를 사용합니다.
     *    - 예시: refreshTokenRepository.findById(jti).orElseThrow(...);
     */

    // 특정 유저의 모든 토큰을 일괄 삭제할 때 사용하는 커스텀 메서드입니다.
    void deleteByUserId(Integer userId);
}
