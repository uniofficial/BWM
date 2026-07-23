package com.bwm.user.service;

import com.bwm.user.dto.UserMeResponse;

public interface UserService {
    UserMeResponse getMyInfo(String userUuid);
}
