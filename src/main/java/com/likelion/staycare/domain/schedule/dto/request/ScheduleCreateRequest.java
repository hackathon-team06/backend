package com.likelion.staycare.domain.schedule.dto.request;

import com.likelion.staycare.domain.schedule.entity.enums.Companion;
import com.likelion.staycare.domain.schedule.entity.enums.ScheduleCategory;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ScheduleCreateRequest(
        @NotNull(message = "일정 날짜를 입력해주세요.")
        LocalDate scheduleDate,

        @NotNull(message = "함께하는 대상을 입력해주세요.")
        Companion companion,

        @NotNull(message = "일정 카테고리를 입력해주세요.")
        ScheduleCategory category
) {
}
