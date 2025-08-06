package com.example.adplatform.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain value object representing mood-based targeting criteria for advertisements.
 * This allows advertisements to be targeted based on user's emotional state or context.
 * This is a persistence-ignorant domain class following DDD principles.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoodTarget {

    private Mood mood;
    private Integer intensityMin; // 1-10 scale
    private Integer intensityMax; // 1-10 scale
    private String timeOfDay; // Morning, Afternoon, Evening, Night
    private String dayOfWeek; // Monday, Tuesday, etc.
    private String season; // Spring, Summer, Fall, Winter
    private boolean include; // True for inclusion, false for exclusion

    /**
     * Check if this mood target matches the given mood criteria.
     *
     * @param mood The mood to check
     * @param intensity The intensity to check
     * @param timeOfDay The time of day to check
     * @param dayOfWeek The day of week to check
     * @param season The season to check
     * @return True if the mood data matches this target
     */
    public boolean matches(
            Mood mood,
            Integer intensity,
            String timeOfDay,
            String dayOfWeek,
            String season
    ) {
        // Mood match
        if (mood != null && this.mood != null) {
            if (this.mood != mood) {
                return false;
            }
        }

        // Intensity range match
        if (intensity != null && (intensityMin != null || intensityMax != null)) {
            if (intensityMin != null && intensity < intensityMin) {
                return false;
            }
            if (intensityMax != null && intensity > intensityMax) {
                return false;
            }
        }

        // Time of day match
        if (timeOfDay != null && this.timeOfDay != null) {
            if (!this.timeOfDay.equalsIgnoreCase(timeOfDay)) {
                return false;
            }
        }

        // Day of week match
        if (dayOfWeek != null && this.dayOfWeek != null) {
            if (!this.dayOfWeek.equalsIgnoreCase(dayOfWeek)) {
                return false;
            }
        }

        // Season match
        if (season != null && this.season != null) {
            if (!this.season.equalsIgnoreCase(season)) {
                return false;
            }
        }

        return true;
    }
}