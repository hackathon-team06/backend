package com.likelion.staycare.domain.user.dto;

import lombok.Builder;

@Builder
public record LoginResponse(
        String accessToken,
        String tokenType,
        Long userId
) {
    public static LoginResponse of(String accessToken, Long userId) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .userId(userId)
                .build();
    }
}
