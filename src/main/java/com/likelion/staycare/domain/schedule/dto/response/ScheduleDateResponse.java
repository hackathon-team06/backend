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
        Long scheduleId,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        LocalTime startTime,
        LocalTime endTime,
        Companion companion,
        ScheduleCategory category,
        ScheduleStatus status,
        long daysRemaining,
        String dDay
) {
    public static ScheduleDateResponse from(Schedule schedule, LocalDate today) {
        long daysRemaining = ChronoUnit.DAYS.between(today, schedule.getStartDate());

        return ScheduleDateResponse.builder()
                .scheduleId(schedule.getId())
                .title(schedule.getTitle())
                .startDate(schedule.getStartDate())
                .endDate(schedule.getEndDate())
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
