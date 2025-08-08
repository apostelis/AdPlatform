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

    private final java.util.List<com.example.adplatform.application.service.targeting.TargetingStrategy> strategyList;

    private java.util.Map<String, com.example.adplatform.application.service.targeting.TargetingStrategy> strategies() {
        java.util.Map<String, com.example.adplatform.application.service.targeting.TargetingStrategy> map = new java.util.HashMap<>();
        for (var s : strategyList) {
            map.put(s.key(), s);
        }
        return map;
    }

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

            java.util.Map<String, Object> bioCriteria = new java.util.HashMap<>();
            if (userBioData != null) {
                bioCriteria.putAll(userBioData);
            }
            java.util.Map<String, Object> geoCriteria = java.util.Map.of("countryCode", countryCode);
            java.util.Map<String, Object> moodCriteria = new java.util.HashMap<>();
            moodCriteria.put("mood", mood);

            List<Advertisement> result = advertisements;

            // Apply strategies conditionally when inputs are present
            var bioStrategy = strategies().get(com.example.adplatform.application.service.targeting.BioTargetingStrategy.KEY);
            if (bioStrategy != null && !bioCriteria.isEmpty()) {
                result = bioStrategy.filter(result, bioCriteria);
            }
            var geoStrategy = strategies().get(com.example.adplatform.application.service.targeting.GeoTargetingStrategy.KEY);
            if (geoStrategy != null && countryCode != null) {
                result = geoStrategy.filter(result, geoCriteria);
            }
            var moodStrategy = strategies().get(com.example.adplatform.application.service.targeting.MoodTargetingStrategy.KEY);
            if (moodStrategy != null && mood != null) {
                result = moodStrategy.filter(result, moodCriteria);
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
            var geoStrategy = strategies().get(com.example.adplatform.application.service.targeting.GeoTargetingStrategy.KEY);
            if (geoStrategy == null) {
                return Collections.emptyList();
            }
            java.util.Map<String, Object> criteria = new java.util.HashMap<>();
            criteria.put("countryCode", countryCode);
            criteria.put("region", region);
            criteria.put("city", city);
            criteria.put("latitude", latitude);
            criteria.put("longitude", longitude);
            return geoStrategy.filter(advertisements, criteria);
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
            var bioStrategy = strategies().get(com.example.adplatform.application.service.targeting.BioTargetingStrategy.KEY);
            if (bioStrategy == null) {
                return Collections.emptyList();
            }
            java.util.Map<String, Object> criteria = new java.util.HashMap<>();
            criteria.put("age", age);
            criteria.put("gender", gender);
            criteria.put("occupation", occupation);
            criteria.put("educationLevel", educationLevel);
            criteria.put("language", language);
            criteria.put("interests", interests);
            return bioStrategy.filter(advertisements, criteria);
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
            var moodStrategy = strategies().get(com.example.adplatform.application.service.targeting.MoodTargetingStrategy.KEY);
            if (moodStrategy == null) {
                return Collections.emptyList();
            }
            java.util.Map<String, Object> criteria = new java.util.HashMap<>();
            criteria.put("mood", mood);
            criteria.put("intensity", intensity);
            criteria.put("timeOfDay", timeOfDay);
            criteria.put("dayOfWeek", dayOfWeek);
            criteria.put("season", season);
            return moodStrategy.filter(advertisements, criteria);
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
            var geo = strategies().get(com.example.adplatform.application.service.targeting.GeoTargetingStrategy.KEY);
            var bio = strategies().get(com.example.adplatform.application.service.targeting.BioTargetingStrategy.KEY);
            var moodStrat = strategies().get(com.example.adplatform.application.service.targeting.MoodTargetingStrategy.KEY);
            boolean geoOk = geo == null || geo.matches(advertisement, java.util.Map.of("countryCode", countryCode));
            java.util.Map<String, Object> bioCriteria = new java.util.HashMap<>();
            bioCriteria.put("age", age);
            bioCriteria.put("gender", gender);
            bioCriteria.put("occupation", occupation);
            bioCriteria.put("educationLevel", educationLevel);
            bioCriteria.put("language", language);
            bioCriteria.put("interests", interests);
            boolean bioOk = bio == null || bio.matches(advertisement, bioCriteria);
            java.util.Map<String, Object> moodCriteria = new java.util.HashMap<>();
            moodCriteria.put("mood", mood);
            moodCriteria.put("intensity", intensity);
            moodCriteria.put("timeOfDay", timeOfDay);
            moodCriteria.put("dayOfWeek", dayOfWeek);
            moodCriteria.put("season", season);
            boolean moodOk = moodStrat == null || moodStrat.matches(advertisement, moodCriteria);
            return geoOk && bioOk && moodOk;
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
            var geo = strategies().get(com.example.adplatform.application.service.targeting.GeoTargetingStrategy.KEY);
            if (geo == null) return false;
            java.util.Map<String, Object> criteria = new java.util.HashMap<>();
            criteria.put("countryCode", countryCode);
            criteria.put("region", region);
            criteria.put("city", city);
            criteria.put("latitude", latitude);
            criteria.put("longitude", longitude);
            return geo.matches(advertisement, criteria);
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
            var bio = strategies().get(com.example.adplatform.application.service.targeting.BioTargetingStrategy.KEY);
            if (bio == null) return false;
            java.util.Map<String, Object> criteria = new java.util.HashMap<>();
            criteria.put("age", age);
            criteria.put("gender", genderStr);
            criteria.put("occupation", occupation);
            criteria.put("educationLevel", educationLevel);
            criteria.put("language", language);
            criteria.put("interests", interests);
            return bio.matches(advertisement, criteria);
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
            var moodStrat = strategies().get(com.example.adplatform.application.service.targeting.MoodTargetingStrategy.KEY);
            if (moodStrat == null) return false;
            java.util.Map<String, Object> criteria = new java.util.HashMap<>();
            criteria.put("mood", mood);
            criteria.put("intensity", intensity);
            criteria.put("timeOfDay", timeOfDay);
            criteria.put("dayOfWeek", dayOfWeek);
            criteria.put("season", season);
            return moodStrat.matches(advertisement, criteria);
        }
}