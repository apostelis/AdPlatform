package com.example.adplatform.infrastructure.web.mapper;

import com.example.adplatform.domain.model.*;
import com.example.adplatform.infrastructure.web.dto.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper for converting between domain entities and DTOs.
 * This is an infrastructure component in the hexagonal architecture.
 */
@Component
public class AdvertisementMapper {

    /**
     * Convert a domain entity to a DTO.
     */
    public AdvertisementDTO toDto(Advertisement advertisement) {
        if (advertisement == null) {
            return null;
        }
        
        return AdvertisementDTO.builder()
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
                .youtubeDetails(toDto(advertisement.getYoutubeDetails()))
                .geoTargets(advertisement.getGeoTargets().stream()
                        .map(this::toDto)
                        .collect(Collectors.toSet()))
                .bioTargets(advertisement.getBioTargets().stream()
                        .map(this::toDto)
                        .collect(Collectors.toSet()))
                .moodTargets(advertisement.getMoodTargets().stream()
                        .map(this::toDto)
                        .collect(Collectors.toSet()))
                .build();
    }

    /**
     * Convert a DTO to a domain entity.
     */
    public Advertisement toDomain(AdvertisementDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return Advertisement.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .content(dto.getContent())
                .source(dto.getSource())
                .sourceIdentifier(dto.getSourceIdentifier())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .active(dto.isActive())
                .targetUrl(dto.getTargetUrl())
                .clickable(dto.isClickable())
                .weight(dto.getWeight())
                .overrideStart(dto.getOverrideStart())
                .overrideEnd(dto.getOverrideEnd())
                .youtubeDetails(toDomain(dto.getYoutubeDetails()))
                .geoTargets(dto.getGeoTargets().stream()
                        .map(this::toDomain)
                        .collect(Collectors.toSet()))
                .bioTargets(dto.getBioTargets().stream()
                        .map(this::toDomain)
                        .collect(Collectors.toSet()))
                .moodTargets(dto.getMoodTargets().stream()
                        .map(this::toDomain)
                        .collect(Collectors.toSet()))
                .build();
    }

    /**
     * Convert a domain entity to a DTO.
     */
    public GeoTargetDTO toDto(GeoTarget geoTarget) {
        if (geoTarget == null) {
            return null;
        }
        
        return GeoTargetDTO.builder()
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
     * Convert a DTO to a domain entity.
     */
    public GeoTarget toDomain(GeoTargetDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return GeoTarget.builder()
                .countryCode(dto.getCountryCode())
                .region(dto.getRegion())
                .city(dto.getCity())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .radiusKm(dto.getRadiusKm())
                .include(dto.isInclude())
                .build();
    }

    /**
     * Convert a domain entity to a DTO.
     */
    public BioTargetDTO toDto(BioTarget bioTarget) {
        if (bioTarget == null) {
            return null;
        }
        
        return BioTargetDTO.builder()
                .minAge(bioTarget.getMinAge())
                .maxAge(bioTarget.getMaxAge())
                .gender(bioTarget.getGender() != null ? bioTarget.getGender().name() : null)
                .occupation(bioTarget.getOccupation())
                .educationLevel(bioTarget.getEducationLevel())
                .language(bioTarget.getLanguage())
                .interestCategory(bioTarget.getInterestCategory())
                .include(bioTarget.isInclude())
                .build();
    }

    /**
     * Convert a DTO to a domain entity.
     */
    public BioTarget toDomain(BioTargetDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return BioTarget.builder()
                .minAge(dto.getMinAge())
                .maxAge(dto.getMaxAge())
                .gender(dto.getGender() != null ? Gender.fromString(dto.getGender()) : null)
                .occupation(dto.getOccupation())
                .educationLevel(dto.getEducationLevel())
                .language(dto.getLanguage())
                .interestCategory(dto.getInterestCategory())
                .include(dto.isInclude())
                .build();
    }

    /**
     * Convert a domain entity to a DTO.
     */
    public MoodTargetDTO toDto(MoodTarget moodTarget) {
        if (moodTarget == null) {
            return null;
        }
        
        return MoodTargetDTO.builder()
                .mood(moodTarget.getMood() != null ? moodTarget.getMood().name() : null)
                .intensityMin(moodTarget.getIntensityMin())
                .intensityMax(moodTarget.getIntensityMax())
                .timeOfDay(moodTarget.getTimeOfDay())
                .dayOfWeek(moodTarget.getDayOfWeek())
                .season(moodTarget.getSeason())
                .include(moodTarget.isInclude())
                .build();
    }

    /**
     * Convert a DTO to a domain entity.
     */
    public MoodTarget toDomain(MoodTargetDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return MoodTarget.builder()
                .mood(dto.getMood() != null ? Mood.fromString(dto.getMood()) : null)
                .intensityMin(dto.getIntensityMin())
                .intensityMax(dto.getIntensityMax())
                .timeOfDay(dto.getTimeOfDay())
                .dayOfWeek(dto.getDayOfWeek())
                .season(dto.getSeason())
                .include(dto.isInclude())
                .build();
    }

    public YouTubeDetailsDTO toDto(YouTubeDetails details) {
        if (details == null) {
            return null;
        }
        return YouTubeDetailsDTO.builder()
                .videoId(details.getVideoId())
                .videoTitle(details.getVideoTitle())
                .channelId(details.getChannelId())
                .channelTitle(details.getChannelTitle())
                .durationSeconds(details.getDurationSeconds())
                .thumbnailUrl(details.getThumbnailUrl())
                .publishedAt(details.getPublishedAt())
                .build();
    }

    public YouTubeDetails toDomain(YouTubeDetailsDTO dto) {
        if (dto == null) {
            return null;
        }
        return YouTubeDetails.builder()
                .videoId(dto.getVideoId())
                .videoTitle(dto.getVideoTitle())
                .channelId(dto.getChannelId())
                .channelTitle(dto.getChannelTitle())
                .durationSeconds(dto.getDurationSeconds())
                .thumbnailUrl(dto.getThumbnailUrl())
                .publishedAt(dto.getPublishedAt())
                .build();
    }
}