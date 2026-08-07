package com.likelion.staycare.domain.user.dto;

import com.likelion.staycare.domain.diagnosis.entity.*;
import com.likelion.staycare.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UserResponse(
        Long userId,
        String nickname,
        String goal,
        String profileImageUrl,
        AgeRange ageRange,
        SkinType skinType,
        SleepHours sleepHours,
        ReturnHomeTime returnHomeTime,
        CheckCycle checkCycle,
        CareMotivation careMotivation,
        Boolean notificationEnabled

) {
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .goal(user.getGoal())
                .profileImageUrl(user.getProfileImageUrl())
                .ageRange(user.getAgeRange())
                .skinType(user.getSkinType())
                .sleepHours(user.getSleepHours())
                .returnHomeTime(user.getReturnHomeTime())
                .checkCycle(user.getCheckCycle())
                .careMotivation(user.getCareMotivation())
                .notificationEnabled(user.getNotificationEnabled())
                .build();
    }
}
