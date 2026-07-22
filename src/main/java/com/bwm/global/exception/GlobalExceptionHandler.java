package com.bwm.global.exception;

import com.bwm.global.dto.ResultDto;
import com.bwm.wallet.exception.WalletException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Spring Security 예외는 EntryPoint와 AccessDeniedHandler에서 우선 처리되지만,
    // 컨트롤러 단에서 발생하는 예외를 잡기 위해 추가합니다.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResultDto<Void>> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResultDto.error("UNAUTHORIZED", "인증 실패: " + e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResultDto<Void>> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResultDto.error("FORBIDDEN", "접근 권한이 없습니다."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResultDto<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResultDto.error("BAD_REQUEST", e.getMessage()));
    }

    @ExceptionHandler(WalletException.class)
    public ResponseEntity<ResultDto<Void>> handleWalletException(WalletException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResultDto.error(e.getErrorCode(), e.getMessage()));
    }

    // 그 외 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultDto<Void>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResultDto.error("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."));
    }
}
