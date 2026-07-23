package com.bwm.user.service;

import com.bwm.user.dto.UserMeResponse;
import com.bwm.user.entity.User;
import com.bwm.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserMeResponse getMyInfo(String userUuid) {
        User user = userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        return UserMeResponse.builder()
                .nickname(user.getNickname())
                .userUuid(user.getUserUuid())
                .build();
    }
}
