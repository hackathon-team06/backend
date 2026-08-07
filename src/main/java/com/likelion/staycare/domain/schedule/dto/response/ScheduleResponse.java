package com.likelion.staycare.domain.schedule.dto.response;

import com.likelion.staycare.domain.schedule.entity.Schedule;
import com.likelion.staycare.domain.schedule.entity.enums.Companion;
import com.likelion.staycare.domain.schedule.entity.enums.ScheduleCategory;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ScheduleResponse(
        Long id,
        LocalDate scheduleDate,
        Companion companion,
        ScheduleCategory category
) {
    public static ScheduleResponse from(Schedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .scheduleDate(schedule.getScheduleDate())
                .companion(schedule.getCompanion())
                .category(schedule.getCategory())
                .build();
    }
}
