package com.likelion.staycare.domain.schedule.entity.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "일정 상태")
public enum ScheduleStatus {
    ACTIVE,
    CANCELLED
}
