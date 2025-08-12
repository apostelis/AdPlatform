package com.example.adplatform.infrastructure.persistence.adapter;

import com.example.adplatform.application.port.out.AdvertisementRepository;
import com.example.adplatform.domain.model.*;
import com.example.adplatform.infrastructure.persistence.entity.*;
import com.example.adplatform.infrastructure.persistence.repository.AdvertisementJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementation of the AdvertisementRepository port.
 * This is an adapter in the hexagonal architecture that connects
 * the application core to the infrastructure layer.
 */
@Component
@RequiredArgsConstructor
@Transactional
public class AdvertisementRepositoryAdapter implements AdvertisementRepository {

    private final AdvertisementJpaRepository jpaRepository;

    @Override
    public List<Advertisement> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::mapToAdvertisement)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<Advertisement> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(this::mapToAdvertisement);
    }

    @Override
    public List<Advertisement> findByActiveTrue() {
        return jpaRepository.findByActiveTrue().stream()
                .map(this::mapToAdvertisement)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<Advertisement> findByActiveTrue(Pageable pageable) {
        return jpaRepository.findByActiveTrue(pageable)
                .map(this::mapToAdvertisement);
    }

    @Override
    public Optional<Advertisement> findById(Long id) {
        return jpaRepository.findById(id)
                .map(this::mapToAdvertisement);
    }

    @Override
    public Advertisement save(Advertisement advertisement) {
        AdvertisementJpaEntity jpaEntity = mapToJpaEntity(advertisement);
        AdvertisementJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return mapToAdvertisement(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Advertisement> findBySource(AdvertisementSource source) {
        return jpaRepository.findBySource(source).stream()
                .map(this::mapToAdvertisement)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<Advertisement> findBySource(AdvertisementSource source, Pageable pageable) {
        return jpaRepository.findBySource(source, pageable)
                .map(this::mapToAdvertisement);
    }

    @Override
    public List<Advertisement> findBySourceAndActiveTrue(AdvertisementSource source) {
        return jpaRepository.findBySourceAndActiveTrue(source).stream()
                .map(this::mapToAdvertisement)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<Advertisement> findBySourceAndActiveTrue(AdvertisementSource source, Pageable pageable) {
        return jpaRepository.findBySourceAndActiveTrue(source, pageable)
                .map(this::mapToAdvertisement);
    }

    @Override
    public List<Advertisement> findByTitleContainingIgnoreCase(String title) {
        return jpaRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::mapToAdvertisement)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<Advertisement> findByTitleContainingIgnoreCase(String title, Pageable pageable) {
        return jpaRepository.findByTitleContainingIgnoreCase(title, pageable)
                .map(this::mapToAdvertisement);
    }

    @Override
    public List<Advertisement> findByCountryCode(String countryCode) {
        return jpaRepository.findByCountryCode(countryCode).stream()
                .map(this::mapToAdvertisement)
                .collect(Collectors.toList());
    }

    @Override
    public List<Advertisement> findByAgeRange(Integer age) {
        return jpaRepository.findByAgeRange(age).stream()
                .map(this::mapToAdvertisement)
                .collect(Collectors.toList());
    }

    @Override
    public List<Advertisement> findByMood(Mood mood) {
        MoodTargetJpaEntity.MoodJpaEnum moodJpaEnum = mapToMoodJpaEnum(mood);
        return jpaRepository.findByMood(moodJpaEnum).stream()
                .map(this::mapToAdvertisement)
                .collect(Collectors.toList());
    }

    /**
     * Map a JPA entity to a domain entity.
     */
    private Advertisement mapToAdvertisement(AdvertisementJpaEntity jpaEntity) {
        return Advertisement.builder()
                .id(jpaEntity.getId())
                .title(jpaEntity.getTitle())
                .description(jpaEntity.getDescription())
                .content(jpaEntity.getContent())
                .source(jpaEntity.getSource())
                .sourceIdentifier(jpaEntity.getSourceIdentifier())
                .createdAt(jpaEntity.getCreatedAt())
                .updatedAt(jpaEntity.getUpdatedAt())
                .active(jpaEntity.isActive())
                .targetUrl(jpaEntity.getTargetUrl())
                .clickable(jpaEntity.isClickable())
                .weight(jpaEntity.getWeight())
                .overrideStart(jpaEntity.getOverrideStart())
                .overrideEnd(jpaEntity.getOverrideEnd())
                .youtubeDetails(mapToYouTubeDetails(jpaEntity.getYoutubeDetails()))
                .geoTargets(jpaEntity.getGeoTargets() != null 
                        ? jpaEntity.getGeoTargets().stream()
                            .map(this::mapToGeoTarget)
                            .collect(Collectors.toSet())
                        : null)
                .bioTargets(jpaEntity.getBioTargets() != null 
                        ? jpaEntity.getBioTargets().stream()
                            .map(this::mapToBioTarget)
                            .collect(Collectors.toSet())
                        : null)
                .moodTargets(jpaEntity.getMoodTargets() != null 
                        ? jpaEntity.getMoodTargets().stream()
                            .map(this::mapToMoodTarget)
                            .collect(Collectors.toSet())
                        : null)
                .build();
    }

    /**
     * Map a domain entity to a JPA entity.
     */
    private AdvertisementJpaEntity mapToJpaEntity(Advertisement advertisement) {
        return AdvertisementJpaEntity.builder()
                .id(advertisement.getId())
                .title(advertisement.getTitle())
                .description(advertisement.getDescription())
                .content(advertisement.getContent())
                .source(advertisement.getSource())
                .sourceIdentifier(advertisement.getSourceIdentifier())
                .createdAt(advertisement.getCreatedAt())
                .updatedAt(advertisement.getUpdatedAt())
                .active(advertisement.isActive())
                .targetUrl(advertisement.getTargetUrl())
                .clickable(advertisement.isClickable())
                .weight(advertisement.getWeight())
                .overrideStart(advertisement.getOverrideStart())
                .overrideEnd(advertisement.getOverrideEnd())
                .youtubeDetails(mapToYouTubeDetailsEmbeddable(advertisement.getYoutubeDetails()))
                .geoTargets((advertisement.getGeoTargets() != null && !advertisement.getGeoTargets().isEmpty())
                        ? advertisement.getGeoTargets().stream()
                            .map(this::mapToGeoTargetJpaEntity)
                            .collect(Collectors.toSet())
                        : null)
                .bioTargets((advertisement.getBioTargets() != null && !advertisement.getBioTargets().isEmpty())
                        ? advertisement.getBioTargets().stream()
                            .map(this::mapToBioTargetJpaEntity)
                            .collect(Collectors.toSet())
                        : null)
                .moodTargets((advertisement.getMoodTargets() != null && !advertisement.getMoodTargets().isEmpty())
                        ? advertisement.getMoodTargets().stream()
                            .map(this::mapToMoodTargetJpaEntity)
                            .collect(Collectors.toSet())
                        : null)
                .build();
    }

    /**
     * Map a JPA entity to a domain entity.
     */
    private GeoTarget mapToGeoTarget(GeoTargetJpaEntity jpaEntity) {
        return GeoTarget.builder()
                .countryCode(jpaEntity.getCountryCode())
                .region(jpaEntity.getRegion())
                .city(jpaEntity.getCity())
                .latitude(jpaEntity.getLatitude())
                .longitude(jpaEntity.getLongitude())
                .radiusKm(jpaEntity.getRadiusKm())
                .include(jpaEntity.isInclude())
                .build();
    }

    /**
     * Map a domain entity to a JPA entity.
     */
    private GeoTargetJpaEntity mapToGeoTargetJpaEntity(GeoTarget geoTarget) {
        return GeoTargetJpaEntity.builder()
                .countryCode(geoTarget.getCountryCode())
                .region(geoTarget.getRegion())
                .city(geoTarget.getCity())
                .latitude(geoTarget.getLatitude())
                .longitude(geoTarget.getLongitude())
                .radiusKm(geoTarget.getRadiusKm())
                .include(geoTarget.isInclude())
                .build();
    }

    /**
     * Map a JPA entity to a domain entity.
     */
    private BioTarget mapToBioTarget(BioTargetJpaEntity jpaEntity) {
        return BioTarget.builder()
                .minAge(jpaEntity.getMinAge())
                .maxAge(jpaEntity.getMaxAge())
                .gender(mapToGender(jpaEntity.getGender()))
                .occupation(jpaEntity.getOccupation())
                .educationLevel(jpaEntity.getEducationLevel())
                .language(jpaEntity.getLanguage())
                .interestCategory(jpaEntity.getInterestCategory())
                .include(jpaEntity.isInclude())
                .build();
    }

    /**
     * Map a domain entity to a JPA entity.
     */
    private BioTargetJpaEntity mapToBioTargetJpaEntity(BioTarget bioTarget) {
        return BioTargetJpaEntity.builder()
                .minAge(bioTarget.getMinAge())
                .maxAge(bioTarget.getMaxAge())
                .gender(mapToGenderJpaEnum(bioTarget.getGender()))
                .occupation(bioTarget.getOccupation())
                .educationLevel(bioTarget.getEducationLevel())
                .language(bioTarget.getLanguage())
                .interestCategory(bioTarget.getInterestCategory())
                .include(bioTarget.isInclude())
                .build();
    }

    /**
     * Map a JPA entity to a domain entity.
     */
    private MoodTarget mapToMoodTarget(MoodTargetJpaEntity jpaEntity) {
        return MoodTarget.builder()
                .mood(mapToMood(jpaEntity.getMood()))
                .intensityMin(jpaEntity.getIntensityMin())
                .intensityMax(jpaEntity.getIntensityMax())
                .timeOfDay(jpaEntity.getTimeOfDay())
                .dayOfWeek(jpaEntity.getDayOfWeek())
                .season(jpaEntity.getSeason())
                .include(jpaEntity.isInclude())
                .build();
    }

    /**
     * Map a domain entity to a JPA entity.
     */
    private MoodTargetJpaEntity mapToMoodTargetJpaEntity(MoodTarget moodTarget) {
        return MoodTargetJpaEntity.builder()
                .mood(mapToMoodJpaEnum(moodTarget.getMood()))
                .intensityMin(moodTarget.getIntensityMin())
                .intensityMax(moodTarget.getIntensityMax())
                .timeOfDay(moodTarget.getTimeOfDay())
                .dayOfWeek(moodTarget.getDayOfWeek())
                .season(moodTarget.getSeason())
                .include(moodTarget.isInclude())
                .build();
    }

    private YouTubeDetails mapToYouTubeDetails(YouTubeDetailsEmbeddable embeddable) {
        if (embeddable == null) return null;
        return YouTubeDetails.builder()
                .videoId(embeddable.getVideoId())
                .videoTitle(embeddable.getVideoTitle())
                .channelId(embeddable.getChannelId())
                .channelTitle(embeddable.getChannelTitle())
                .durationSeconds(embeddable.getDurationSeconds())
                .thumbnailUrl(embeddable.getThumbnailUrl())
                .publishedAt(embeddable.getPublishedAt())
                .build();
    }

    private YouTubeDetailsEmbeddable mapToYouTubeDetailsEmbeddable(YouTubeDetails details) {
        if (details == null) return null;
        return YouTubeDetailsEmbeddable.builder()
                .videoId(details.getVideoId())
                .videoTitle(details.getVideoTitle())
                .channelId(details.getChannelId())
                .channelTitle(details.getChannelTitle())
                .durationSeconds(details.getDurationSeconds())
                .thumbnailUrl(details.getThumbnailUrl())
                .publishedAt(details.getPublishedAt())
                .build();
    }

    /**
     * Map a JPA enum to a domain enum.
     */
    private Gender mapToGender(BioTargetJpaEntity.GenderJpaEnum genderJpaEnum) {
        if (genderJpaEnum == null) {
            return null;
        }
        // Map by enum name to avoid duplicate switch logic
        return Gender.valueOf(genderJpaEnum.name());
    }

    /**
     * Map a domain enum to a JPA enum.
     */
    private BioTargetJpaEntity.GenderJpaEnum mapToGenderJpaEnum(Gender gender) {
        if (gender == null) {
            return null;
        }
        // Map by enum name to avoid duplicate switch logic
        return BioTargetJpaEntity.GenderJpaEnum.valueOf(gender.name());
    }

    /**
     * Map a JPA enum to a domain enum.
     */
    private Mood mapToMood(MoodTargetJpaEntity.MoodJpaEnum moodJpaEnum) {
        if (moodJpaEnum == null) {
            return null;
        }
        // Map by enum name to avoid duplicate switch logic
        return Mood.valueOf(moodJpaEnum.name());
    }

    /**
     * Map a domain enum to a JPA enum.
     */
    private MoodTargetJpaEntity.MoodJpaEnum mapToMoodJpaEnum(Mood mood) {
        if (mood == null) {
            return null;
        }
        // Map by enum name to avoid duplicate switch logic
        return MoodTargetJpaEntity.MoodJpaEnum.valueOf(mood.name());
    }
}