package com.bwm.user.controller;

import com.bwm.global.dto.ResultDto;
import com.bwm.user.dto.UserMeResponse;
import com.bwm.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ResultDto<UserMeResponse>> getMyInfo(Authentication authentication) {
        // authentication.getName() 에는 SecurityConfig 필터를 거치면서 저장된 userUuid가 들어있음
        String userUuid = authentication.getName();
        UserMeResponse response = userService.getMyInfo(userUuid);
        return ResponseEntity.ok(ResultDto.success(response));
    }
}
