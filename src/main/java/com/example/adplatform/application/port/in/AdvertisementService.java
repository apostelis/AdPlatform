package com.example.adplatform.application.port.in;

import com.example.adplatform.domain.model.Advertisement;
import com.example.adplatform.domain.model.AdvertisementSource;
import com.example.adplatform.domain.model.Mood;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Service port for Advertisement operations.
 * This is a port in the hexagonal architecture that defines
 * the contract for business operations on advertisements.
 */
public interface AdvertisementService {

    /**
     * Get all advertisements.
     *
     * @return List of all advertisements
     */
    List<Advertisement> getAllAdvertisements();
    
    /**
     * Get all advertisements with pagination.
     *
     * @param pageable pagination information
     * @return Page of advertisements
     */
    default Page<Advertisement> getAllAdvertisements(Pageable pageable) {
        throw new UnsupportedOperationException("Pagination not implemented yet");
    }

    /**
     * Get all active advertisements.
     *
     * @return List of active advertisements
     */
    List<Advertisement> getActiveAdvertisements();
    
    /**
     * Get all active advertisements with pagination.
     *
     * @param pageable pagination information
     * @return Page of active advertisements
     */
    default Page<Advertisement> getActiveAdvertisements(Pageable pageable) {
        throw new UnsupportedOperationException("Pagination not implemented yet");
    }

    /**
     * Get advertisement by ID.
     *
     * @param id The advertisement ID
     * @return Optional containing the advertisement if found
     */
    Optional<Advertisement> getAdvertisementById(Long id);

    /**
     * Save a new advertisement or update an existing one.
     *
     * @param advertisement The advertisement to save
     * @return The saved advertisement
     */
    Advertisement saveAdvertisement(Advertisement advertisement);

    /**
     * Delete an advertisement by ID.
     *
     * @param id The advertisement ID to delete
     */
    void deleteAdvertisement(Long id);

    /**
     * Get advertisements by source.
     *
     * @param source The source of the advertisement (STORAGE or YOUTUBE)
     * @return List of advertisements from the specified source
     */
    List<Advertisement> getAdvertisementsBySource(AdvertisementSource source);
    
    /**
     * Get advertisements by source with pagination.
     *
     * @param source The source of the advertisement (STORAGE or YOUTUBE)
     * @param pageable pagination information
     * @return Page of advertisements from the specified source
     */
    default Page<Advertisement> getAdvertisementsBySource(AdvertisementSource source, Pageable pageable) {
        throw new UnsupportedOperationException("Pagination not implemented yet");
    }

    /**
     * Get advertisements by title containing the given text.
     *
     * @param title The text to search for in advertisement titles
     * @return List of advertisements with matching titles
     */
    List<Advertisement> getAdvertisementsByTitle(String title);
    
    /**
     * Get advertisements by title containing the given text with pagination.
     *
     * @param title The text to search for in advertisement titles
     * @param pageable pagination information
     * @return Page of advertisements with matching titles
     */
    default Page<Advertisement> getAdvertisementsByTitle(String title, Pageable pageable) {
        throw new UnsupportedOperationException("Pagination not implemented yet");
    }

    /**
     * Get targeted advertisements based on user context.
     *
     * @param countryCode The user's country code
     * @param userBioData Map containing user biographical data
     * @param mood The user's current mood
     * @return List of advertisements targeted to the user
     */
    List<Advertisement> getTargetedAdvertisements(
            String countryCode,
            Map<String, Object> userBioData,
            Mood mood
    );

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
    List<Advertisement> getGeoTargetedAdvertisements(
            String countryCode,
            String region,
            String city,
            Double latitude,
            Double longitude
    );

    /**
     * Get advertisements targeted by biographical data.
     *
     * @param age The user's age
     * @param gender The user's gender
     * @param occupation The user's occupation
     * @param educationLevel The user's education level
     * @param language The user's language
     * @param interests The user's interests
     * @return List of advertisements targeted by biographical data
     */
    List<Advertisement> getBioTargetedAdvertisements(
            Integer age,
            String gender,
            String occupation,
            String educationLevel,
            String language,
            Set<String> interests
    );

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
    List<Advertisement> getMoodTargetedAdvertisements(
            Mood mood,
            Integer intensity,
            String timeOfDay,
            String dayOfWeek,
            String season
    );
}