package com.rentalhouse.exception;

import com.rentalhouse.dto.ApiDtos.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.rentalhouse.exception.AppExceptions.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiResponse> notFound(ResourceNotFoundException ex) { return response(HttpStatus.NOT_FOUND, ex.getMessage()); }

    @ExceptionHandler(DuplicateEmailException.class)
    ResponseEntity<ApiResponse> duplicate(DuplicateEmailException ex) { return response(HttpStatus.CONFLICT, ex.getMessage()); }

    @ExceptionHandler(InvalidRequestException.class)
    ResponseEntity<ApiResponse> invalid(InvalidRequestException ex) { return response(HttpStatus.BAD_REQUEST, ex.getMessage()); }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ApiResponse> authenticationFailure() { return response(HttpStatus.UNAUTHORIZED, "Invalid email or password"); }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiResponse> forbidden() { return response(HttpStatus.FORBIDDEN, "You are not authorized to perform this action"); }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiResponse> status(ResponseStatusException ex) {
        return response(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getReason() == null ? "Request failed" : ex.getReason());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("message", "Validation failed");
        body.put("path", request.getRequestURI());
        body.put("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse> unexpected(Exception ex) {
        log.error("Unexpected request failure", ex);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong on the server");
    }

    private ResponseEntity<ApiResponse> response(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ApiResponse(false, message, null));
    }
}