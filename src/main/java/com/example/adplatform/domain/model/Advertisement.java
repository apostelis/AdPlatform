package com.example.adplatform.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Domain entity representing an Advertisement in the system.
 * This is a persistence-ignorant domain entity following DDD principles.
 * Advertisements can be sourced from storage or YouTube and have
 * targeting rules based on geolocation, biographical data, and user mood.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Advertisement {

    private Long id;
    private String title;
    private String description;
    private String content;
    private AdvertisementSource source;
    private String sourceIdentifier; // File path or YouTube ID
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;

    // Targeting rules
    private Set<GeoTarget> geoTargets = new HashSet<>();
    private Set<BioTarget> bioTargets = new HashSet<>();
    private Set<MoodTarget> moodTargets = new HashSet<>();

    /**
     * Business method to check if this advertisement is valid for the given targeting criteria.
     */
    public boolean matchesTargeting(
            String countryCode,
            Integer age,
            String gender,
            String occupation,
            String educationLevel,
            String language,
            Set<String> interests,
            Mood mood,
            Integer intensity,
            String timeOfDay,
            String dayOfWeek,
            String season
    ) {
        return matchesGeoTargeting(countryCode, null, null, null, null)
                && matchesBioTargeting(age, gender, occupation, educationLevel, language, interests)
                && matchesMoodTargeting(mood, intensity, timeOfDay, dayOfWeek, season);
    }

    /**
     * Check if this advertisement matches the given geo targeting criteria.
     */
    public boolean matchesGeoTargeting(
            String countryCode,
            String region,
            String city,
            Double latitude,
            Double longitude
    ) {
        // If no geo targets are defined, the ad doesn't match specific geo targeting
        if (geoTargets == null || geoTargets.isEmpty()) {
            return false;
        }

        // Check for any matching include targets
        boolean hasIncludeMatch = geoTargets.stream()
                .filter(target -> target.isInclude())
                .anyMatch(target -> target.matches(countryCode, region, city, latitude, longitude));

        // Check for any matching exclude targets
        boolean hasExcludeMatch = geoTargets.stream()
                .filter(target -> !target.isInclude())
                .anyMatch(target -> target.matches(countryCode, region, city, latitude, longitude));

        // Ad matches if it has at least one include match and no exclude matches
        return hasIncludeMatch && !hasExcludeMatch;
    }

    /**
     * Check if this advertisement matches the given biographical targeting criteria.
     */
    public boolean matchesBioTargeting(
            Integer age,
            String genderStr,
            String occupation,
            String educationLevel,
            String language,
            Set<String> interests
    ) {
        // If no bio targets are defined, the ad doesn't match specific bio targeting
        if (bioTargets == null || bioTargets.isEmpty()) {
            return false;
        }

        final Gender gender = genderStr != null ? Gender.fromString(genderStr) : null;

        // Check for any matching include targets
        boolean hasIncludeMatch = bioTargets.stream()
                .filter(target -> target.isInclude())
                .anyMatch(target -> target.matches(age, gender, occupation, educationLevel, language, interests));

        // Check for any matching exclude targets
        boolean hasExcludeMatch = bioTargets.stream()
                .filter(target -> !target.isInclude())
                .anyMatch(target -> target.matches(age, gender, occupation, educationLevel, language, interests));

        // Ad matches if it has at least one include match and no exclude matches
        return hasIncludeMatch && !hasExcludeMatch;
    }

    /**
     * Check if this advertisement matches the given mood targeting criteria.
     */
    public boolean matchesMoodTargeting(
            Mood mood,
            Integer intensity,
            String timeOfDay,
            String dayOfWeek,
            String season
    ) {
        // If no mood targets are defined, the ad doesn't match specific mood targeting
        if (moodTargets == null || moodTargets.isEmpty()) {
            return false;
        }

        // Check for any matching include targets
        boolean hasIncludeMatch = moodTargets.stream()
                .filter(target -> target.isInclude())
                .anyMatch(target -> target.matches(mood, intensity, timeOfDay, dayOfWeek, season));

        // Check for any matching exclude targets
        boolean hasExcludeMatch = moodTargets.stream()
                .filter(target -> !target.isInclude())
                .anyMatch(target -> target.matches(mood, intensity, timeOfDay, dayOfWeek, season));

        // Ad matches if it has at least one include match and no exclude matches
        return hasIncludeMatch && !hasExcludeMatch;
    }
}
