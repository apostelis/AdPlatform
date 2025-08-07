package com.example.adplatform.application.exception;

/**
 * Exception thrown when an advertisement cannot be found.
 * This exception is typically thrown when attempting to retrieve, update, or delete
 * an advertisement that does not exist in the system.
 */
public class AdvertisementNotFoundException extends AdvertisementException {

    /**
     * Constructs a new advertisement not found exception with a default message.
     */
    public AdvertisementNotFoundException() {
        super("Advertisement not found");
    }

    /**
     * Constructs a new advertisement not found exception with the specified advertisement ID.
     *
     * @param id the ID of the advertisement that was not found
     */
    public AdvertisementNotFoundException(Long id) {
        super("Advertisement not found with id: " + id);
    }

    /**
     * Constructs a new advertisement not found exception with a custom message.
     *
     * @param message the detail message
     */
    public AdvertisementNotFoundException(String message) {
        super(message);
    }
}