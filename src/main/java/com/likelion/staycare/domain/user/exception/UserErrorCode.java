package com.likelion.staycare.domain.user.exception;

import com.likelion.staycare.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
    USER_NOT_FOUND("USER4041", "해당 유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD("USER4011", "아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    DUPLICATE_LOGIN_ID("USER4091", "이미 사용 중인 아이디입니다.", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
