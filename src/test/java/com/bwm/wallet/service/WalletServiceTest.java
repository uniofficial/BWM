package com.bwm.wallet.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.bwm.wallet.dto.WalletResponseDto;
import com.bwm.wallet.entity.Wallet;
import com.bwm.wallet.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    @DisplayName("지갑 조회 성공 테스트 - 존재하는 유저 ID일 때 현재 잔액을 반환해야 한다")
    void getMyWallet_success() {
        Integer userId = 1;
        Integer balance = 50000;
        
        Wallet mockWallet = Wallet.builder()
                .userId(userId)
                .balance(balance)
                .build();
        
        when(walletRepository.findById(userId)).thenReturn(Optional.of(mockWallet));

        // when
        WalletResponseDto result = walletService.getMyWallet(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getBalance()).isEqualTo(balance);
        log.info("지갑 조회 성공 검증 완료 - 잔액: {}", result.getBalance());
    }

    @Test
    @DisplayName("지갑 조회 실패 테스트 - 서비스 코드를 건드리지 않고 테스트 단에서 예외 로그 출력 및 검증")
    void getMyWallet_fail_notFound() {
        Integer userId = 999;
        when(walletRepository.findById(userId)).thenReturn(Optional.empty());

        try {
            walletService.getMyWallet(userId);
            fail("예외가 발생해야 하는데 발생하지 않았습니다."); // 예외가 안 터지면 테스트 실패 처리
        } catch (IllegalArgumentException e) {
            log.warn("지갑 조회 실패 검증 - 유저 ID: {}, 던져진 예외: {}", userId, e.getMessage());
            
            assertThat(e.getMessage()).isEqualTo("해당 유저의 지갑을 찾을 수 없습니다.");
        }
    }
}
