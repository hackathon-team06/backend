package com.likelion.staycare.domain.user.dto;

import lombok.Builder;

@Builder
public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long userId
) {
    public static LoginResponse of(String accessToken,String refreshToken,Long userId) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(userId)
                .build();
    }
}
