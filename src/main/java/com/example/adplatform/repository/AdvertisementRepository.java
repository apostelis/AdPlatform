package com.example.adplatform.repository;

import com.example.adplatform.model.Advertisement;
import com.example.adplatform.model.AdvertisementSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Advertisement entity.
 * Provides methods to interact with the advertisements database table.
 */
@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    /**
     * Find all active advertisements.
     *
     * @return List of active advertisements
     */
    List<Advertisement> findByActiveTrue();

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
     * Custom query to find advertisements that match a specific country code.
     *
     * @param countryCode The ISO country code to match
     * @return List of advertisements targeting the specified country
     */
    @Query("SELECT DISTINCT a FROM Advertisement a JOIN a.geoTargets g WHERE g.countryCode = :countryCode AND g.include = true AND a.active = true")
    List<Advertisement> findByCountryCode(@Param("countryCode") String countryCode);

    /**
     * Custom query to find advertisements that match a specific age range.
     *
     * @param age The age to match against the min and max age range
     * @return List of advertisements targeting the specified age
     */
    @Query("SELECT DISTINCT a FROM Advertisement a JOIN a.bioTargets b WHERE " +
           "(b.minAge IS NULL OR b.minAge <= :age) AND " +
           "(b.maxAge IS NULL OR b.maxAge >= :age) AND " +
           "b.include = true AND a.active = true")
    List<Advertisement> findByAgeRange(@Param("age") Integer age);

    /**
     * Custom query to find advertisements that match a specific mood.
     *
     * @param mood The mood to match (as a string representation of the Mood enum)
     * @return List of advertisements targeting the specified mood
     */
    @Query("SELECT DISTINCT a FROM Advertisement a JOIN a.moodTargets m WHERE " +
           "m.mood = :mood AND m.include = true AND a.active = true")
    List<Advertisement> findByMood(@Param("mood") String mood);
}