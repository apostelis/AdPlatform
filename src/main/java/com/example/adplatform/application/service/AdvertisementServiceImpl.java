package com.example.adplatform.application.service;

import com.example.adplatform.application.exception.AdvertisementNotFoundException;
import com.example.adplatform.application.exception.AdvertisementOperationException;
import com.example.adplatform.application.exception.AdvertisementValidationException;
import com.example.adplatform.application.port.in.AdvertisementService;
import com.example.adplatform.application.port.out.AdvertisementRepository;
import com.example.adplatform.config.CacheConfig;
import com.example.adplatform.domain.model.Advertisement;
import com.example.adplatform.domain.model.AdvertisementSource;
import com.example.adplatform.domain.model.Mood;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Cacheable(cacheNames = CacheConfig.CACHE_ALL_ADVERTISEMENTS)
    public List<Advertisement> getAllAdvertisements() {
        log.debug("Fetching all advertisements from database");
        return advertisementRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_ALL_ADVERTISEMENTS, key = "#pageable")
    public Page<Advertisement> getAllAdvertisements(Pageable pageable) {
        log.debug("Fetching all advertisements with pagination from database");
        return advertisementRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_ACTIVE_ADVERTISEMENTS)
    public List<Advertisement> getActiveAdvertisements() {
        log.debug("Fetching active advertisements from database");
        return advertisementRepository.findByActiveTrue();
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_ACTIVE_ADVERTISEMENTS, key = "#pageable")
    public Page<Advertisement> getActiveAdvertisements(Pageable pageable) {
        log.debug("Fetching active advertisements with pagination from database");
        return advertisementRepository.findByActiveTrue(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_ADVERTISEMENT_BY_ID, key = "#id")
    public Optional<Advertisement> getAdvertisementById(Long id) {
        log.debug("Fetching advertisement with id: {} from database", id);
        return advertisementRepository.findById(id);
    }
    
    /**
     * Gets an advertisement by ID, throwing an exception if not found.
     *
     * @param id the advertisement ID
     * @return the advertisement
     * @throws AdvertisementNotFoundException if the advertisement is not found
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_ADVERTISEMENT_BY_ID, key = "#id")
    public Advertisement getAdvertisementByIdOrThrow(Long id) {
        log.debug("Fetching advertisement with id: {} from database (or throw)", id);
        return advertisementRepository.findById(id)
                .orElseThrow(() -> new AdvertisementNotFoundException(id));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.CACHE_ALL_ADVERTISEMENTS, allEntries = true),
            @CacheEvict(cacheNames = CacheConfig.CACHE_ACTIVE_ADVERTISEMENTS, allEntries = true),
            @CacheEvict(cacheNames = CacheConfig.CACHE_ADVERTISEMENT_BY_ID, key = "#advertisement.id", condition = "#advertisement.id != null"),
            @CacheEvict(cacheNames = CacheConfig.CACHE_ADVERTISEMENTS_BY_SOURCE, allEntries = true),
            @CacheEvict(cacheNames = CacheConfig.CACHE_ADVERTISEMENTS_BY_TITLE, allEntries = true),
            @CacheEvict(cacheNames = CacheConfig.CACHE_TARGETED_ADVERTISEMENTS, allEntries = true),
            @CacheEvict(cacheNames = CacheConfig.CACHE_GEO_TARGETED_ADVERTISEMENTS, allEntries = true),
            @CacheEvict(cacheNames = CacheConfig.CACHE_BIO_TARGETED_ADVERTISEMENTS, allEntries = true),
            @CacheEvict(cacheNames = CacheConfig.CACHE_MOOD_TARGETED_ADVERTISEMENTS, allEntries = true)
    })
    public Advertisement saveAdvertisement(Advertisement advertisement) {
        try {
            log.debug("Saving advertisement and evicting caches");
            validateAdvertisement(advertisement);
            return advertisementRepository.save(advertisement);
        } catch (AdvertisementValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new AdvertisementOperationException(
                    advertisement.getId() == null 
                            ? AdvertisementOperationException.OperationType.CREATE 
                            : AdvertisementOperationException.OperationType.UPDATE,
                    "Failed to save advertisement",
                    e
            );
        }
    }
    
    /**
     * Validates an advertisement before saving.
     *
     * @param advertisement the advertisement to validate
     * @throws AdvertisementValidationException if validation fails
     */
    private void validateAdvertisement(Advertisement advertisement) {
        AdvertisementValidationException exception = new AdvertisementValidationException();
        
        if (advertisement.getTitle() == null || advertisement.getTitle().trim().isEmpty()) {
            exception.addError("title", "Title is required");
        }
        
        if (advertisement.getContent() == null || advertisement.getContent().trim().isEmpty()) {
            exception.addError("content", "Content is required");
        }
        
        if (advertisement.getSource() == null) {
            exception.addError("source", "Source is required");
        } else if (advertisement.getSourceIdentifier() == null || advertisement.getSourceIdentifier().trim().isEmpty()) {
            exception.addError("sourceIdentifier", "Source identifier is required");
        }
        
        if (!exception.getErrors().isEmpty()) {
            throw exception;
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.CACHE_ALL_ADVERTISEMENTS, allEntries = true),
            @CacheEvict(cacheNames = CacheConfig.CACHE_ACTIVE_ADVERTISEMENTS, allEntries = true),
            @CacheEvict(cacheNames = CacheConfig.CACHE_ADVERTISEMENT_BY_ID, key = "#id"),
            @CacheEvict(cacheNames = CacheConfig.CACHE_ADVERTISEMENTS_BY_SOURCE, allEntries = true),
            @CacheEvict(cacheNames = CacheConfig.CACHE_ADVERTISEMENTS_BY_TITLE, allEntries = true),
            @CacheEvict(cacheNames = CacheConfig.CACHE_TARGETED_ADVERTISEMENTS, allEntries = true),
            @CacheEvict(cacheNames = CacheConfig.CACHE_GEO_TARGETED_ADVERTISEMENTS, allEntries = true),
            @CacheEvict(cacheNames = CacheConfig.CACHE_BIO_TARGETED_ADVERTISEMENTS, allEntries = true),
            @CacheEvict(cacheNames = CacheConfig.CACHE_MOOD_TARGETED_ADVERTISEMENTS, allEntries = true)
    })
    public void deleteAdvertisement(Long id) {
        try {
            log.debug("Deleting advertisement with id: {} and evicting caches", id);
            // Check if advertisement exists before deleting
            if (advertisementRepository.findById(id).isEmpty()) {
                throw new AdvertisementNotFoundException(id);
            }
            advertisementRepository.deleteById(id);
        } catch (AdvertisementNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new AdvertisementOperationException(
                    AdvertisementOperationException.OperationType.DELETE,
                    "Failed to delete advertisement with id: " + id,
                    e
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_ADVERTISEMENTS_BY_SOURCE, key = "#source")
    public List<Advertisement> getAdvertisementsBySource(AdvertisementSource source) {
        log.debug("Fetching advertisements by source: {} from database", source);
        return advertisementRepository.findBySource(source);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_ADVERTISEMENTS_BY_SOURCE, key = "#source.toString() + '-' + #pageable")
    public Page<Advertisement> getAdvertisementsBySource(AdvertisementSource source, Pageable pageable) {
        log.debug("Fetching advertisements by source: {} with pagination from database", source);
        return advertisementRepository.findBySource(source, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_ADVERTISEMENTS_BY_TITLE, key = "#title")
    public List<Advertisement> getAdvertisementsByTitle(String title) {
        log.debug("Fetching advertisements by title: {} from database", title);
        return advertisementRepository.findByTitleContainingIgnoreCase(title);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_ADVERTISEMENTS_BY_TITLE, key = "#title + '-' + #pageable")
    public Page<Advertisement> getAdvertisementsByTitle(String title, Pageable pageable) {
        log.debug("Fetching advertisements by title: {} with pagination from database", title);
        return advertisementRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_TARGETED_ADVERTISEMENTS, 
               key = "{#countryCode, #userBioData.toString(), #mood != null ? #mood.toString() : 'null'}")
    public List<Advertisement> getTargetedAdvertisements(
            String countryCode,
            Map<String, Object> userBioData,
            Mood mood
    ) {
        log.debug("Fetching targeted advertisements for country: {}, mood: {} from database", countryCode, mood);
        try {
            // Get all active advertisements
            List<Advertisement> activeAds = getActiveAdvertisements();
    
            // Start with all active ads
            List<Advertisement> result = new ArrayList<>(activeAds);
    
            // Apply bio targeting if user data is available
            if (userBioData != null && !userBioData.isEmpty()) {
                Integer age = (Integer) userBioData.getOrDefault("age", null);
                String gender = (String) userBioData.getOrDefault("gender", null);
                String occupation = (String) userBioData.getOrDefault("occupation", null);
                String educationLevel = (String) userBioData.getOrDefault("educationLevel", null);
                String language = (String) userBioData.getOrDefault("language", null);
                @SuppressWarnings("unchecked")
                Set<String> interests = new HashSet<>((List<String>) userBioData.getOrDefault("interests", Collections.emptyList()));
    
                // Only return ads that have bio targeting rules that match the criteria
                return result.stream()
                        .filter(ad -> {
                            // Ad must have bio targets to be considered
                            if (ad.getBioTargets() == null || ad.getBioTargets().isEmpty()) {
                                return false;
                            }
                            // Check if the bio targets match
                            return ad.matchesBioTargeting(age, gender, occupation, educationLevel, language, interests);
                        })
                        .collect(Collectors.toList());
            }
    
            // If no bio data is provided, return an empty list
            return Collections.emptyList();
        } catch (Exception e) {
            throw new AdvertisementOperationException(
                    AdvertisementOperationException.OperationType.TARGETING,
                    "Failed to get targeted advertisements",
                    e
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_GEO_TARGETED_ADVERTISEMENTS, 
               key = "{#countryCode, #region, #city, #latitude, #longitude}")
    public List<Advertisement> getGeoTargetedAdvertisements(
            String countryCode,
            String region,
            String city,
            Double latitude,
            Double longitude
    ) {
        log.debug("Fetching geo-targeted advertisements for country: {}, region: {}, city: {} from database", 
                 countryCode, region, city);
        try {
            List<Advertisement> activeAds = getActiveAdvertisements();
    
            // Filter ads that have geo targeting and match the criteria
            return activeAds.stream()
                    .filter(ad -> {
                        // If the ad has no geo targets, it doesn't match specific geo targeting
                        if (ad.getGeoTargets() == null || ad.getGeoTargets().isEmpty()) {
                            return false;
                        }
                        return ad.matchesGeoTargeting(countryCode, region, city, latitude, longitude);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new AdvertisementOperationException(
                    AdvertisementOperationException.OperationType.TARGETING,
                    "Failed to get geo-targeted advertisements",
                    e
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_BIO_TARGETED_ADVERTISEMENTS, 
               key = "{#age, #gender, #occupation, #educationLevel, #language, #interests != null ? #interests.toString() : 'null'}")
    public List<Advertisement> getBioTargetedAdvertisements(
            Integer age,
            String gender,
            String occupation,
            String educationLevel,
            String language,
            Set<String> interests
    ) {
        log.debug("Fetching bio-targeted advertisements for age: {}, gender: {}, occupation: {} from database", 
                 age, gender, occupation);
        try {
            List<Advertisement> activeAds = getActiveAdvertisements();
    
            // Filter ads that have bio targeting and match the criteria
            return activeAds.stream()
                    .filter(ad -> {
                        // If the ad has no bio targets, it doesn't match specific bio targeting
                        if (ad.getBioTargets() == null || ad.getBioTargets().isEmpty()) {
                            return false;
                        }
                        return ad.matchesBioTargeting(age, gender, occupation, educationLevel, language, interests);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new AdvertisementOperationException(
                    AdvertisementOperationException.OperationType.TARGETING,
                    "Failed to get bio-targeted advertisements",
                    e
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_MOOD_TARGETED_ADVERTISEMENTS, 
               key = "{#mood != null ? #mood.toString() : 'null', #intensity, #timeOfDay, #dayOfWeek, #season}")
    public List<Advertisement> getMoodTargetedAdvertisements(
            Mood mood,
            Integer intensity,
            String timeOfDay,
            String dayOfWeek,
            String season
    ) {
        log.debug("Fetching mood-targeted advertisements for mood: {}, intensity: {}, timeOfDay: {} from database", 
                 mood, intensity, timeOfDay);
        try {
            List<Advertisement> activeAds = getActiveAdvertisements();
    
            // Filter ads that have mood targeting and match the criteria
            return activeAds.stream()
                    .filter(ad -> {
                        // If the ad has no mood targets, it doesn't match specific mood targeting
                        if (ad.getMoodTargets() == null || ad.getMoodTargets().isEmpty()) {
                            return false;
                        }
                        return ad.matchesMoodTargeting(mood, intensity, timeOfDay, dayOfWeek, season);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new AdvertisementOperationException(
                    AdvertisementOperationException.OperationType.TARGETING,
                    "Failed to get mood-targeted advertisements",
                    e
            );
        }
    }
}
