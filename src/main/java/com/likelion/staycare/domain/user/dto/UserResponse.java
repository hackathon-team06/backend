package com.likelion.staycare.domain.user.dto;

import com.likelion.staycare.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UserResponse(
        Long userId,
        String nickname,
        String goal
) {
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .goal(user.getGoal())
                .build();
    }
}
