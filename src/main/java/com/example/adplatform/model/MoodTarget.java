package com.example.adplatform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embeddable class representing mood-based targeting criteria for advertisements.
 * This allows advertisements to be targeted based on user's emotional state or context.
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoodTarget {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Mood mood;
    
    private Integer intensityMin; // 1-10 scale
    
    private Integer intensityMax; // 1-10 scale
    
    private String timeOfDay; // Morning, Afternoon, Evening, Night
    
    private String dayOfWeek; // Monday, Tuesday, etc.
    
    private String season; // Spring, Summer, Fall, Winter
    
    @Column(nullable = false)
    private boolean include; // True for inclusion, false for exclusion
    
    /**
     * Enum representing different mood states for targeting.
     */
    public enum Mood {
        HAPPY,
        SAD,
        EXCITED,
        RELAXED,
        STRESSED,
        BORED,
        FOCUSED,
        TIRED,
        ENERGETIC,
        NEUTRAL
    }
}