package com.bwm.global.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResultDto<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;

    public static <T> ResultDto<T> success(T data) {
        return ResultDto.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ResultDto<T> error(String code, String message) {
        return ResultDto.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .build();
    }
}
