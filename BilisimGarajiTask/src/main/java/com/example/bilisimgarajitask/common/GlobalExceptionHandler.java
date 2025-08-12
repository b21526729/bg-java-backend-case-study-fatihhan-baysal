package com.example.bilisimgarajitask.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> notFound(NotFoundException ex, HttpServletRequest req){
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
    }
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<ApiError> validation(Exception ex, HttpServletRequest req){
        return build(HttpStatus.BAD_REQUEST, "Validation failed", req.getRequestURI());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> generic(Exception ex, HttpServletRequest req){
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", req.getRequestURI());
    }
    private ResponseEntity<ApiError> build(HttpStatus s, String msg, String path){
        return ResponseEntity.status(s).body(new ApiError(s.value(), s.getReasonPhrase(), msg, path, Instant.now()));
    }
}