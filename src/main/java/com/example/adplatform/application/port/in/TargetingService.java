package com.example.adplatform.application.port.in;

import com.example.adplatform.domain.model.Advertisement;
import com.example.adplatform.domain.model.Mood;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service interface for handling advertisement targeting logic.
 * This service is responsible for filtering advertisements based on various targeting criteria.
 */
public interface TargetingService {

    /**
     * Filters advertisements based on combined targeting criteria.
     *
     * @param advertisements the list of advertisements to filter
     * @param countryCode the country code for geo targeting
     * @param userBioData a map of user biographical data for bio targeting
     * @param mood the user's mood for mood targeting
     * @return a list of advertisements that match the targeting criteria
     */
    List<Advertisement> filterByTargetingCriteria(
            List<Advertisement> advertisements,
            String countryCode,
            Map<String, Object> userBioData,
            Mood mood
    );

    /**
     * Filters advertisements based on geographical targeting criteria.
     *
     * @param advertisements the list of advertisements to filter
     * @param countryCode the country code
     * @param region the region or state
     * @param city the city
     * @param latitude the latitude coordinate
     * @param longitude the longitude coordinate
     * @return a list of advertisements that match the geo targeting criteria
     */
    List<Advertisement> filterByGeoTargeting(
            List<Advertisement> advertisements,
            String countryCode,
            String region,
            String city,
            Double latitude,
            Double longitude
    );

    /**
     * Filters advertisements based on biographical targeting criteria.
     *
     * @param advertisements the list of advertisements to filter
     * @param age the user's age
     * @param gender the user's gender
     * @param occupation the user's occupation
     * @param educationLevel the user's education level
     * @param language the user's preferred language
     * @param interests the user's interests
     * @return a list of advertisements that match the bio targeting criteria
     */
    List<Advertisement> filterByBioTargeting(
            List<Advertisement> advertisements,
            Integer age,
            String gender,
            String occupation,
            String educationLevel,
            String language,
            Set<String> interests
    );

    /**
     * Filters advertisements based on mood targeting criteria.
     *
     * @param advertisements the list of advertisements to filter
     * @param mood the user's mood
     * @param intensity the intensity of the mood
     * @param timeOfDay the time of day
     * @param dayOfWeek the day of the week
     * @param season the season
     * @return a list of advertisements that match the mood targeting criteria
     */
    List<Advertisement> filterByMoodTargeting(
            List<Advertisement> advertisements,
            Mood mood,
            Integer intensity,
            String timeOfDay,
            String dayOfWeek,
            String season
    );

    /**
     * Checks if an advertisement matches the given targeting criteria.
     *
     * @param advertisement the advertisement to check
     * @param countryCode the country code for geo targeting
     * @param age the user's age for bio targeting
     * @param gender the user's gender for bio targeting
     * @param occupation the user's occupation for bio targeting
     * @param educationLevel the user's education level for bio targeting
     * @param language the user's preferred language for bio targeting
     * @param interests the user's interests for bio targeting
     * @param mood the user's mood for mood targeting
     * @param intensity the intensity of the mood for mood targeting
     * @param timeOfDay the time of day for mood targeting
     * @param dayOfWeek the day of the week for mood targeting
     * @param season the season for mood targeting
     * @return true if the advertisement matches the targeting criteria, false otherwise
     */
    boolean matchesTargeting(
            Advertisement advertisement,
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
    );

    /**
     * Checks if an advertisement matches the given geo targeting criteria.
     *
     * @param advertisement the advertisement to check
     * @param countryCode the country code
     * @param region the region or state
     * @param city the city
     * @param latitude the latitude coordinate
     * @param longitude the longitude coordinate
     * @return true if the advertisement matches the geo targeting criteria, false otherwise
     */
    boolean matchesGeoTargeting(
            Advertisement advertisement,
            String countryCode,
            String region,
            String city,
            Double latitude,
            Double longitude
    );

    /**
     * Checks if an advertisement matches the given biographical targeting criteria.
     *
     * @param advertisement the advertisement to check
     * @param age the user's age
     * @param gender the user's gender
     * @param occupation the user's occupation
     * @param educationLevel the user's education level
     * @param language the user's preferred language
     * @param interests the user's interests
     * @return true if the advertisement matches the bio targeting criteria, false otherwise
     */
    boolean matchesBioTargeting(
            Advertisement advertisement,
            Integer age,
            String gender,
            String occupation,
            String educationLevel,
            String language,
            Set<String> interests
    );

    /**
     * Checks if an advertisement matches the given mood targeting criteria.
     *
     * @param advertisement the advertisement to check
     * @param mood the user's mood
     * @param intensity the intensity of the mood
     * @param timeOfDay the time of day
     * @param dayOfWeek the day of the week
     * @param season the season
     * @return true if the advertisement matches the mood targeting criteria, false otherwise
     */
    boolean matchesMoodTargeting(
            Advertisement advertisement,
            Mood mood,
            Integer intensity,
            String timeOfDay,
            String dayOfWeek,
            String season
    );
}