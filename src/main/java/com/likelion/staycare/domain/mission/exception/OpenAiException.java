package com.likelion.staycare.domain.mission.exception;

import com.likelion.staycare.global.exception.CustomException;
import lombok.Getter;

@Getter
public class OpenAiException extends CustomException {

    private final String detailMessage;

    public OpenAiException(OpenAiErrorCode errorCode) {
        super(errorCode);
        this.detailMessage = errorCode.getMessage();
    }

    public OpenAiException(OpenAiErrorCode errorCode, String detailMessage) {
        super(errorCode);
        this.detailMessage = detailMessage;
    }
}
