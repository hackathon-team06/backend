package com.likelion.staycare.domain.diagnosis.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;
import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ReturnHomeTime {
    HOUR_6("6시", LocalTime.of(18, 0)),
    HOUR_7("7시", LocalTime.of(19, 0)),
    HOUR_8("8시", LocalTime.of(20, 0)),
    HOUR_9("9시", LocalTime.of(21, 0)),
    HOUR_10("10시", LocalTime.of(22, 0)),
    HOUR_11("11시", LocalTime.of(23, 0)),
    HOUR_12("12시", LocalTime.of(0, 0)); // 자정 기준

    private final String label;
    private final LocalTime sendTime;

    @JsonValue
    public String getJsonValue() {
        return label;
    }

    @JsonCreator
    public static ReturnHomeTime from(String value) {
        return Arrays.stream(values())
                .filter(v -> v.label.equals(value) || v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 귀가 시간입니다: " + value));
    }

    public static ReturnHomeTime from(LocalTime time) {
        return Arrays.stream(values())
                .filter(v -> v.sendTime.getHour() == time.getHour()
                        && v.sendTime.getMinute() == time.getMinute())
                .findFirst()
                .orElse(null);
    }
}
