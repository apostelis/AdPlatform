package com.example.adplatform.domain.model;

/**
 * Enum representing different moods for targeting advertisements.
 * This is a domain enum following DDD principles.
 */
public enum Mood {
    /**
     * Happy, joyful mood.
     */
    HAPPY,
    
    /**
     * Sad, melancholic mood.
     */
    SAD,
    
    /**
     * Excited, enthusiastic mood.
     */
    EXCITED,
    
    /**
     * Relaxed, calm mood.
     */
    RELAXED,
    
    /**
     * Angry, frustrated mood.
     */
    ANGRY,
    
    /**
     * Neutral, balanced mood.
     */
    NEUTRAL;
    
    /**
     * Convert a string to a Mood enum value, case-insensitive.
     * 
     * @param moodStr The string representation of the mood
     * @return The corresponding Mood enum value, or null if not found
     */
    public static Mood fromString(String moodStr) {
        if (moodStr == null) {
            return null;
        }
        
        try {
            return Mood.valueOf(moodStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}