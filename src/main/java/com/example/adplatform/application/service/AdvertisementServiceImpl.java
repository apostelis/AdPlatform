package com.example.adplatform.application.service;

import com.example.adplatform.application.exception.AdvertisementNotFoundException;
import com.example.adplatform.application.exception.AdvertisementOperationException;
import com.example.adplatform.application.exception.AdvertisementValidationException;
import com.example.adplatform.application.port.in.AdvertisementService;
import com.example.adplatform.application.port.in.TargetingService;
import com.example.adplatform.application.port.in.ViewingPolicyService;
import com.example.adplatform.application.port.out.AdvertisementRepository;
import com.example.adplatform.config.CacheConfig;
import com.example.adplatform.domain.model.Advertisement;
import com.example.adplatform.domain.model.AdvertisementSource;
import com.example.adplatform.domain.model.Mood;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of the AdvertisementService port.
 * This is a service in the hexagonal architecture that implements
 * the business logic for advertisement operations.
 */
@Service
@Slf4j
@Transactional
public class AdvertisementServiceImpl implements AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final TargetingService targetingService;
    private final ViewingPolicyService viewingPolicyService;
    private final com.example.adplatform.application.port.out.AdvertisementEventPublisher eventPublisher;

    /**
     * Primary constructor for AdvertisementServiceImpl with event publishing support.
     * 
     * @param advertisementRepository the repository for advertisement persistence operations
     * @param targetingService the service for handling advertisement targeting logic
     * @param viewingPolicyService the service for managing viewing policies and fairness
     * @param eventPublisher the publisher for advertisement-related domain events
     */
    @org.springframework.beans.factory.annotation.Autowired
    public AdvertisementServiceImpl(AdvertisementRepository advertisementRepository,
                                    TargetingService targetingService,
                                    ViewingPolicyService viewingPolicyService,
                                    com.example.adplatform.application.port.out.AdvertisementEventPublisher eventPublisher) {
        this.advertisementRepository = advertisementRepository;
        this.targetingService = targetingService;
        this.viewingPolicyService = viewingPolicyService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Backward-compatible constructor for tests and legacy wiring.
     * Creates default instances of ViewingPolicyService and NoOpAdvertisementEventPublisher.
     * 
     * @param advertisementRepository the repository for advertisement persistence operations
     * @param targetingService the service for handling advertisement targeting logic
     */
    public AdvertisementServiceImpl(AdvertisementRepository advertisementRepository,
                                    TargetingService targetingService) {
        this.advertisementRepository = advertisementRepository;
        this.targetingService = targetingService;
        this.viewingPolicyService = new ViewingPolicyServiceImpl();
        this.eventPublisher = new NoOpAdvertisementEventPublisher();
    }

    // No-op publisher used when event infrastructure is not wired (e.g., in unit tests)
    private static final class NoOpAdvertisementEventPublisher implements com.example.adplatform.application.port.out.AdvertisementEventPublisher {
        @Override
        public void publish(com.example.adplatform.domain.event.AdvertisementViewedEvent event) {
            // no-op
        }
        @Override
        public void publish(com.example.adplatform.domain.event.AdvertisementInteractedEvent event) {
            // no-op
        }
    }

    /**
     * Retrieves all advertisements from the repository.
     * Results are cached to improve performance for repeated queries.
     * 
     * @return a list of all advertisements in the system
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_ALL_ADVERTISEMENTS)
    public List<Advertisement> getAllAdvertisements() {
        log.debug("Fetching all advertisements from database");
        return advertisementRepository.findAll();
    }
    
    /**
     * Retrieves all advertisements with pagination support.
     * Results are cached to improve performance for repeated queries.
     * 
     * @param pageable the pagination information (page number, size, sorting)
     * @return a page of advertisements
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_ALL_ADVERTISEMENTS, key = "#pageable")
    public Page<Advertisement> getAllAdvertisements(Pageable pageable) {
        log.debug("Fetching all advertisements with pagination from database");
        return advertisementRepository.findAll(pageable);
    }

    /**
     * Retrieves only active advertisements from the repository.
     * Results are cached to improve performance for repeated queries.
     * 
     * @return a list of active advertisements
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_ACTIVE_ADVERTISEMENTS)
    public List<Advertisement> getActiveAdvertisements() {
        log.debug("Fetching active advertisements from database");
        return advertisementRepository.findByActiveTrue();
    }
    
    /**
     * Retrieves only active advertisements with pagination support.
     * Results are cached to improve performance for repeated queries.
     * 
     * @param pageable the pagination information (page number, size, sorting)
     * @return a page of active advertisements
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_ACTIVE_ADVERTISEMENTS, key = "#pageable")
    public Page<Advertisement> getActiveAdvertisements(Pageable pageable) {
        log.debug("Fetching active advertisements with pagination from database");
        return advertisementRepository.findByActiveTrue(pageable);
    }

    /**
     * Retrieves an advertisement by its unique identifier.
     * Results are cached to improve performance for repeated queries.
     * 
     * @param id the unique identifier of the advertisement
     * @return an Optional containing the advertisement if found, empty otherwise
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_ADVERTISEMENT_BY_ID, key = "#id")
    public Optional<Advertisement> getAdvertisementById(Long id) {
        if (id == null) {
            log.warn("Attempted to fetch advertisement with null id");
            return Optional.empty();
        }
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
        if (id == null) {
            throw new IllegalArgumentException("Advertisement ID cannot be null");
        }
        log.debug("Fetching advertisement with id: {} from database (or throw)", id);
        return advertisementRepository.findById(id)
                .orElseThrow(() -> new AdvertisementNotFoundException(id));
    }

    /**
     * Saves an advertisement to the repository after validation.
     * Performs comprehensive validation and evicts all relevant caches upon successful save.
     * 
     * @param advertisement the advertisement to save (create or update)
     * @return the saved advertisement with generated/updated fields
     * @throws AdvertisementValidationException if the advertisement fails validation
     * @throws AdvertisementOperationException if the save operation fails
     */
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
        if (advertisement == null) {
            throw new IllegalArgumentException("Advertisement cannot be null");
        }
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
        if (advertisement == null) {
            throw new IllegalArgumentException("Advertisement cannot be null for validation");
        }
        
        AdvertisementValidationException exception = new AdvertisementValidationException();

        validateBasicFields(advertisement, exception);
        validateYoutubeConsistency(advertisement, exception);
        validateClickability(advertisement, exception);

        if (!exception.getErrors().isEmpty()) {
            throw exception;
        }
    }

    private static void validateBasicFields(Advertisement advertisement, AdvertisementValidationException exception) {
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
    }

    private static void validateYoutubeConsistency(Advertisement advertisement, AdvertisementValidationException exception) {
        if (advertisement.getSource() == AdvertisementSource.YOUTUBE && advertisement.getYoutubeDetails() != null) {
            String videoId = advertisement.getYoutubeDetails().getVideoId();
            if (videoId != null && !videoId.equals(advertisement.getSourceIdentifier())) {
                exception.addError("youtubeDetails.videoId", "YouTube videoId must match sourceIdentifier for YOUTUBE ads");
            }
        }
    }

    private static void validateClickability(Advertisement advertisement, AdvertisementValidationException exception) {
        if (advertisement.isClickable()) {
            String url = advertisement.getTargetUrl();
            if (url == null || url.trim().isEmpty()) {
                exception.addError("targetUrl", "Target URL is required when ad is clickable");
            } else {
                try {
                    java.net.URI uri = java.net.URI.create(url.trim());
                    if (uri.getScheme() == null || (!uri.getScheme().equalsIgnoreCase("http") && !uri.getScheme().equalsIgnoreCase("https"))) {
                        exception.addError("targetUrl", "Target URL must start with http or https");
                    }
                } catch (Exception e) {
                    exception.addError("targetUrl", "Target URL is not a valid URI");
                }
            }
        }
    }

    /**
     * Deletes an advertisement by its unique identifier.
     * Verifies the advertisement exists before deletion and evicts all relevant caches.
     * 
     * @param id the unique identifier of the advertisement to delete
     * @throws AdvertisementNotFoundException if the advertisement is not found
     * @throws AdvertisementOperationException if the delete operation fails
     */
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
        if (id == null) {
            throw new IllegalArgumentException("Advertisement ID cannot be null");
        }
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
            
            // Use the targeting service to filter advertisements
            List<Advertisement> filtered = targetingService.filterByTargetingCriteria(activeAds, countryCode, userBioData, mood);
            return viewingPolicyService.orderForDisplayWithFairFirst(filtered);
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
            
            // Use the targeting service to filter advertisements by geo targeting
            List<Advertisement> filtered = targetingService.filterByGeoTargeting(activeAds, countryCode, region, city, latitude, longitude);
            return viewingPolicyService.orderForDisplayWithFairFirst(filtered);
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
            
            // Use the targeting service to filter advertisements by bio targeting
            List<Advertisement> filtered = targetingService.filterByBioTargeting(activeAds, age, gender, occupation, educationLevel, language, interests);
            return viewingPolicyService.orderForDisplayWithFairFirst(filtered);
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
            
            // Use the targeting service to filter advertisements by mood targeting
            List<Advertisement> filtered = targetingService.filterByMoodTargeting(activeAds, mood, intensity, timeOfDay, dayOfWeek, season);
            return viewingPolicyService.orderForDisplayWithFairFirst(filtered);
        } catch (Exception e) {
            throw new AdvertisementOperationException(
                    AdvertisementOperationException.OperationType.TARGETING,
                    "Failed to get mood-targeted advertisements",
                    e
            );
        }
    }

    @Override
    public void trackAdvertisementView(Long id) {
        // Ensure the advertisement exists (will throw if not found)
        getAdvertisementByIdOrThrow(id);
        var event = new com.example.adplatform.domain.event.AdvertisementViewedEvent(id, java.time.Instant.now());
        eventPublisher.publish(event);
        log.debug("Tracked advertisement view for id: {}", id);
    }

    @Override
    public void trackAdvertisementInteraction(Long id, String interactionType) {
        // Ensure the advertisement exists (will throw if not found)
        getAdvertisementByIdOrThrow(id);
        var event = new com.example.adplatform.domain.event.AdvertisementInteractedEvent(id, interactionType, java.time.Instant.now());
        eventPublisher.publish(event);
        log.debug("Tracked advertisement interaction for id: {} type: {}", id, interactionType);
    }
}
