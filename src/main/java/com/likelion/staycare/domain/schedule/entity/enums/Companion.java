package com.likelion.staycare.domain.schedule.entity.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "일정 동행자 유형",
        example = "FRIEND",
        allowableValues = {"ALONE", "FAMILY", "FRIEND", "LOVER", "COWORKER", "ACQUAINTANCE"}
)
public enum Companion {
    ALONE,
    FAMILY,
    FRIEND,
    LOVER,
    COWORKER,
    ACQUAINTANCE
}
