package com.example.adplatform.service.impl;

import com.example.adplatform.domain.model.Advertisement;
import com.example.adplatform.domain.model.AdvertisementSource;
import com.example.adplatform.domain.model.Mood;
import com.example.adplatform.service.AdvertisementService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This service implementation has been replaced by com.example.adplatform.application.service.AdvertisementServiceImpl
 * as part of the migration to a hexagonal architecture.
 * 
 * @deprecated Use the new service implementation in the application layer instead.
 */
@Deprecated
public class AdvertisementServiceImpl implements AdvertisementService {

    @Override
    public List<Advertisement> getAllAdvertisements() {
        return Collections.emptyList();
    }

    @Override
    public List<Advertisement> getActiveAdvertisements() {
        return Collections.emptyList();
    }

    @Override
    public Optional<Advertisement> getAdvertisementById(Long id) {
        return Optional.empty();
    }

    @Override
    public Advertisement saveAdvertisement(Advertisement advertisement) {
        throw new UnsupportedOperationException("This service implementation is deprecated. Use com.example.adplatform.application.service.AdvertisementServiceImpl instead.");
    }

    @Override
    public void deleteAdvertisement(Long id) {
        throw new UnsupportedOperationException("This service implementation is deprecated. Use com.example.adplatform.application.service.AdvertisementServiceImpl instead.");
    }

    @Override
    public List<Advertisement> getAdvertisementsBySource(AdvertisementSource source) {
        return Collections.emptyList();
    }

    @Override
    public List<Advertisement> getAdvertisementsByTitle(String title) {
        return Collections.emptyList();
    }

    @Override
    public List<Advertisement> getTargetedAdvertisements(
            String countryCode,
            Map<String, Object> userBioData,
            Mood mood
    ) {
        return Collections.emptyList();
    }

    @Override
    public List<Advertisement> getGeoTargetedAdvertisements(
            String countryCode,
            String region,
            String city,
            Double latitude,
            Double longitude
    ) {
        return Collections.emptyList();
    }

    @Override
    public List<Advertisement> getBioTargetedAdvertisements(
            Integer age,
            String gender,
            String occupation,
            String educationLevel,
            String language,
            List<String> interests
    ) {
        return Collections.emptyList();
    }

    @Override
    public List<Advertisement> getMoodTargetedAdvertisements(
            Mood mood,
            Integer intensity,
            String timeOfDay,
            String dayOfWeek,
            String season
    ) {
        return Collections.emptyList();
    }
}