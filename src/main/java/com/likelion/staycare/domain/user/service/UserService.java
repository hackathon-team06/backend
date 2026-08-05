package com.likelion.staycare.domain.user.service;

import com.likelion.staycare.domain.user.dto.*;
import com.likelion.staycare.domain.user.entity.User;
import com.likelion.staycare.domain.user.exception.UserErrorCode;
import com.likelion.staycare.domain.user.repository.UserRepository;
import com.likelion.staycare.global.exception.CustomException;
import com.likelion.staycare.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;


    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new CustomException(UserErrorCode.INVALID_PASSWORD));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(UserErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.createAccessToken(user.getId());
        return LoginResponse.of(accessToken, user.getId(), user.getNickname(), user.getGoal());
    }

    @Transactional
    public UserResponse updateNickname(Long userId, NicknameRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        user.updateNickname(request.nickname());
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateGoal(Long userId, GoalRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        user.updateGoal(request.goal());
        return UserResponse.from(user);
    }

    public UserResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }
}
