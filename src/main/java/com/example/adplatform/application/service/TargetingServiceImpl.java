package com.example.adplatform.application.service;

import com.example.adplatform.application.port.in.TargetingService;
import com.example.adplatform.domain.model.Advertisement;
import com.example.adplatform.domain.model.Gender;
import com.example.adplatform.domain.model.Mood;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the TargetingService interface.
 * This service encapsulates the targeting logic for advertisements.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TargetingServiceImpl implements TargetingService {

    @Override
    public List<Advertisement> filterByTargetingCriteria(
            List<Advertisement> advertisements,
            String countryCode,
            Map<String, Object> userBioData,
            Mood mood
    ) {
        log.debug("Filtering advertisements by targeting criteria: country={}, mood={}", countryCode, mood);
        
        if (advertisements == null || advertisements.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Start with all advertisements
        List<Advertisement> result = new ArrayList<>(advertisements);
        
        // Apply bio targeting if user data is available
        if (userBioData != null && !userBioData.isEmpty()) {
            Integer age = (Integer) userBioData.getOrDefault("age", null);
            String gender = (String) userBioData.getOrDefault("gender", null);
            String occupation = (String) userBioData.getOrDefault("occupation", null);
            String educationLevel = (String) userBioData.getOrDefault("educationLevel", null);
            String language = (String) userBioData.getOrDefault("language", null);
            @SuppressWarnings("unchecked")
            Set<String> interests = new HashSet<>((List<String>) userBioData.getOrDefault("interests", Collections.emptyList()));
            
            // Filter by bio targeting
            result = result.stream()
                    .filter(ad -> matchesBioTargeting(ad, age, gender, occupation, educationLevel, language, interests))
                    .collect(Collectors.toList());
        }
        
        return result;
    }

    @Override
    public List<Advertisement> filterByGeoTargeting(
            List<Advertisement> advertisements,
            String countryCode,
            String region,
            String city,
            Double latitude,
            Double longitude
    ) {
        log.debug("Filtering advertisements by geo targeting: country={}, region={}, city={}", countryCode, region, city);
        
        if (advertisements == null || advertisements.isEmpty()) {
            return Collections.emptyList();
        }
        
        return advertisements.stream()
                .filter(ad -> matchesGeoTargeting(ad, countryCode, region, city, latitude, longitude))
                .collect(Collectors.toList());
    }

    @Override
    public List<Advertisement> filterByBioTargeting(
            List<Advertisement> advertisements,
            Integer age,
            String gender,
            String occupation,
            String educationLevel,
            String language,
            Set<String> interests
    ) {
        log.debug("Filtering advertisements by bio targeting: age={}, gender={}, occupation={}", age, gender, occupation);
        
        if (advertisements == null || advertisements.isEmpty()) {
            return Collections.emptyList();
        }
        
        return advertisements.stream()
                .filter(ad -> matchesBioTargeting(ad, age, gender, occupation, educationLevel, language, interests))
                .collect(Collectors.toList());
    }

    @Override
    public List<Advertisement> filterByMoodTargeting(
            List<Advertisement> advertisements,
            Mood mood,
            Integer intensity,
            String timeOfDay,
            String dayOfWeek,
            String season
    ) {
        log.debug("Filtering advertisements by mood targeting: mood={}, intensity={}, timeOfDay={}", mood, intensity, timeOfDay);
        
        if (advertisements == null || advertisements.isEmpty()) {
            return Collections.emptyList();
        }
        
        return advertisements.stream()
                .filter(ad -> matchesMoodTargeting(ad, mood, intensity, timeOfDay, dayOfWeek, season))
                .collect(Collectors.toList());
    }

    @Override
    public boolean matchesTargeting(
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
    ) {
        return matchesGeoTargeting(advertisement, countryCode, null, null, null, null)
                && matchesBioTargeting(advertisement, age, gender, occupation, educationLevel, language, interests)
                && matchesMoodTargeting(advertisement, mood, intensity, timeOfDay, dayOfWeek, season);
    }

    @Override
    public boolean matchesGeoTargeting(
            Advertisement advertisement,
            String countryCode,
            String region,
            String city,
            Double latitude,
            Double longitude
    ) {
        // If no geo targets are defined, the ad doesn't match specific geo targeting
        if (advertisement.getGeoTargets() == null || advertisement.getGeoTargets().isEmpty()) {
            return false;
        }

        // Check for any matching include targets
        boolean hasIncludeMatch = advertisement.getGeoTargets().stream()
                .filter(target -> target.isInclude())
                .anyMatch(target -> target.matches(countryCode, region, city, latitude, longitude));

        // Check for any matching exclude targets
        boolean hasExcludeMatch = advertisement.getGeoTargets().stream()
                .filter(target -> !target.isInclude())
                .anyMatch(target -> target.matches(countryCode, region, city, latitude, longitude));

        // Ad matches if it has at least one include match and no exclude matches
        return hasIncludeMatch && !hasExcludeMatch;
    }

    @Override
    public boolean matchesBioTargeting(
            Advertisement advertisement,
            Integer age,
            String genderStr,
            String occupation,
            String educationLevel,
            String language,
            Set<String> interests
    ) {
        // If no bio targets are defined, the ad doesn't match specific bio targeting
        if (advertisement.getBioTargets() == null || advertisement.getBioTargets().isEmpty()) {
            return false;
        }

        final Gender gender = genderStr != null ? Gender.fromString(genderStr) : null;

        // Check for any matching include targets
        boolean hasIncludeMatch = advertisement.getBioTargets().stream()
                .filter(target -> target.isInclude())
                .anyMatch(target -> target.matches(age, gender, occupation, educationLevel, language, interests));

        // Check for any matching exclude targets
        boolean hasExcludeMatch = advertisement.getBioTargets().stream()
                .filter(target -> !target.isInclude())
                .anyMatch(target -> target.matches(age, gender, occupation, educationLevel, language, interests));

        // Ad matches if it has at least one include match and no exclude matches
        return hasIncludeMatch && !hasExcludeMatch;
    }

    @Override
    public boolean matchesMoodTargeting(
            Advertisement advertisement,
            Mood mood,
            Integer intensity,
            String timeOfDay,
            String dayOfWeek,
            String season
    ) {
        // If no mood targets are defined, the ad doesn't match specific mood targeting
        if (advertisement.getMoodTargets() == null || advertisement.getMoodTargets().isEmpty()) {
            return false;
        }

        // Check for any matching include targets
        boolean hasIncludeMatch = advertisement.getMoodTargets().stream()
                .filter(target -> target.isInclude())
                .anyMatch(target -> target.matches(mood, intensity, timeOfDay, dayOfWeek, season));

        // Check for any matching exclude targets
        boolean hasExcludeMatch = advertisement.getMoodTargets().stream()
                .filter(target -> !target.isInclude())
                .anyMatch(target -> target.matches(mood, intensity, timeOfDay, dayOfWeek, season));

        // Ad matches if it has at least one include match and no exclude matches
        return hasIncludeMatch && !hasExcludeMatch;
    }
}