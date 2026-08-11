package com.likelion.staycare.domain.user.dto;

import com.likelion.staycare.domain.diagnosis.entity.*;
import com.likelion.staycare.domain.user.entity.User;
import lombok.Builder;

import java.time.LocalTime;

@Builder
public record UserResponse(
        Long userId,
        String nickname,
        String goal,
        String profileImageUrl,
        Integer age,
        Gender gender,
        SkinType skinType,
        LocalTime wakeUpTime,
        LocalTime returnHomeTime,
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
                .age(user.getAge())
                .gender(user.getGender())
                .skinType(user.getSkinType())
                .wakeUpTime(user.getWakeUpTime())
                .returnHomeTime(user.getReturnHomeTime())
                .checkCycle(user.getCheckCycle())
                .careMotivation(user.getCareMotivation())
                .notificationEnabled(user.getNotificationEnabled())
                .build();
    }
}
