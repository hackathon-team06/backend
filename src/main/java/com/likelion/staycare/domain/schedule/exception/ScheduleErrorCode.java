package com.likelion.staycare.domain.schedule.exception;

import com.likelion.staycare.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ScheduleErrorCode implements BaseErrorCode {
    SCHEDULE_NOT_FOUND("SCHEDULE4041", "해당 일정을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_SCHEDULE_DATE_RANGE("SCHEDULE4001", "startDate는 endDate보다 늦을 수 없습니다.", HttpStatus.BAD_REQUEST),
    SCHEDULE_DATE_CONFLICT("SCHEDULE4091", "해당 기간과 겹치는 일정이 이미 존재합니다.", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
