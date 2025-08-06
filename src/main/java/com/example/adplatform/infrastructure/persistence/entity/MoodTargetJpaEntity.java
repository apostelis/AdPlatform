package com.example.adplatform.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embeddable JPA entity representing mood-based targeting criteria for advertisements.
 * This is an infrastructure entity in the hexagonal architecture.
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoodTargetJpaEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MoodJpaEnum mood;
    
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
    public enum MoodJpaEnum {
        HAPPY,
        SAD,
        EXCITED,
        RELAXED,
        ANGRY,
        NEUTRAL
    }
}