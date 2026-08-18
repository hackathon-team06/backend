package com.likelion.staycare.domain.stamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "스탬프북 카드 응답")
public record StampBookCardResponse(

        @Schema(description = "스탬프북 시작일", example = "2026-07-26")
        LocalDate startDate,

        @Schema(description = "스탬프북 종료일", example = "2026-08-22")
        LocalDate endDate,

        @Schema(description = "스탬프북 기간(일)", example = "28")
        Integer periodDays,

        @Schema(description = "카드 진행 상태", example = "IN_PROGRESS", allowableValues = {
                "COMPLETED", "IN_PROGRESS"
        })
        String status,

        @Schema(description = "진행 일수", example = "24")
        Integer progressDays,

        @Schema(description = "카드 표시 문구", example = "24일째 진행중")
        String displayText
) {
}
