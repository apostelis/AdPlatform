package com.example.adplatform.controller;

import com.example.adplatform.model.Advertisement;
import com.example.adplatform.model.AdvertisementSource;
import com.example.adplatform.model.MoodTarget.Mood;
import com.example.adplatform.service.AdvertisementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for Advertisement operations.
 * Provides endpoints for managing advertisements and implementing targeting logic.
 */
@RestController
@RequestMapping("/advertisements")
@RequiredArgsConstructor
@Slf4j
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    /**
     * Get all advertisements.
     *
     * @return List of all advertisements
     */
    @GetMapping
    public ResponseEntity<List<Advertisement>> getAllAdvertisements() {
        log.debug("REST request to get all advertisements");
        return ResponseEntity.ok(advertisementService.getAllAdvertisements());
    }

    /**
     * Get all active advertisements.
     *
     * @return List of active advertisements
     */
    @GetMapping("/active")
    public ResponseEntity<List<Advertisement>> getActiveAdvertisements() {
        log.debug("REST request to get all active advertisements");
        return ResponseEntity.ok(advertisementService.getActiveAdvertisements());
    }

    /**
     * Get advertisement by ID.
     *
     * @param id The advertisement ID
     * @return The advertisement if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Advertisement> getAdvertisementById(@PathVariable Long id) {
        log.debug("REST request to get advertisement with id: {}", id);
        return advertisementService.getAdvertisementById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new advertisement.
     *
     * @param advertisement The advertisement to create
     * @return The created advertisement
     */
    @PostMapping
    public ResponseEntity<Advertisement> createAdvertisement(@Valid @RequestBody Advertisement advertisement) {
        log.debug("REST request to create advertisement: {}", advertisement);
        if (advertisement.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        Advertisement result = advertisementService.saveAdvertisement(advertisement);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Update an existing advertisement.
     *
     * @param id The advertisement ID
     * @param advertisement The advertisement to update
     * @return The updated advertisement
     */
    @PutMapping("/{id}")
    public ResponseEntity<Advertisement> updateAdvertisement(
            @PathVariable Long id,
            @Valid @RequestBody Advertisement advertisement
    ) {
        log.debug("REST request to update advertisement with id: {}", id);
        if (advertisement.getId() == null || !advertisement.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }
        if (!advertisementService.getAdvertisementById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Advertisement result = advertisementService.saveAdvertisement(advertisement);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete an advertisement by ID.
     *
     * @param id The advertisement ID to delete
     * @return No content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdvertisement(@PathVariable Long id) {
        log.debug("REST request to delete advertisement with id: {}", id);
        if (!advertisementService.getAdvertisementById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        advertisementService.deleteAdvertisement(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get advertisements by source.
     *
     * @param source The source of the advertisement (STORAGE or YOUTUBE)
     * @return List of advertisements from the specified source
     */
    @GetMapping("/source/{source}")
    public ResponseEntity<List<Advertisement>> getAdvertisementsBySource(@PathVariable AdvertisementSource source) {
        log.debug("REST request to get advertisements by source: {}", source);
        return ResponseEntity.ok(advertisementService.getAdvertisementsBySource(source));
    }

    /**
     * Get advertisements by title containing the given text.
     *
     * @param title The text to search for in advertisement titles
     * @return List of advertisements with matching titles
     */
    @GetMapping("/search")
    public ResponseEntity<List<Advertisement>> getAdvertisementsByTitle(@RequestParam String title) {
        log.debug("REST request to get advertisements by title containing: {}", title);
        return ResponseEntity.ok(advertisementService.getAdvertisementsByTitle(title));
    }

    /**
     * Get targeted advertisements based on user context.
     *
     * @param countryCode The user's country code
     * @param userBioData Map containing user biographical data
     * @param mood The user's current mood
     * @return List of advertisements targeted to the user
     */
    @PostMapping("/targeted")
    public ResponseEntity<List<Advertisement>> getTargetedAdvertisements(
            @RequestParam(required = false) String countryCode,
            @RequestBody(required = false) Map<String, Object> userBioData,
            @RequestParam(required = false) Mood mood
    ) {
        log.debug("REST request to get targeted advertisements for country: {}, mood: {}", countryCode, mood);
        return ResponseEntity.ok(advertisementService.getTargetedAdvertisements(countryCode, userBioData, mood));
    }

    /**
     * Get advertisements targeted by geolocation.
     *
     * @param countryCode The user's country code
     * @param region The user's region/state
     * @param city The user's city
     * @param latitude The user's latitude
     * @param longitude The user's longitude
     * @return List of advertisements targeted by geolocation
     */
    @GetMapping("/geo-targeted")
    public ResponseEntity<List<Advertisement>> getGeoTargetedAdvertisements(
            @RequestParam(required = false) String countryCode,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude
    ) {
        log.debug("REST request to get geo-targeted advertisements for country: {}, region: {}, city: {}", 
                countryCode, region, city);
        return ResponseEntity.ok(advertisementService.getGeoTargetedAdvertisements(
                countryCode, region, city, latitude, longitude));
    }

    /**
     * Get advertisements targeted by mood.
     *
     * @param mood The user's current mood
     * @param intensity The intensity of the mood (1-10)
     * @param timeOfDay The current time of day
     * @param dayOfWeek The current day of week
     * @param season The current season
     * @return List of advertisements targeted by mood
     */
    @GetMapping("/mood-targeted")
    public ResponseEntity<List<Advertisement>> getMoodTargetedAdvertisements(
            @RequestParam Mood mood,
            @RequestParam(required = false) Integer intensity,
            @RequestParam(required = false) String timeOfDay,
            @RequestParam(required = false) String dayOfWeek,
            @RequestParam(required = false) String season
    ) {
        log.debug("REST request to get mood-targeted advertisements for mood: {}, intensity: {}", 
                mood, intensity);
        return ResponseEntity.ok(advertisementService.getMoodTargetedAdvertisements(
                mood, intensity, timeOfDay, dayOfWeek, season));
    }
}