package com.likelion.staycare.domain.schedule.dto.request;

import com.likelion.staycare.domain.schedule.entity.enums.Companion;
import com.likelion.staycare.domain.schedule.entity.enums.ScheduleCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(name = "ScheduleUpdateRequest", description = "일정 수정 요청")
public record ScheduleUpdateRequest(
        @NotBlank
        @Schema(description = "일정 제목", example = "피부과 방문")
        String title,

        @NotNull
        @Schema(description = "일정 시작일", example = "2026-08-20")
        LocalDate startDate,

        @NotNull
        @Schema(description = "일정 종료일", example = "2026-08-20")
        LocalDate endDate,

        @Schema(description = "시작 시간", example = "13:00", nullable = true)
        LocalTime startTime,

        @Schema(description = "종료 시간", example = "16:00", nullable = true)
        LocalTime endTime,

        @NotNull
        @Schema(description = "동행자", example = "ALONE")
        Companion companion,

        @NotNull
        @Schema(description = "일정 카테고리", example = "SELF_CARE")
        ScheduleCategory category
) {
}
