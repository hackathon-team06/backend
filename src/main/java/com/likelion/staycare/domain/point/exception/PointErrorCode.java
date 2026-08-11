package com.likelion.staycare.domain.point.exception;

import com.likelion.staycare.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PointErrorCode implements BaseErrorCode {
    POINT_PROCESS_FAILED("POINT5001", "포인트 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
