package com.likelion.staycare.global.exception;

import com.likelion.staycare.global.common.BaseResponse;
import com.likelion.staycare.global.exception.model.BaseErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<BaseResponse<Object>> handleCustomException(CustomException ex) {
        BaseErrorCode errorCode = ex.getErrorCode();
        log.warn("CustomException 발생: {} - {}", errorCode.getCode(), errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatus())
                .body(BaseResponse.error(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> String.format("[%s] %s", e.getField(), e.getDefaultMessage()))
                .collect(Collectors.joining(" / "));

        log.warn("Validation 오류 발생: {}", errorMessages);

        return ResponseEntity.badRequest()
                .body(BaseResponse.error(
                        GlobalErrorCode.INVALID_INPUT_VALUE.getCode(),
                        errorMessages
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleException(Exception ex) {
        log.error("Server 오류 발생", ex);

        return ResponseEntity.status(GlobalErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(BaseResponse.error(
                        GlobalErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                        GlobalErrorCode.INTERNAL_SERVER_ERROR.getMessage()
                ));
    }
}
