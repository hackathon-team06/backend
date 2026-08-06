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
        OutingFrequency outingFrequency,
        CheckCycle checkCycle,
        CareMotivation careMotivation,
        LocalDateTime lastDiagnosisAt,
        Boolean hasDiagnosis
) {
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .goal(user.getGoal())
                .ageRange(user.getAgeRange())
                .skinType(user.getSkinType())
                .outingFrequency(user.getOutingFrequency())
                .checkCycle(user.getCheckCycle())
                .careMotivation(user.getCareMotivation())
                .lastDiagnosisAt(user.getLastDiagnosisAt())
                .hasDiagnosis(user.hasDiagnosis())
                .build();
    }
}
