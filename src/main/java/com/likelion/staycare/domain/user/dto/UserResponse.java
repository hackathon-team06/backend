package com.likelion.staycare.domain.user.dto;

import com.likelion.staycare.domain.diagnosis.entity.*;
import com.likelion.staycare.domain.user.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponse(
        Long userId,
        String nickname,
        String goal,
        AgeRange ageRange,
        SkinType skinType,
        SleepHours sleepHours,
        OutingFrequency outingFrequency,
        CheckCycle checkCycle,
        CareMotivation careMotivation
) {
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .goal(user.getGoal())
                .ageRange(user.getAgeRange())
                .skinType(user.getSkinType())
                .sleepHours(user.getSleepHours())
                .outingFrequency(user.getOutingFrequency())
                .checkCycle(user.getCheckCycle())
                .careMotivation(user.getCareMotivation())
                .build();
    }
}
