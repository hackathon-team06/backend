package com.likelion.staycare.domain.schedule.entity.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "일정 카테고리",
        example = "WEDDING",
        allowableValues = {
                "SELF_CARE", "MEETING", "DATE", "TRAVEL", "EVENT",
                "CEREMONY", "WEDDING", "DRINKING", "TALK"
        }
)
public enum ScheduleCategory {
    SELF_CARE,
    MEETING,
    DATE,
    TRAVEL,
    EVENT,
    CEREMONY,
    WEDDING,
    DRINKING,
    TALK
}


