package com.likelion.staycare.domain.user.dto;

import lombok.Builder;

@Builder
public record LoginResponse(
        String accessToken,
        String tokenType,
        Long userId,
        String nickname,
        String goal
) {
    public static LoginResponse of(String accessToken, Long userId, String nickname, String goal) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .userId(userId)
                .nickname(nickname)
                .goal(goal)
                .build();
    }
}
