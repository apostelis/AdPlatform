package com.example.adplatform.infrastructure.web.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard error response for API errors.
 * This class represents the structure of error responses sent to clients
 * when an exception occurs during API processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    /**
     * The HTTP status code of the response.
     */
    private int status;
    
    /**
     * The error message.
     */
    private String message;
    
    /**
     * The path of the request that caused the error.
     */
    private String path;
    
    /**
     * The timestamp when the error occurred.
     */
    private LocalDateTime timestamp;
}