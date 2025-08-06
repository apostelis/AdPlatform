package com.example.adplatform.domain.model;

/**
 * Enum representing gender options for biographical targeting.
 * This is a domain enum following DDD principles.
 */
public enum Gender {
    /**
     * Male gender.
     */
    MALE,
    
    /**
     * Female gender.
     */
    FEMALE,
    
    /**
     * Non-binary gender.
     */
    NON_BINARY,
    
    /**
     * Represents all genders (for targeting all genders).
     */
    ALL;
    
    /**
     * Convert a string to a Gender enum value, case-insensitive.
     * 
     * @param genderStr The string representation of the gender
     * @return The corresponding Gender enum value, or null if not found
     */
    public static Gender fromString(String genderStr) {
        if (genderStr == null) {
            return null;
        }
        
        try {
            return Gender.valueOf(genderStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}