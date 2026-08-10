package com.likelion.staycare.domain.schedule.dto.response;

import com.likelion.staycare.domain.schedule.entity.Schedule;
import com.likelion.staycare.domain.schedule.entity.enums.Companion;
import com.likelion.staycare.domain.schedule.entity.enums.ScheduleCategory;
import com.likelion.staycare.domain.schedule.entity.enums.ScheduleStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record ScheduleResponse(
        Long id,
        String title,
        LocalDate scheduleDate,
        LocalTime startTime,
        LocalTime endTime,
        Companion companion,
        ScheduleCategory category,
        ScheduleStatus status
) {
    public static ScheduleResponse from(Schedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .scheduleDate(schedule.getScheduleDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .companion(schedule.getCompanion())
                .category(schedule.getCategory())
                .status(schedule.getStatus())
                .build();
    }
}
