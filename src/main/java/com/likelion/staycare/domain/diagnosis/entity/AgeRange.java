package com.likelion.staycare.domain.diagnosis.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AgeRange {

    AGE_15_20("15~20세"),
    AGE_21_25("21~25세"),
    AGE_26_30("26~30세"),
    AGE_31_35("31~35세"),
    AGE_36_PLUS("36세 이후");

    private final String label;

    @JsonValue
    public String getJsonValue() {
        return label;
    }

    @JsonCreator
    public static AgeRange from(String value) {
        return Arrays.stream(values())
                .filter(v -> v.label.equals(value) || v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 나이대입니다: " + value));
    }
}
