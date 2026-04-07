package com.SE320.therapy.exception;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import com.SE320.therapy.dto.ApiErrorDetail;
import com.SE320.therapy.dto.ApiErrorEnvelope;
import com.SE320.therapy.dto.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorEnvelope> handleApiException(ApiException ex, HttpServletRequest request) {
        logHandledException(request, ex.getStatus(), ex.getCode(), ex);
        return build(ex.getStatus(), ex.getCode(), ex.getMessage(), ex.getDetails());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorEnvelope> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ApiErrorDetail> details = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(this::toDetail)
            .toList();

        logHandledException(request, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex);
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Invalid input provided", details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorEnvelope> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        List<ApiErrorDetail> details = ex.getConstraintViolations()
            .stream()
            .map(violation -> new ApiErrorDetail(
                violation.getPropertyPath() == null ? null : violation.getPropertyPath().toString(),
                violation.getMessage()
            ))
            .toList();

        logHandledException(request, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex);
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Invalid input provided", details);
    }

    @ExceptionHandler({ IllegalArgumentException.class, MethodArgumentTypeMismatchException.class })
    public ResponseEntity<ApiErrorEnvelope> handleBadRequest(Exception ex, HttpServletRequest request) {
        String message = ex.getMessage() == null || ex.getMessage().isBlank()
            ? "Invalid request."
            : ex.getMessage();
        logHandledException(request, HttpStatus.BAD_REQUEST, "INVALID_REQUEST", ex);
        return build(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", message, List.of());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorEnvelope> handleMissingParameter(
        MissingServletRequestParameterException ex,
        HttpServletRequest request
    ) {
        logHandledException(request, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex);
        return build(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            "Invalid input provided",
            List.of(new ApiErrorDetail(ex.getParameterName(), "is required"))
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorEnvelope> handleUnreadableMessage(
        HttpMessageNotReadableException ex,
        HttpServletRequest request
    ) {
        logHandledException(request, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex);
        return build(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            "Invalid input provided",
            List.of(new ApiErrorDetail(null, "Request body is missing or malformed"))
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorEnvelope> handleResponseStatusException(
        ResponseStatusException ex,
        HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String code = resolveCode(status);
        String message = ex.getReason() == null || ex.getReason().isBlank()
            ? status.getReasonPhrase()
            : ex.getReason();
        logHandledException(request, status, code, ex);
        return build(status, code, message, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorEnvelope> handleUnexpected(Exception ex, HttpServletRequest request) {
        logUnhandledException(request, ex);
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

    private void logHandledException(HttpServletRequest request, HttpStatus status, String code, Exception ex) {
        String method = request == null ? "unknown" : request.getMethod();
        String path = request == null ? "unknown" : request.getRequestURI();
        if (status.is5xxServerError()) {
            log.error("Request failed: method={} path={} status={} code={}", method, path, status.value(), code, ex);
            return;
        }
        log.warn(
            "Request handled with client error: method={} path={} status={} code={} message={}",
            method,
            path,
            status.value(),
            code,
            ex.getMessage()
        );
    }

    private void logUnhandledException(HttpServletRequest request, Exception ex) {
        String method = request == null ? "unknown" : request.getMethod();
        String path = request == null ? "unknown" : request.getRequestURI();
        log.error("Unhandled exception for method={} path={}", method, path, ex);
    }

    private String resolveCode(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> "INVALID_REQUEST";
            case UNAUTHORIZED -> "UNAUTHORIZED";
            case FORBIDDEN -> "FORBIDDEN";
            case NOT_FOUND -> "NOT_FOUND";
            case CONFLICT -> "CONFLICT";
            default -> status.is4xxClientError() ? "CLIENT_ERROR" : "INTERNAL_SERVER_ERROR";
        };
    }
}
