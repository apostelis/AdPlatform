package com.example.adplatform.application.exception;

/**
 * Exception thrown when an advertisement operation fails.
 * This exception is typically thrown when an operation on an advertisement
 * (such as create, update, delete) fails due to a system or business rule error.
 */
public class AdvertisementOperationException extends AdvertisementException {

    private final OperationType operationType;

    /**
     * Enum representing the type of operation that failed.
     */
    public enum OperationType {
        CREATE,
        UPDATE,
        DELETE,
        RETRIEVE,
        TARGETING
    }

    /**
     * Constructs a new advertisement operation exception with the specified operation type.
     *
     * @param operationType the type of operation that failed
     */
    public AdvertisementOperationException(OperationType operationType) {
        super("Advertisement operation failed: " + operationType);
        this.operationType = operationType;
    }

    /**
     * Constructs a new advertisement operation exception with the specified operation type and message.
     *
     * @param operationType the type of operation that failed
     * @param message the detail message
     */
    public AdvertisementOperationException(OperationType operationType, String message) {
        super(message);
        this.operationType = operationType;
    }

    /**
     * Constructs a new advertisement operation exception with the specified operation type, message, and cause.
     *
     * @param operationType the type of operation that failed
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public AdvertisementOperationException(OperationType operationType, String message, Throwable cause) {
        super(message, cause);
        this.operationType = operationType;
    }

    /**
     * Gets the type of operation that failed.
     *
     * @return the operation type
     */
    public OperationType getOperationType() {
        return operationType;
    }
}