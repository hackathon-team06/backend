package com.likelion.staycare.domain.mission.dto.request;

import com.likelion.staycare.domain.mission.entity.enums.MorningRoutineItemSource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "아침 루틴 항목 요청")
public record MorningRoutineItemRequest(
        @NotBlank
        @Schema(description = "루틴 내용", example = "수분 토너 사용하기")
        String content,

        @Schema(description = "선택 카테고리", example = "보습")
        String category,

        @NotNull
        @Schema(description = "항목 출처", example = "AI")
        MorningRoutineItemSource source
) {
}
