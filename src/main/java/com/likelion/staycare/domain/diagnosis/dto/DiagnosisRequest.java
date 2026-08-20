package com.likelion.staycare.domain.diagnosis.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion.staycare.domain.diagnosis.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record DiagnosisRequest(
        @NotNull
        @Schema(description = "성별", example = "남성")
        Gender gender,

        @NotNull
        @Schema(description = "나이", example = "24")
        Integer age,

        @NotNull
        @Schema(description = "피부 타입", example = "건성")
        SkinType skinType,

        @NotNull
        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "string", example = "06:00", description = "기상 시간")
        LocalTime wakeUpTime,

        @NotNull
        @JsonFormat(pattern = "HH:mm")
        @Schema(type = "string", example = "18:00", description = "귀가 시간")
        LocalTime returnHomeTime,

        @NotNull
        @Schema(description = "체크 주기", example = "7일")
        CheckCycle checkCycle,

        @NotNull
        @Schema(description = "관리 목표", example = "촉촉한 피부")
        CareMotivation careMotivation
) {
}
