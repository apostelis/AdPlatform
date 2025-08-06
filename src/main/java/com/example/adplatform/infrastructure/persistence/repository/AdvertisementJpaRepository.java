package com.example.adplatform.infrastructure.persistence.repository;

import com.example.adplatform.domain.model.AdvertisementSource;
import com.example.adplatform.infrastructure.persistence.entity.AdvertisementJpaEntity;
import com.example.adplatform.infrastructure.persistence.entity.MoodTargetJpaEntity.MoodJpaEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA repository for Advertisement entity.
 * This is an infrastructure component in the hexagonal architecture.
 */
@Repository
public interface AdvertisementJpaRepository extends JpaRepository<AdvertisementJpaEntity, Long> {

    /**
     * Find all active advertisements.
     *
     * @return List of active advertisements
     */
    List<AdvertisementJpaEntity> findByActiveTrue();

    /**
     * Find advertisements by source.
     *
     * @param source The source of the advertisement (STORAGE or YOUTUBE)
     * @return List of advertisements from the specified source
     */
    List<AdvertisementJpaEntity> findBySource(AdvertisementSource source);

    /**
     * Find active advertisements by source.
     *
     * @param source The source of the advertisement
     * @return List of active advertisements from the specified source
     */
    List<AdvertisementJpaEntity> findBySourceAndActiveTrue(AdvertisementSource source);

    /**
     * Find advertisements by title containing the given text (case insensitive).
     *
     * @param title The text to search for in advertisement titles
     * @return List of advertisements with matching titles
     */
    List<AdvertisementJpaEntity> findByTitleContainingIgnoreCase(String title);

    /**
     * Custom query to find advertisements that match a specific country code.
     *
     * @param countryCode The ISO country code to match
     * @return List of advertisements targeting the specified country
     */
    @Query("SELECT DISTINCT a FROM AdvertisementJpaEntity a JOIN a.geoTargets g WHERE g.countryCode = :countryCode AND g.include = true AND a.active = true")
    List<AdvertisementJpaEntity> findByCountryCode(@Param("countryCode") String countryCode);

    /**
     * Custom query to find advertisements that match a specific age range.
     *
     * @param age The age to match against the min and max age range
     * @return List of advertisements targeting the specified age
     */
    @Query("SELECT DISTINCT a FROM AdvertisementJpaEntity a JOIN a.bioTargets b WHERE " +
           "(b.minAge IS NULL OR b.minAge <= :age) AND " +
           "(b.maxAge IS NULL OR b.maxAge >= :age) AND " +
           "b.include = true AND a.active = true")
    List<AdvertisementJpaEntity> findByAgeRange(@Param("age") Integer age);

    /**
     * Custom query to find advertisements that match a specific mood.
     *
     * @param mood The mood to match
     * @return List of advertisements targeting the specified mood
     */
    @Query("SELECT DISTINCT a FROM AdvertisementJpaEntity a JOIN a.moodTargets m WHERE " +
           "m.mood = :mood AND m.include = true AND a.active = true")
    List<AdvertisementJpaEntity> findByMood(@Param("mood") MoodJpaEnum mood);
}