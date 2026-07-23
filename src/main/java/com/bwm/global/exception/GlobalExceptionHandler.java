package com.bwm.global.exception;

import com.bwm.global.dto.ResultDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultDto<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResultDto.error("BAD_REQUEST", errorMessage != null ? errorMessage : "유효성 검사 실패"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResultDto<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResultDto.error("BAD_REQUEST", e.getMessage()));
    }

    // 회원 탈퇴 제한 등 현재 상태 충돌은 500이 아닌 409로 응답해 사용자에게 사유를 전달합니다.
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResultDto<Void>> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResultDto.error("CONFLICT", e.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResultDto<Void>> handleBusinessException(BusinessException e) {
        // 서버 로그에는 상세 메시지(가변 인자 포함) 출력
        log.error("[Business Exception] {} : {}", e.getErrorCode(), e.getLogMessage());

        // 프론트엔드로는 안전한 기본 메시지만 응답
        return ResponseEntity.status(e.getStatus())
                .body(ResultDto.error(e.getErrorCode(), e.getMessage()));
    }

    // 그 외 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultDto<Void>> handleException(Exception e) {
        log.error("[서버 내부 오류 발생] ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResultDto.error("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."));
    }
}