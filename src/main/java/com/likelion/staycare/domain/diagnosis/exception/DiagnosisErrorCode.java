package com.likelion.staycare.domain.diagnosis.exception;

import com.likelion.staycare.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DiagnosisErrorCode implements BaseErrorCode {
    DIAGNOSIS_NOT_FOUND("DIAG4041", "진단 이력을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FORBIDDEN_DIAGNOSIS("DIAG4031", "해당 진단에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
