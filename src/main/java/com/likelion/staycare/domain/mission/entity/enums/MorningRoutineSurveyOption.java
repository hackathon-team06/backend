package com.likelion.staycare.domain.mission.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = """
                최초 생활 루틴 설문 코드입니다.
                WATER_AFTER_WAKEUP=기상 직후 미온수 한 잔 마시기
                QUICK_WASH_AFTER_RETURN=귀가 후 10분 이내에 세안하기
                NIGHT_STRETCHING=잠들기 전 스트레칭으로 혈액순환 돕기
                TAKE_SKIN_SUPPLEMENT=피부 영양제 챙겨 먹기
                CARRY_LIPBALM_SUNSTICK=립밤/선스틱 가방에 챙기기
                MORNING_VENTILATION=아침 환기 5분 시키기
                APPLY_SUNSCREEN_BEFORE_OUTING=외출 전 선크림 바르기
                """
)
public enum MorningRoutineSurveyOption {

    WATER_AFTER_WAKEUP("기상 직후 미온수 한 잔 마시기"),
    QUICK_WASH_AFTER_RETURN("귀가 후 10분 이내에 세안하기"),
    NIGHT_STRETCHING("잠들기 전 스트레칭으로 혈액순환 돕기"),
    TAKE_SKIN_SUPPLEMENT("피부 영양제 챙겨 먹기"),
    CARRY_LIPBALM_SUNSTICK("립밤/선스틱 가방에 챙기기"),
    MORNING_VENTILATION("아침 환기 5분 시키기"),
    APPLY_SUNSCREEN_BEFORE_OUTING("외출 전 선크림 바르기");

    private final String label;

    MorningRoutineSurveyOption(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static MorningRoutineSurveyOption from(String value) {
        for (MorningRoutineSurveyOption option : values()) {
            if (option.name().equalsIgnoreCase(value) || option.label.equals(value)) {
                return option;
            }
        }
        throw new IllegalArgumentException("Unknown morning routine survey option: " + value);
    }
}
