package com.likelion.staycare.domain.stamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "달력 날짜별 스탬프 상태")
public record StampCalendarDayResponse(

        @Schema(description = "날짜", example = "2026-05-01")
        LocalDate date,

        @Schema(description = "스탬프 상태", example = "FULL_SUCCESS", allowableValues = {
                "FULL_SUCCESS", "PARTIAL_SUCCESS", "NOT_DONE"
        })
        String status,

        @Schema(description = "해당 날짜 총 획득 포인트 (일일 포인트 + 정산 포인트)", example = "8")
        Integer point
) {
}
