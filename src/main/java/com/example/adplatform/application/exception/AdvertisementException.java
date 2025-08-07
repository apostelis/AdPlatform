package com.example.adplatform.application.exception;

/**
 * Base exception class for all advertisement-related exceptions.
 * This class serves as the parent for all specific exception types in the advertisement domain.
 */
public class AdvertisementException extends RuntimeException {

    /**
     * Constructs a new advertisement exception with the specified detail message.
     *
     * @param message the detail message
     */
    public AdvertisementException(String message) {
        super(message);
    }

    /**
     * Constructs a new advertisement exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public AdvertisementException(String message, Throwable cause) {
        super(message, cause);
    }
}