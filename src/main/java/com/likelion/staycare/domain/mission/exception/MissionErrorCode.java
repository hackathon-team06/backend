package com.likelion.staycare.domain.mission.exception;

import com.likelion.staycare.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MissionErrorCode implements BaseErrorCode {
    MISSION_NOT_FOUND("MISSION4041", "미션을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    MISSION_STEP_NOT_FOUND("MISSION4042", "미션 단계를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FORBIDDEN_MISSION("MISSION4031", "해당 미션에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    MORNING_MISSION_TIME_CLOSED("MISSION4001", "아침 미션 시간이 종료되었습니다.", HttpStatus.BAD_REQUEST),
    EVENING_MISSION_TIME_ONLY("MISSION4002", "저녁 미션 시간입니다.", HttpStatus.BAD_REQUEST),
    MISSION_STEP_PROCESS_FAILED("MISSION5001", "미션 단계 완료 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
