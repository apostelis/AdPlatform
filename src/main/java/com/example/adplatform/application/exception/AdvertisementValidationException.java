package com.example.adplatform.application.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception thrown when advertisement validation fails.
 * This exception is typically thrown when attempting to create or update an advertisement
 * with invalid data that does not meet the business rules or constraints.
 */
public class AdvertisementValidationException extends AdvertisementException {

    private final Map<String, String> errors;

    /**
     * Constructs a new advertisement validation exception with a default message.
     */
    public AdvertisementValidationException() {
        super("Advertisement validation failed");
        this.errors = new HashMap<>();
    }

    /**
     * Constructs a new advertisement validation exception with a custom message.
     *
     * @param message the detail message
     */
    public AdvertisementValidationException(String message) {
        super(message);
        this.errors = new HashMap<>();
    }

    /**
     * Constructs a new advertisement validation exception with a custom message and validation errors.
     *
     * @param message the detail message
     * @param errors a map of field names to error messages
     */
    public AdvertisementValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }

    /**
     * Adds a validation error for a specific field.
     *
     * @param field the field name
     * @param errorMessage the error message
     * @return this exception instance for method chaining
     */
    public AdvertisementValidationException addError(String field, String errorMessage) {
        this.errors.put(field, errorMessage);
        return this;
    }

    /**
     * Gets the validation errors.
     *
     * @return a map of field names to error messages
     */
    public Map<String, String> getErrors() {
        return errors;
    }
}