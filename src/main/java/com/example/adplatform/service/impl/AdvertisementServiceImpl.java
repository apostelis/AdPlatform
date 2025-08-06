package com.example.adplatform.service.impl;

import com.example.adplatform.model.*;
import com.example.adplatform.model.BioTarget.Gender;
import com.example.adplatform.model.MoodTarget.Mood;
import com.example.adplatform.repository.AdvertisementRepository;
import com.example.adplatform.service.AdvertisementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the AdvertisementService interface.
 * Provides business logic for advertisement operations and targeting.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AdvertisementServiceImpl implements AdvertisementService {

    private final AdvertisementRepository advertisementRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Advertisement> getAllAdvertisements() {
        return advertisementRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Advertisement> getActiveAdvertisements() {
        return advertisementRepository.findByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Advertisement> getAdvertisementById(Long id) {
        return advertisementRepository.findById(id);
    }

    @Override
    public Advertisement saveAdvertisement(Advertisement advertisement) {
        return advertisementRepository.save(advertisement);
    }

    @Override
    public void deleteAdvertisement(Long id) {
        advertisementRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Advertisement> getAdvertisementsBySource(AdvertisementSource source) {
        return advertisementRepository.findBySource(source);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Advertisement> getAdvertisementsByTitle(String title) {
        return advertisementRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Advertisement> getTargetedAdvertisements(
            String countryCode,
            Map<String, Object> userBioData,
            Mood mood
    ) {
        // Get all active advertisements
        List<Advertisement> activeAds = getActiveAdvertisements();

        // Apply geo targeting
        List<Advertisement> geoTargetedAds = activeAds.stream()
                .filter(ad -> matchesGeoTarget(ad, countryCode, null, null, null, null))
                .collect(Collectors.toList());

        // Apply bio targeting if user data is available
        List<Advertisement> bioTargetedAds = geoTargetedAds;
        if (userBioData != null && !userBioData.isEmpty()) {
            Integer age = (Integer) userBioData.getOrDefault("age", null);
            String genderStr = (String) userBioData.getOrDefault("gender", null);
            String occupation = (String) userBioData.getOrDefault("occupation", null);
            String educationLevel = (String) userBioData.getOrDefault("educationLevel", null);
            String language = (String) userBioData.getOrDefault("language", null);
            @SuppressWarnings("unchecked")
            List<String> interests = (List<String>) userBioData.getOrDefault("interests", Collections.emptyList());

            bioTargetedAds = geoTargetedAds.stream()
                    .filter(ad -> matchesBioTarget(ad, age, genderStr, occupation, educationLevel, language, interests))
                    .collect(Collectors.toList());
        }

        // Apply mood targeting if mood is provided
        List<Advertisement> moodTargetedAds = bioTargetedAds;
        if (mood != null) {
            moodTargetedAds = bioTargetedAds.stream()
                    .filter(ad -> matchesMoodTarget(ad, mood, null, null, null, null))
                    .collect(Collectors.toList());
        }

        return moodTargetedAds;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Advertisement> getGeoTargetedAdvertisements(
            String countryCode,
            String region,
            String city,
            Double latitude,
            Double longitude
    ) {
        List<Advertisement> activeAds = getActiveAdvertisements();

        return activeAds.stream()
                .filter(ad -> matchesGeoTarget(ad, countryCode, region, city, latitude, longitude))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Advertisement> getBioTargetedAdvertisements(
            Integer age,
            String genderStr,
            String occupation,
            String educationLevel,
            String language,
            List<String> interests
    ) {
        List<Advertisement> activeAds = getActiveAdvertisements();

        return activeAds.stream()
                .filter(ad -> matchesBioTarget(ad, age, genderStr, occupation, educationLevel, language, interests))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Advertisement> getMoodTargetedAdvertisements(
            Mood mood,
            Integer intensity,
            String timeOfDay,
            String dayOfWeek,
            String season
    ) {
        List<Advertisement> activeAds = getActiveAdvertisements();

        return activeAds.stream()
                .filter(ad -> matchesMoodTarget(ad, mood, intensity, timeOfDay, dayOfWeek, season))
                .collect(Collectors.toList());
    }

    /**
     * Check if an advertisement matches the given geo targeting criteria.
     */
    private boolean matchesGeoTarget(
            Advertisement ad,
            String countryCode,
            String region,
            String city,
            Double latitude,
            Double longitude
    ) {
        // If no geo targets are defined, the ad matches everyone
        if (ad.getGeoTargets() == null || ad.getGeoTargets().isEmpty()) {
            return true;
        }

        // Check for any matching include targets
        boolean hasIncludeMatch = ad.getGeoTargets().stream()
                .filter(GeoTarget::isInclude)
                .anyMatch(target -> {
                    // Country code match
                    if (countryCode != null && target.getCountryCode() != null) {
                        if (!target.getCountryCode().equalsIgnoreCase(countryCode)) {
                            return false;
                        }
                    }

                    // Region match (if specified)
                    if (region != null && target.getRegion() != null) {
                        if (!target.getRegion().equalsIgnoreCase(region)) {
                            return false;
                        }
                    }

                    // City match (if specified)
                    if (city != null && target.getCity() != null) {
                        if (!target.getCity().equalsIgnoreCase(city)) {
                            return false;
                        }
                    }

                    // Proximity match (if coordinates and radius are specified)
                    if (latitude != null && longitude != null && 
                        target.getLatitude() != null && target.getLongitude() != null && 
                        target.getRadiusKm() != null) {
                        double distance = calculateDistance(
                                latitude, longitude, 
                                target.getLatitude(), target.getLongitude());
                        if (distance > target.getRadiusKm()) {
                            return false;
                        }
                    }

                    return true;
                });

        // Check for any matching exclude targets
        boolean hasExcludeMatch = ad.getGeoTargets().stream()
                .filter(target -> !target.isInclude())
                .anyMatch(target -> {
                    // Country code match
                    if (countryCode != null && target.getCountryCode() != null) {
                        if (target.getCountryCode().equalsIgnoreCase(countryCode)) {
                            return true;
                        }
                    }

                    // Region match
                    if (region != null && target.getRegion() != null) {
                        if (target.getRegion().equalsIgnoreCase(region)) {
                            return true;
                        }
                    }

                    // City match
                    if (city != null && target.getCity() != null) {
                        if (target.getCity().equalsIgnoreCase(city)) {
                            return true;
                        }
                    }

                    // Proximity match
                    if (latitude != null && longitude != null && 
                        target.getLatitude() != null && target.getLongitude() != null && 
                        target.getRadiusKm() != null) {
                        double distance = calculateDistance(
                                latitude, longitude, 
                                target.getLatitude(), target.getLongitude());
                        if (distance <= target.getRadiusKm()) {
                            return true;
                        }
                    }

                    return false;
                });

        // Ad matches if it has at least one include match and no exclude matches
        return hasIncludeMatch && !hasExcludeMatch;
    }

    /**
     * Check if an advertisement matches the given biographical targeting criteria.
     */
    private boolean matchesBioTarget(
            Advertisement ad,
            Integer age,
            String genderStr,
            String occupation,
            String educationLevel,
            String language,
            List<String> interests
    ) {
        // If no bio targets are defined, the ad matches everyone
        if (ad.getBioTargets() == null || ad.getBioTargets().isEmpty()) {
            return true;
        }

        final Gender gender;
        if (genderStr != null) {
            Gender tempGender = null;
            try {
                tempGender = Gender.valueOf(genderStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid gender string, leave as null
            }
            gender = tempGender;
        } else {
            gender = null;
        }

        // Check for any matching include targets
        boolean hasIncludeMatch = ad.getBioTargets().stream()
                .filter(BioTarget::isInclude)
                .anyMatch(target -> {
                    // Age range match
                    if (age != null && (target.getMinAge() != null || target.getMaxAge() != null)) {
                        if (target.getMinAge() != null && age < target.getMinAge()) {
                            return false;
                        }
                        if (target.getMaxAge() != null && age > target.getMaxAge()) {
                            return false;
                        }
                    }

                    // Gender match
                    if (gender != null && target.getGender() != null) {
                        if (target.getGender() != Gender.ALL && target.getGender() != gender) {
                            return false;
                        }
                    }

                    // Occupation match
                    if (occupation != null && target.getOccupation() != null) {
                        if (!target.getOccupation().equalsIgnoreCase(occupation)) {
                            return false;
                        }
                    }

                    // Education level match
                    if (educationLevel != null && target.getEducationLevel() != null) {
                        if (!target.getEducationLevel().equalsIgnoreCase(educationLevel)) {
                            return false;
                        }
                    }

                    // Language match
                    if (language != null && target.getLanguage() != null) {
                        if (!target.getLanguage().equalsIgnoreCase(language)) {
                            return false;
                        }
                    }

                    // Interest category match
                    if (interests != null && !interests.isEmpty() && target.getInterestCategory() != null) {
                        if (interests.stream().noneMatch(interest -> 
                                interest.equalsIgnoreCase(target.getInterestCategory()))) {
                            return false;
                        }
                    }

                    return true;
                });

        // Check for any matching exclude targets
        boolean hasExcludeMatch = ad.getBioTargets().stream()
                .filter(target -> !target.isInclude())
                .anyMatch(target -> {
                    // Age range match
                    if (age != null && (target.getMinAge() != null || target.getMaxAge() != null)) {
                        boolean inRange = true;
                        if (target.getMinAge() != null && age < target.getMinAge()) {
                            inRange = false;
                        }
                        if (target.getMaxAge() != null && age > target.getMaxAge()) {
                            inRange = false;
                        }
                        if (inRange) {
                            return true;
                        }
                    }

                    // Gender match
                    if (gender != null && target.getGender() != null) {
                        if (target.getGender() == Gender.ALL || target.getGender() == gender) {
                            return true;
                        }
                    }

                    // Occupation match
                    if (occupation != null && target.getOccupation() != null) {
                        if (target.getOccupation().equalsIgnoreCase(occupation)) {
                            return true;
                        }
                    }

                    // Education level match
                    if (educationLevel != null && target.getEducationLevel() != null) {
                        if (target.getEducationLevel().equalsIgnoreCase(educationLevel)) {
                            return true;
                        }
                    }

                    // Language match
                    if (language != null && target.getLanguage() != null) {
                        if (target.getLanguage().equalsIgnoreCase(language)) {
                            return true;
                        }
                    }

                    // Interest category match
                    if (interests != null && !interests.isEmpty() && target.getInterestCategory() != null) {
                        if (interests.stream().anyMatch(interest -> 
                                interest.equalsIgnoreCase(target.getInterestCategory()))) {
                            return true;
                        }
                    }

                    return false;
                });

        // Ad matches if it has at least one include match and no exclude matches
        return hasIncludeMatch && !hasExcludeMatch;
    }

    /**
     * Check if an advertisement matches the given mood targeting criteria.
     */
    private boolean matchesMoodTarget(
            Advertisement ad,
            Mood mood,
            Integer intensity,
            String timeOfDay,
            String dayOfWeek,
            String season
    ) {
        // If no mood targets are defined, the ad matches everyone
        if (ad.getMoodTargets() == null || ad.getMoodTargets().isEmpty()) {
            return true;
        }

        // Check for any matching include targets
        boolean hasIncludeMatch = ad.getMoodTargets().stream()
                .filter(MoodTarget::isInclude)
                .anyMatch(target -> {
                    // Mood match
                    if (mood != null && target.getMood() != null) {
                        if (target.getMood() != mood) {
                            return false;
                        }
                    }

                    // Intensity range match
                    if (intensity != null && (target.getIntensityMin() != null || target.getIntensityMax() != null)) {
                        if (target.getIntensityMin() != null && intensity < target.getIntensityMin()) {
                            return false;
                        }
                        if (target.getIntensityMax() != null && intensity > target.getIntensityMax()) {
                            return false;
                        }
                    }

                    // Time of day match
                    if (timeOfDay != null && target.getTimeOfDay() != null) {
                        if (!target.getTimeOfDay().equalsIgnoreCase(timeOfDay)) {
                            return false;
                        }
                    }

                    // Day of week match
                    if (dayOfWeek != null && target.getDayOfWeek() != null) {
                        if (!target.getDayOfWeek().equalsIgnoreCase(dayOfWeek)) {
                            return false;
                        }
                    }

                    // Season match
                    if (season != null && target.getSeason() != null) {
                        if (!target.getSeason().equalsIgnoreCase(season)) {
                            return false;
                        }
                    }

                    return true;
                });

        // Check for any matching exclude targets
        boolean hasExcludeMatch = ad.getMoodTargets().stream()
                .filter(target -> !target.isInclude())
                .anyMatch(target -> {
                    // Mood match
                    if (mood != null && target.getMood() != null) {
                        if (target.getMood() == mood) {
                            return true;
                        }
                    }

                    // Intensity range match
                    if (intensity != null && (target.getIntensityMin() != null || target.getIntensityMax() != null)) {
                        boolean inRange = true;
                        if (target.getIntensityMin() != null && intensity < target.getIntensityMin()) {
                            inRange = false;
                        }
                        if (target.getIntensityMax() != null && intensity > target.getIntensityMax()) {
                            inRange = false;
                        }
                        if (inRange) {
                            return true;
                        }
                    }

                    // Time of day match
                    if (timeOfDay != null && target.getTimeOfDay() != null) {
                        if (target.getTimeOfDay().equalsIgnoreCase(timeOfDay)) {
                            return true;
                        }
                    }

                    // Day of week match
                    if (dayOfWeek != null && target.getDayOfWeek() != null) {
                        if (target.getDayOfWeek().equalsIgnoreCase(dayOfWeek)) {
                            return true;
                        }
                    }

                    // Season match
                    if (season != null && target.getSeason() != null) {
                        if (target.getSeason().equalsIgnoreCase(season)) {
                            return true;
                        }
                    }

                    return false;
                });

        // Ad matches if it has at least one include match and no exclude matches
        return hasIncludeMatch && !hasExcludeMatch;
    }

    /**
     * Calculate the distance between two points in kilometers using the Haversine formula.
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
