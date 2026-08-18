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
    MORNING_ROUTINE_NOT_FOUND("MISSION4043", "아침 루틴이 아직 설정되지 않았습니다.", HttpStatus.NOT_FOUND),
    MORNING_ROUTINE_ITEM_NOT_FOUND("MISSION4044", "아침 루틴 항목을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FORBIDDEN_MISSION("MISSION4031", "해당 미션에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),
    MORNING_MISSION_TIME_CLOSED("MISSION4001", "아침 미션 생성 가능 시간이 지났습니다.", HttpStatus.BAD_REQUEST),
    EVENING_MISSION_TIME_ONLY("MISSION4002", "저녁 시간에만 저녁 미션을 생성할 수 있습니다.", HttpStatus.BAD_REQUEST),
    INVALID_MORNING_ROUTINE_SIZE("MISSION4003", "아침 루틴 요청 개수가 현재 단계와 맞지 않습니다.", HttpStatus.BAD_REQUEST),
    MISSION_STEP_COMPLETION_NOT_ALLOWED("MISSION4004", "현재 이 미션 단계는 완료 처리할 수 없습니다.", HttpStatus.BAD_REQUEST),
    MORNING_ROUTINE_ALREADY_FULL("MISSION4005", "현재 고정 아침 미션이 이미 3개입니다. 먼저 삭제 후 추가해 주세요.", HttpStatus.BAD_REQUEST),
    EVENING_MISSION_ALREADY_FULL("MISSION4006", "현재 수정 가능한 저녁 미션이 이미 3개입니다.", HttpStatus.BAD_REQUEST),
    EVENING_MISSION_STEP_EDIT_NOT_ALLOWED("MISSION4007", "이 저녁 미션 단계는 수정할 수 없습니다.", HttpStatus.BAD_REQUEST),
    EVENING_MISSION_ADD_SIZE_INVALID("MISSION4008", "추가하려는 저녁 미션 개수가 현재 비어 있는 슬롯 수와 맞지 않습니다.", HttpStatus.BAD_REQUEST),
    MORNING_ROUTINE_SURVEY_ALREADY_COMPLETED("MISSION4091", "이미 아침 루틴 설문이 완료되었습니다.", HttpStatus.CONFLICT),
    MISSION_STEP_PROCESS_FAILED("MISSION5001", "미션 단계 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
