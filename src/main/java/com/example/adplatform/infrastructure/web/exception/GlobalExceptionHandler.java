package com.example.adplatform.infrastructure.web.exception;

import com.example.adplatform.application.exception.AdvertisementNotFoundException;
import com.example.adplatform.application.exception.AdvertisementOperationException;
import com.example.adplatform.application.exception.AdvertisementValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * This class handles exceptions thrown by the application and converts them
 * to appropriate HTTP responses with structured error information.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles AdvertisementNotFoundException.
     *
     * @param ex the exception
     * @param request the web request
     * @return a ResponseEntity with error details
     */
    @ExceptionHandler(AdvertisementNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAdvertisementNotFoundException(
            AdvertisementNotFoundException ex, WebRequest request) {
        log.error("Advertisement not found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles AdvertisementValidationException.
     *
     * @param ex the exception
     * @param request the web request
     * @return a ResponseEntity with error details
     */
    @ExceptionHandler(AdvertisementValidationException.class)
    public ResponseEntity<ValidationErrorResponse> handleAdvertisementValidationException(
            AdvertisementValidationException ex, WebRequest request) {
        log.error("Advertisement validation failed: {}", ex.getMessage());
        
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now(),
                ex.getErrors()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles AdvertisementOperationException.
     *
     * @param ex the exception
     * @param request the web request
     * @return a ResponseEntity with error details
     */
    @ExceptionHandler(AdvertisementOperationException.class)
    public ResponseEntity<ErrorResponse> handleAdvertisementOperationException(
            AdvertisementOperationException ex, WebRequest request) {
        log.error("Advertisement operation failed: {} - {}", ex.getOperationType(), ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles validation errors from @Valid annotations.
     *
     * @param ex the exception
     * @param request the web request
     * @return a ResponseEntity with error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                request.getDescription(false),
                LocalDateTime.now(),
                errors
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all other exceptions.
     *
     * @param ex the exception
     * @param request the web request
     * @return a ResponseEntity with error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Unhandled exception: ", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                request.getDescription(false),
                LocalDateTime.now()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}