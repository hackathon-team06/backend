package com.likelion.staycare.domain.diagnosis.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum CareMotivation {
    MOIST_SKIN("촉촉한 피부"),
    D_DAY_PROMISE("D-DAY 약속"),
    TROUBLE_CARE("트러블/열감 진정"),
    HOMECOMING_HABIT("귀가 후 습관 형성"),
    HYDRATION("수분 챙기기"),
    NUTRITION("영양 챙기기");

    private final String label;

    @JsonValue
    public String getJsonValue() {
        return label;
    }

    @JsonCreator
    public static CareMotivation from(String value) {
        return Arrays.stream(values())
                .filter(v -> v.label.equals(value) || v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 동기입니다: " + value));
    }
}
