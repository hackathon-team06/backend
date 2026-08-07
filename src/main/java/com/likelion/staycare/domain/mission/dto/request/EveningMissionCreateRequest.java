package com.likelion.staycare.domain.mission.dto.request;

import com.likelion.staycare.domain.mission.entity.enums.SkinCondition;
import jakarta.validation.constraints.NotNull;

public record EveningMissionCreateRequest(
        @NotNull
        SkinCondition skinCondition
) {
}
