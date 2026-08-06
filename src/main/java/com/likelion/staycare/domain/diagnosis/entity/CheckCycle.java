package com.likelion.staycare.domain.diagnosis.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum CheckCycle {
    DAYS_7(7, "7일"),
    DAYS_14(14, "14일"),
    DAYS_21(21, "21일"),
    DAYS_28(28, "28일");

    private final int days;

    private final String label;

    @JsonValue
    public String getJsonValue() {
        return label;
    }

    @JsonCreator
    public static CheckCycle from(String value) {
        return Arrays.stream(values())
                .filter(v -> v.label.equals(value) || v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 동기입니다: " + value));
    }
}
