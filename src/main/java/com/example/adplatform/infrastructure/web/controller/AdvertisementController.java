package com.example.adplatform.infrastructure.web.controller;

import com.example.adplatform.application.port.in.AdvertisementService;
import com.example.adplatform.domain.model.Advertisement;
import com.example.adplatform.domain.model.AdvertisementSource;
import com.example.adplatform.domain.model.Mood;
import com.example.adplatform.infrastructure.web.dto.AdvertisementDTO;
import com.example.adplatform.infrastructure.web.mapper.AdvertisementMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for Advertisement operations.
 * This is an adapter in the hexagonal architecture that connects
 * the application core to the web layer.
 */
@RestController
@RequestMapping("/advertisements")
@RequiredArgsConstructor
@Slf4j
public class AdvertisementController {

    private final AdvertisementService advertisementService;
    private final AdvertisementMapper mapper;

    /**
     * Get all advertisements.
     *
     * @return List of all advertisements
     */
    @GetMapping
    public ResponseEntity<List<AdvertisementDTO>> getAllAdvertisements() {
        log.debug("REST request to get all advertisements");
        List<Advertisement> advertisements = advertisementService.getAllAdvertisements();
        List<AdvertisementDTO> dtos = advertisements.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Get all active advertisements.
     *
     * @return List of active advertisements
     */
    @GetMapping("/active")
    public ResponseEntity<List<AdvertisementDTO>> getActiveAdvertisements() {
        log.debug("REST request to get all active advertisements");
        List<Advertisement> advertisements = advertisementService.getActiveAdvertisements();
        List<AdvertisementDTO> dtos = advertisements.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Get advertisement by ID.
     *
     * @param id The advertisement ID
     * @return The advertisement if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdvertisementDTO> getAdvertisementById(@PathVariable Long id) {
        log.debug("REST request to get advertisement with id: {}", id);
        return advertisementService.getAdvertisementById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new advertisement.
     *
     * @param dto The advertisement to create
     * @return The created advertisement
     */
    @PostMapping
    public ResponseEntity<AdvertisementDTO> createAdvertisement(@RequestBody AdvertisementDTO dto) {
        log.debug("REST request to create advertisement: {}", dto);
        if (dto.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        Advertisement advertisement = mapper.toDomain(dto);
        Advertisement result = advertisementService.saveAdvertisement(advertisement);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(result));
    }

    /**
     * Update an existing advertisement.
     *
     * @param id The advertisement ID
     * @param dto The advertisement to update
     * @return The updated advertisement
     */
    @PutMapping("/{id}")
    public ResponseEntity<AdvertisementDTO> updateAdvertisement(
            @PathVariable Long id,
            @RequestBody AdvertisementDTO dto
    ) {
        log.debug("REST request to update advertisement with id: {}", id);
        if (dto.getId() == null || !dto.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }
        if (!advertisementService.getAdvertisementById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Advertisement advertisement = mapper.toDomain(dto);
        Advertisement result = advertisementService.saveAdvertisement(advertisement);
        return ResponseEntity.ok(mapper.toDto(result));
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
    public ResponseEntity<List<AdvertisementDTO>> getAdvertisementsBySource(@PathVariable AdvertisementSource source) {
        log.debug("REST request to get advertisements by source: {}", source);
        List<Advertisement> advertisements = advertisementService.getAdvertisementsBySource(source);
        List<AdvertisementDTO> dtos = advertisements.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Get advertisements by title containing the given text.
     *
     * @param title The text to search for in advertisement titles
     * @return List of advertisements with matching titles
     */
    @GetMapping("/search")
    public ResponseEntity<List<AdvertisementDTO>> getAdvertisementsByTitle(@RequestParam String title) {
        log.debug("REST request to get advertisements by title containing: {}", title);
        List<Advertisement> advertisements = advertisementService.getAdvertisementsByTitle(title);
        List<AdvertisementDTO> dtos = advertisements.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
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
    public ResponseEntity<List<AdvertisementDTO>> getTargetedAdvertisements(
            @RequestParam(required = false) String countryCode,
            @RequestBody(required = false) Map<String, Object> userBioData,
            @RequestParam(required = false) Mood mood
    ) {
        log.debug("REST request to get targeted advertisements for country: {}, mood: {}", countryCode, mood);
        List<Advertisement> advertisements = advertisementService.getTargetedAdvertisements(countryCode, userBioData, mood);
        List<AdvertisementDTO> dtos = advertisements.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
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
    public ResponseEntity<List<AdvertisementDTO>> getGeoTargetedAdvertisements(
            @RequestParam(required = false) String countryCode,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude
    ) {
        log.debug("REST request to get geo-targeted advertisements for country: {}, region: {}, city: {}", 
                countryCode, region, city);
        List<Advertisement> advertisements = advertisementService.getGeoTargetedAdvertisements(
                countryCode, region, city, latitude, longitude);
        List<AdvertisementDTO> dtos = advertisements.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
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
    public ResponseEntity<List<AdvertisementDTO>> getMoodTargetedAdvertisements(
            @RequestParam Mood mood,
            @RequestParam(required = false) Integer intensity,
            @RequestParam(required = false) String timeOfDay,
            @RequestParam(required = false) String dayOfWeek,
            @RequestParam(required = false) String season
    ) {
        log.debug("REST request to get mood-targeted advertisements for mood: {}, intensity: {}", 
                mood, intensity);
        List<Advertisement> advertisements = advertisementService.getMoodTargetedAdvertisements(
                mood, intensity, timeOfDay, dayOfWeek, season);
        List<AdvertisementDTO> dtos = advertisements.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}