package com.likelion.staycare.domain.schedule.dto.request;

import com.likelion.staycare.domain.schedule.entity.enums.Companion;
import com.likelion.staycare.domain.schedule.entity.enums.ScheduleCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(name = "ScheduleCreateRequest", description = "일정 등록 요청")
public record ScheduleCreateRequest(
        @NotNull
        @Schema(description = "일정 시작일", example = "2026-08-15")
        LocalDate startDate,

        @NotNull
        @Schema(description = "일정 종료일", example = "2026-08-17")
        LocalDate endDate,

        @Schema(description = "시작 시간", example = "09:00", nullable = true)
        LocalTime startTime,

        @Schema(description = "종료 시간", example = "20:00", nullable = true)
        LocalTime endTime,

        @NotNull
        @Schema(description = "동행자", example = "FRIEND")
        Companion companion,

        @NotNull
        @Schema(description = "일정 카테고리", example = "TRAVEL")
        ScheduleCategory category
) {
}
