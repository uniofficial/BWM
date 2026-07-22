package com.bwm.auth.service;

import com.bwm.auth.dto.LoginRequest;
import com.bwm.auth.dto.LoginResult;
import com.bwm.auth.dto.SignupRequest;

public interface AuthService {
    void signup(SignupRequest request);
    LoginResult login(LoginRequest request);
    LoginResult reissue(String refreshToken);
    void withdraw(String userUuid);
}
