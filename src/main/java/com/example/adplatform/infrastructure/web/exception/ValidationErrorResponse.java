package com.example.adplatform.infrastructure.web.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Error response for validation errors.
 * This class extends the standard ErrorResponse to include field-specific validation errors.
 */
@Getter
@Setter
@NoArgsConstructor
public class ValidationErrorResponse extends ErrorResponse {
    
    /**
     * Map of field names to error messages.
     */
    private Map<String, String> errors;
    
    /**
     * Constructs a new validation error response.
     *
     * @param status the HTTP status code
     * @param message the error message
     * @param path the request path
     * @param timestamp the timestamp when the error occurred
     * @param errors the map of field names to error messages
     */
    public ValidationErrorResponse(int status, String message, String path, LocalDateTime timestamp, Map<String, String> errors) {
        super(status, message, path, timestamp);
        this.errors = errors;
    }
}