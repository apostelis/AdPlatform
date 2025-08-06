package com.example.adplatform.application.port.out;

import com.example.adplatform.domain.model.Advertisement;
import com.example.adplatform.domain.model.AdvertisementSource;
import com.example.adplatform.domain.model.Mood;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for Advertisement domain entity.
 * This is a port in the hexagonal architecture that defines
 * the contract for persistence operations on advertisements.
 */
public interface AdvertisementRepository {

    /**
     * Find all advertisements.
     *
     * @return List of all advertisements
     */
    List<Advertisement> findAll();

    /**
     * Find all active advertisements.
     *
     * @return List of active advertisements
     */
    List<Advertisement> findByActiveTrue();

    /**
     * Find advertisement by ID.
     *
     * @param id The advertisement ID
     * @return Optional containing the advertisement if found
     */
    Optional<Advertisement> findById(Long id);

    /**
     * Save a new advertisement or update an existing one.
     *
     * @param advertisement The advertisement to save
     * @return The saved advertisement
     */
    Advertisement save(Advertisement advertisement);

    /**
     * Delete an advertisement by ID.
     *
     * @param id The advertisement ID to delete
     */
    void deleteById(Long id);

    /**
     * Find advertisements by source.
     *
     * @param source The source of the advertisement (STORAGE or YOUTUBE)
     * @return List of advertisements from the specified source
     */
    List<Advertisement> findBySource(AdvertisementSource source);

    /**
     * Find active advertisements by source.
     *
     * @param source The source of the advertisement
     * @return List of active advertisements from the specified source
     */
    List<Advertisement> findBySourceAndActiveTrue(AdvertisementSource source);

    /**
     * Find advertisements by title containing the given text (case insensitive).
     *
     * @param title The text to search for in advertisement titles
     * @return List of advertisements with matching titles
     */
    List<Advertisement> findByTitleContainingIgnoreCase(String title);

    /**
     * Find advertisements that match a specific country code.
     *
     * @param countryCode The ISO country code to match
     * @return List of advertisements targeting the specified country
     */
    List<Advertisement> findByCountryCode(String countryCode);

    /**
     * Find advertisements that match a specific age range.
     *
     * @param age The age to match against the min and max age range
     * @return List of advertisements targeting the specified age
     */
    List<Advertisement> findByAgeRange(Integer age);

    /**
     * Find advertisements that match a specific mood.
     *
     * @param mood The mood to match
     * @return List of advertisements targeting the specified mood
     */
    List<Advertisement> findByMood(Mood mood);
}