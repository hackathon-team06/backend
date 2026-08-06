package com.likelion.staycare.domain.diagnosis.dto;

import com.likelion.staycare.domain.diagnosis.entity.AgeRange;
import com.likelion.staycare.domain.diagnosis.entity.CareMotivation;
import com.likelion.staycare.domain.diagnosis.entity.CheckCycle;
import com.likelion.staycare.domain.diagnosis.entity.OutingFrequency;
import com.likelion.staycare.domain.diagnosis.entity.SleepHours;
import com.likelion.staycare.domain.diagnosis.entity.SkinType;
import jakarta.validation.constraints.NotNull;

public record DiagnosisRequest(
        @NotNull AgeRange ageRange,
        @NotNull SleepHours sleepHours,
        @NotNull SkinType skinType,
        @NotNull OutingFrequency outingFrequency,
        @NotNull CheckCycle checkCycle,
        @NotNull CareMotivation careMotivation
) {
}
