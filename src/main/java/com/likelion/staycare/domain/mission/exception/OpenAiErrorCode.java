package com.likelion.staycare.domain.mission.exception;

import com.likelion.staycare.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OpenAiErrorCode implements BaseErrorCode {
    OPENAI_REQUEST_FAILED("OPENAI5001", "OpenAI API 요청 중 오류가 발생했습니다.", HttpStatus.BAD_GATEWAY),
    OPENAI_HTTP_ERROR("OPENAI5002", "OpenAI API가 오류 응답을 반환했습니다.", HttpStatus.BAD_GATEWAY),
    OPENAI_INVALID_RESPONSE("OPENAI5003", "OpenAI 응답 형식이 올바르지 않습니다.", HttpStatus.BAD_GATEWAY),
    OPENAI_JSON_PARSE_ERROR("OPENAI5004", "OpenAI 응답 JSON 파싱에 실패했습니다.", HttpStatus.BAD_GATEWAY),
    OPENAI_EMPTY_RESPONSE("OPENAI5005", "OpenAI 응답 본문이 비어 있습니다.", HttpStatus.BAD_GATEWAY);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
