package com.example.adplatform.application.service;

import com.example.adplatform.application.port.in.AdvertisementService;
import com.example.adplatform.application.port.out.AdvertisementRepository;
import com.example.adplatform.domain.model.Advertisement;
import com.example.adplatform.domain.model.AdvertisementSource;
import com.example.adplatform.domain.model.Mood;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the AdvertisementService port.
 * This is a service in the hexagonal architecture that implements
 * the business logic for advertisement operations.
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
                .filter(ad -> ad.matchesGeoTargeting(countryCode, null, null, null, null))
                .collect(Collectors.toList());

        // Apply bio targeting if user data is available
        List<Advertisement> bioTargetedAds = geoTargetedAds;
        if (userBioData != null && !userBioData.isEmpty()) {
            Integer age = (Integer) userBioData.getOrDefault("age", null);
            String gender = (String) userBioData.getOrDefault("gender", null);
            String occupation = (String) userBioData.getOrDefault("occupation", null);
            String educationLevel = (String) userBioData.getOrDefault("educationLevel", null);
            String language = (String) userBioData.getOrDefault("language", null);
            @SuppressWarnings("unchecked")
            Set<String> interests = new HashSet<>((List<String>) userBioData.getOrDefault("interests", Collections.emptyList()));

            bioTargetedAds = geoTargetedAds.stream()
                    .filter(ad -> ad.matchesBioTargeting(age, gender, occupation, educationLevel, language, interests))
                    .collect(Collectors.toList());
        }

        // Apply mood targeting if mood is provided
        List<Advertisement> moodTargetedAds = bioTargetedAds;
        if (mood != null) {
            moodTargetedAds = bioTargetedAds.stream()
                    .filter(ad -> ad.matchesMoodTargeting(mood, null, null, null, null))
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
                .filter(ad -> ad.matchesGeoTargeting(countryCode, region, city, latitude, longitude))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Advertisement> getBioTargetedAdvertisements(
            Integer age,
            String gender,
            String occupation,
            String educationLevel,
            String language,
            Set<String> interests
    ) {
        List<Advertisement> activeAds = getActiveAdvertisements();

        return activeAds.stream()
                .filter(ad -> ad.matchesBioTargeting(age, gender, occupation, educationLevel, language, interests))
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
                .filter(ad -> ad.matchesMoodTargeting(mood, intensity, timeOfDay, dayOfWeek, season))
                .collect(Collectors.toList());
    }
}