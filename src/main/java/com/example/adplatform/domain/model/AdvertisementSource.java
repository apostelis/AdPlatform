package com.example.adplatform.domain.model;

/**
 * Enum representing the source of an advertisement.
 * This is a domain enum following DDD principles.
 */
public enum AdvertisementSource {
    /**
     * Advertisement content is stored in the local storage system.
     */
    STORAGE,
    
    /**
     * Advertisement content is sourced from YouTube.
     */
    YOUTUBE
}