package com.bwm.auth.service;

import com.bwm.auth.dto.LoginRequest;
import com.bwm.auth.dto.LoginResult;
import com.bwm.auth.dto.SignupRequest;
import com.bwm.auth.entity.RefreshToken;
import com.bwm.auth.repository.RefreshTokenRepository;
import com.bwm.global.config.security.JwtProvider;
import com.bwm.user.entity.User;
import com.bwm.user.entity.UserRole;
import com.bwm.user.repository.UserRepository;
import com.bwm.user.repository.UserRoleRepository;
import com.bwm.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bwm.item.repository.ItemRepository;
import com.bwm.item.entity.ItemStatus;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final WalletService walletService;
    private final ItemRepository itemRepository;

    @Override
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

        // 회원가입 시 지갑 생성
        walletService.createWallet(user);
    }

    @Override
    @Transactional
    public LoginResult login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 암호화된 비밀번호 비교로 원복!
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        String roleStr = user.getRoles().stream()
                .map(UserRole::getUserRole)
                .collect(java.util.stream.Collectors.joining(","));
        if (roleStr.isEmpty()) {
            roleStr = "USER"; // 기본 권한 방어 코드
        }

        // JTI 직접 생성 및 DB 먼저 저장 (트랜잭션 관점 최적화)
        String jti = java.util.UUID.randomUUID().toString();
        long nowMillis = System.currentTimeMillis(); // 생성 시간 완벽 동기화용
        
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .jti(jti)
                .userId(user.getUserId())
                .validityDuration(jwtProvider.getRefreshExpiration())
                .nowMillis(nowMillis)
                .build();
                
        refreshTokenRepository.save(refreshTokenEntity);

        // 실제 JWT Token 발급 (DB 저장 성공 시에만 연산 수행)
        String accessToken = jwtProvider.generateAccessToken(user.getUserUuid(), roleStr, nowMillis);
        String refreshToken = jwtProvider.generateRefreshToken(jti, nowMillis);

        return LoginResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken) // 클라이언트에게는 토큰 자체를 줌
                .nickname(user.getNickname())
                .role(roleStr)
                .build();
    }

    @Override
    @Transactional
    public LoginResult reissue(String refreshToken) {
        // 1. Refresh Token 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 Refresh Token 입니다. 다시 로그인해주세요.");
        }

        // 2. Refresh Token에서 JTI 추출
        String jti = jwtProvider.getJtiFromToken(refreshToken);

        // 3. DB에서 JTI 값으로 저장된 토큰 정보 찾기
        RefreshToken tokenEntity = refreshTokenRepository.findById(jti)
                .orElseThrow(() -> new IllegalArgumentException("DB에 존재하지 않는 갱신 토큰(JTI)입니다. 다시 로그인해주세요."));

        // 4. 해당 ID로 유저 정보 조회
        User user = userRepository.findById(tokenEntity.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        String roleStr = user.getRoles().stream()
                .map(UserRole::getUserRole)
                .collect(java.util.stream.Collectors.joining(","));
        if (roleStr.isEmpty()) {
            roleStr = "USER";
        }

        // 5. RTR (Refresh Token Rotation): 사용된 기존 토큰(JTI) 폐기
        refreshTokenRepository.delete(tokenEntity);

        // 6. 새 JTI 생성 및 DB 먼저 저장
        String newJti = java.util.UUID.randomUUID().toString();
        long nowMillis = System.currentTimeMillis(); // 생성 시간 완벽 동기화용
        
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .jti(newJti)
                .userId(user.getUserId())
                .validityDuration(jwtProvider.getRefreshExpiration())
                .nowMillis(nowMillis)
                .build();
        refreshTokenRepository.save(newRefreshTokenEntity);

        // 7. 새로운 Access Token 및 Refresh Token 발급 (DB 저장 성공 시에만)
        String newAccessToken = jwtProvider.generateAccessToken(user.getUserUuid(), roleStr, nowMillis);
        String newRefreshToken = jwtProvider.generateRefreshToken(newJti, nowMillis);

        return LoginResult.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .nickname(user.getNickname())
                .role(roleStr)
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
            return;
        }
        
        try {
            String jti = jwtProvider.getJtiFromToken(refreshToken);
            refreshTokenRepository.deleteById(jti);
        } catch (Exception e) {
            // 토큰 파싱 실패 또는 DB 삭제 실패 시 무시 (이미 로그아웃 처리된 것으로 간주)
        }
    }

    @Override
    @Transactional
    public void withdraw(String userUuid) {
        User user = userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Integer userId = user.getUserId();

        // 1. 최고 입찰자로 진행 중/낙찰 상태인지 확인 (입찰 취소 불가 상태)
        boolean hasActiveBids = itemRepository.existsByHighestBidder_UserIdAndStatusIn(
                userId, List.of(ItemStatus.OPEN, ItemStatus.SOLD));
        if (hasActiveBids) {
            throw new IllegalStateException("진행 중이거나 낙찰된 경매가 있어 탈퇴할 수 없습니다.");
        }

        // 2. 판매자로 진행 중/판매 완료(미정산 등) 상태인지 확인
        boolean hasActiveSales = itemRepository.existsBySeller_UserIdAndStatusIn(
                userId, List.of(ItemStatus.OPEN, ItemStatus.SOLD));
        if (hasActiveSales) {
            throw new IllegalStateException("진행 중이거나 처리되지 않은 판매 건이 있어 탈퇴할 수 없습니다.");
        }

        // 3. 지갑 잔액 확인 및 삭제 (WalletService 내부에서 잔액>0 이면 예외 발생)
        walletService.deleteWallet(userId);

        // 4. 유저 논리적 삭제 (Soft Delete)
        user.markAsDeleted();
    }
}
