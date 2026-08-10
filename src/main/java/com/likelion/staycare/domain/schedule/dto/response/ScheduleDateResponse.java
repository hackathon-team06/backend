package com.likelion.staycare.domain.schedule.dto.response;

import com.likelion.staycare.domain.schedule.entity.Schedule;
import com.likelion.staycare.domain.schedule.entity.enums.Companion;
import com.likelion.staycare.domain.schedule.entity.enums.ScheduleCategory;
import com.likelion.staycare.domain.schedule.entity.enums.ScheduleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Builder
@Schema(description = "날짜별 일정 조회 응답")
public record ScheduleDateResponse(
        @Schema(description = "일정 ID", example = "1")
        Long scheduleId,

        @Schema(description = "일정 제목", example = "친구 결혼식")
        String title,

        @Schema(description = "일정 날짜", example = "2026-08-15")
        LocalDate date,

        @Schema(description = "일정 시작 시각", example = "13:00", nullable = true)
        LocalTime startTime,

        @Schema(description = "일정 종료 시각", example = "16:00", nullable = true)
        LocalTime endTime,

        @Schema(description = "동행자", example = "FRIEND")
        Companion companion,

        @Schema(description = "일정 카테고리", example = "WEDDING")
        ScheduleCategory category,

        @Schema(description = "일정 상태", example = "ACTIVE")
        ScheduleStatus status,

        @Schema(description = "남은 일수", example = "5")
        long daysRemaining,

        @Schema(description = "D-Day 문자열", example = "D-5")
        String dDay
) {
    public static ScheduleDateResponse from(Schedule schedule, LocalDate today) {
        long daysRemaining = ChronoUnit.DAYS.between(today, schedule.getScheduleDate());

        return ScheduleDateResponse.builder()
                .scheduleId(schedule.getId())
                .title(schedule.getTitle())
                .date(schedule.getScheduleDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .companion(schedule.getCompanion())
                .category(schedule.getCategory())
                .status(schedule.getStatus())
                .daysRemaining(daysRemaining)
                .dDay(toDday(daysRemaining))
                .build();
    }

    private static String toDday(long daysRemaining) {
        if (daysRemaining == 0) {
            return "D-Day";
        }

        if (daysRemaining > 0) {
            return "D-" + daysRemaining;
        }

        return "D+" + Math.abs(daysRemaining);
    }
}
