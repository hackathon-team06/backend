package com.likelion.staycare.domain.schedule.dto.request;

import com.likelion.staycare.domain.schedule.entity.enums.Companion;
import com.likelion.staycare.domain.schedule.entity.enums.ScheduleCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(
        name = "ScheduleUpdateRequest",
        description = """
                일정 수정 요청
                companion 선택값: ALONE, FAMILY, FRIEND, LOVER, COWORKER, ACQUAINTANCE
                category 선택값: SELF_CARE, MEETING, DATE, TRAVEL, EVENT, CEREMONY, WEDDING, DRINKING, TALK
                """
)
public record ScheduleUpdateRequest(
        @NotBlank(message = "일정 제목을 입력해주세요.")
        @Schema(description = "일정 제목", example = "친구 결혼식")
        String title,

        @NotNull(message = "일정 날짜를 입력해주세요.")
        @Schema(description = "일정 날짜", example = "2026-08-15")
        LocalDate scheduleDate,

        @Schema(description = "일정 시작 시각", example = "13:00", nullable = true)
        LocalTime startTime,

        @Schema(description = "일정 종료 시각", example = "16:00", nullable = true)
        LocalTime endTime,

        @NotNull(message = "동행자를 입력해주세요.")
        @Schema(
                description = "동행자",
                example = "FRIEND",
                allowableValues = {"ALONE", "FAMILY", "FRIEND", "LOVER", "COWORKER", "ACQUAINTANCE"}
        )
        Companion companion,

        @NotNull(message = "일정 카테고리를 입력해주세요.")
        @Schema(
                description = "일정 카테고리",
                example = "WEDDING",
                allowableValues = {
                        "SELF_CARE", "MEETING", "DATE", "TRAVEL", "EVENT",
                        "CEREMONY", "WEDDING", "DRINKING", "TALK"
                }
        )
        ScheduleCategory category
) {
}
