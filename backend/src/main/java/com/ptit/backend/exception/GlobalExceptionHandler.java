package com.ptit.backend.exception;

import com.ptit.backend.dto.response.ApiResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Object>> handleAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse<Object> response = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(exception.getMessage() != null ? exception.getMessage() : errorCode.getMessage())
                .result(null)
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ApiResponse<Object> response = ApiResponse.builder()
                .code(ErrorCode.INVALID_REQUEST.getCode())
                .message("Du lieu dau vao khong hop le")
                .result(errors)
                .build();

        return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getHttpStatus()).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );

        ApiResponse<Object> response = ApiResponse.builder()
                .code(ErrorCode.INVALID_REQUEST.getCode())
                .message("Du lieu dau vao khong hop le")
                .result(errors)
                .build();

        return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getHttpStatus()).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        ApiResponse<Object> response = ApiResponse.builder()
                .code(ErrorCode.INVALID_REQUEST.getCode())
                .message("Dinh dang du lieu gui len khong hop le")
                .result(null)
                .build();

        return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getHttpStatus()).body(response);
    }


    @ExceptionHandler(org.springframework.ai.retry.NonTransientAiException.class)
    public ResponseEntity<ApiResponse<Object>> handleNonTransientAiException(
            org.springframework.ai.retry.NonTransientAiException exception) {
        String msg = exception.getMessage() != null ? exception.getMessage().toLowerCase() : "";

        ErrorCode errorCode;
        if (msg.contains("429") || msg.contains("quota") || msg.contains("rate limit")) {
            errorCode = ErrorCode.CHATBOT_AI_QUOTA_EXCEEDED;
        } else {
            errorCode = ErrorCode.CHATBOT_AI_UNAVAILABLE;
        }

        ApiResponse<Object> response = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .result(null)
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }


    @ExceptionHandler(com.fasterxml.jackson.core.JsonProcessingException.class)
    public ResponseEntity<ApiResponse<Object>> handleJsonParseException(
            com.fasterxml.jackson.core.JsonProcessingException exception) {
        ApiResponse<Object> response = ApiResponse.builder()
                .code(ErrorCode.CHATBOT_INVALID_RESPONSE.getCode())
                .message(ErrorCode.CHATBOT_INVALID_RESPONSE.getMessage())
                .result(null)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception exception) {
        ApiResponse<Object> response = ApiResponse.builder()
                .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                .result(null)
                .build();

        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getHttpStatus()).body(response);
    }
}

