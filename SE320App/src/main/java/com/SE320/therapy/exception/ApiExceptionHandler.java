package com.SE320.therapy.exception;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.SE320.therapy.dto.ApiErrorDetail;
import com.SE320.therapy.dto.ApiErrorEnvelope;
import com.SE320.therapy.dto.ApiErrorResponse;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorEnvelope> handleApiException(ApiException ex) {
        return build(ex.getStatus(), ex.getCode(), ex.getMessage(), ex.getDetails());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorEnvelope> handleValidation(MethodArgumentNotValidException ex) {
        List<ApiErrorDetail> details = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(this::toDetail)
            .toList();

        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Invalid input provided", details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorEnvelope> handleConstraintViolation(ConstraintViolationException ex) {
        List<ApiErrorDetail> details = ex.getConstraintViolations()
            .stream()
            .map(violation -> new ApiErrorDetail(
                violation.getPropertyPath() == null ? null : violation.getPropertyPath().toString(),
                violation.getMessage()
            ))
            .toList();

        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Invalid input provided", details);
    }

    @ExceptionHandler({ IllegalArgumentException.class, MethodArgumentTypeMismatchException.class })
    public ResponseEntity<ApiErrorEnvelope> handleBadRequest(Exception ex) {
        String message = ex.getMessage() == null || ex.getMessage().isBlank()
            ? "Invalid request."
            : ex.getMessage();
        return build(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", message, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorEnvelope> handleUnexpected(Exception ex) {
        return build(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred.",
            List.of()
        );
    }

    private ApiErrorDetail toDetail(FieldError error) {
        String message = error.getDefaultMessage() == null || error.getDefaultMessage().isBlank()
            ? "Invalid value"
            : error.getDefaultMessage();
        return new ApiErrorDetail(error.getField(), message);
    }

    private ResponseEntity<ApiErrorEnvelope> build(
        HttpStatus status,
        String code,
        String message,
        List<ApiErrorDetail> details
    ) {
        return ResponseEntity.status(status)
            .body(new ApiErrorEnvelope(new ApiErrorResponse(code, message, details, Instant.now())));
    }
}
