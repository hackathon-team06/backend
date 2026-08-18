package com.likelion.staycare.domain.stamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "현재 스탬프북 종료일까지 남은 기간 응답")
public record StampBookCountdownResponse(

        @Schema(description = "스탬프북 시작일", example = "2026-08-05")
        LocalDate startDate,

        @Schema(description = "스탬프북 종료일", example = "2026-08-18")
        LocalDate endDate,

        @Schema(description = "설정된 기간 일수", example = "14")
        Integer periodDays,

        @Schema(description = "오늘부터 종료일까지 남은 일수", example = "3")
        long remainingDays,

        @Schema(description = "종료일 기준 D-day 표기", example = "D-3")
        String dDay
) {
}
