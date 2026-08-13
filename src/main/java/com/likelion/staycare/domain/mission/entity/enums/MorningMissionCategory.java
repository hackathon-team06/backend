package com.likelion.staycare.domain.mission.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "아침 고정 루틴 카테고리")
public enum MorningMissionCategory {

    MOISTURE("수분/보습"),
    SUN_PROTECTION("자외선 차단"),
    CLEANSING("세안/클렌징"),
    DIET_NUTRITION("식습관/영양"),
    SOOTHING_BARRIER("진정/장벽"),
    SLEEP_REST("수면/휴식"),
    HYGIENE("위생 관리"),
    EXERCISE_STRETCHING("운동/스트레칭"),
    POPULAR("인기 미션");

    private final String label;

    MorningMissionCategory(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static MorningMissionCategory from(String value) {
        for (MorningMissionCategory category : values()) {
            if (category.name().equalsIgnoreCase(value) || category.label.equals(value)) {
                return category;
            }
        }

        throw new IllegalArgumentException("Unknown morning mission category: " + value);
    }
}
