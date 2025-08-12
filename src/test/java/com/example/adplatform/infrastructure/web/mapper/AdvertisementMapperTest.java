package com.example.adplatform.infrastructure.web.mapper;

import com.example.adplatform.domain.model.*;
import com.example.adplatform.infrastructure.web.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AdvertisementMapperTest {

    private AdvertisementMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AdvertisementMapper();
    }

    @Test
    @DisplayName("toDto should return null when input is null")
    void toDto_null() {
        assertNull(mapper.toDto((Advertisement) null));
        assertNull(mapper.toDto((GeoTarget) null));
        assertNull(mapper.toDto((BioTarget) null));
        assertNull(mapper.toDto((MoodTarget) null));
        assertNull(mapper.toDto((YouTubeDetails) null));
    }

    @Test
    @DisplayName("toDomain should return null when input is null")
    void toDomain_null() {
        assertNull(mapper.toDomain((AdvertisementDTO) null));
        assertNull(mapper.toDomain((GeoTargetDTO) null));
        assertNull(mapper.toDomain((BioTargetDTO) null));
        assertNull(mapper.toDomain((MoodTargetDTO) null));
        assertNull(mapper.toDomain((YouTubeDetailsDTO) null));
    }

    @Test
    @DisplayName("Should map Advertisement domain -> DTO including nested targets and YouTube details")
    void domainToDto_full() {
        LocalDateTime now = LocalDateTime.now();

        GeoTarget geo = GeoTarget.builder()
                .countryCode("US")
                .region("CA")
                .city("San Francisco")
                .latitude(37.7749)
                .longitude(-122.4194)
                .radiusKm(10)
                .include(true)
                .build();

        BioTarget bio = BioTarget.builder()
                .minAge(18)
                .maxAge(35)
                .gender(Gender.FEMALE)
                .occupation("Engineer")
                .educationLevel("Bachelor")
                .language("en")
                .interestCategory("Tech")
                .include(true)
                .build();

        MoodTarget mood = MoodTarget.builder()
                .mood(Mood.HAPPY)
                .intensityMin(3)
                .intensityMax(8)
                .timeOfDay("morning")
                .dayOfWeek("MONDAY")
                .season("SUMMER")
                .include(true)
                .build();

        YouTubeDetails yt = YouTubeDetails.builder()
                .videoId("vid123")
                .videoTitle("Great Video")
                .channelId("ch1")
                .channelTitle("Cool Channel")
                .durationSeconds(120)
                .thumbnailUrl("http://img")
                .publishedAt(now.minusDays(1).atZone(java.time.ZoneId.systemDefault()).toInstant())
                .build();

        Advertisement ad = Advertisement.builder()
                .id(42L)
                .title("Title")
                .description("Desc")
                .content("Content")
                .source(AdvertisementSource.YOUTUBE)
                .sourceIdentifier("vid123")
                .createdAt(now.minusDays(2))
                .updatedAt(now)
                .active(true)
                .targetUrl("https://example.com")
                .clickable(true)
                .weight(5)
                .overrideStart(now.minusDays(3))
                .overrideEnd(now.plusDays(3))
                .youtubeDetails(yt)
                .geoTargets(Set.of(geo))
                .bioTargets(Set.of(bio))
                .moodTargets(Set.of(mood))
                .build();

        AdvertisementDTO dto = mapper.toDto(ad);

        assertNotNull(dto);
        assertEquals(ad.getId(), dto.getId());
        assertEquals(ad.getTitle(), dto.getTitle());
        assertEquals(ad.getDescription(), dto.getDescription());
        assertEquals(ad.getContent(), dto.getContent());
        assertEquals(ad.getSource(), dto.getSource());
        assertEquals(ad.getSourceIdentifier(), dto.getSourceIdentifier());
        assertEquals(ad.getCreatedAt(), dto.getCreatedAt());
        assertEquals(ad.getUpdatedAt(), dto.getUpdatedAt());
        assertEquals(ad.isActive(), dto.isActive());
        assertEquals(ad.getTargetUrl(), dto.getTargetUrl());
        assertEquals(ad.isClickable(), dto.isClickable());
        assertEquals(ad.getWeight(), dto.getWeight());
        assertEquals(ad.getOverrideStart(), dto.getOverrideStart());
        assertEquals(ad.getOverrideEnd(), dto.getOverrideEnd());

        assertNotNull(dto.getYoutubeDetails());
        assertEquals(yt.getVideoId(), dto.getYoutubeDetails().getVideoId());
        assertEquals(1, dto.getGeoTargets().size());
        assertEquals(1, dto.getBioTargets().size());
        assertEquals(1, dto.getMoodTargets().size());
    }

    @Test
    @DisplayName("Should map Advertisement DTO -> domain including nested targets and YouTube details")
    void dtoToDomain_full() {
        LocalDateTime now = LocalDateTime.now();

        GeoTargetDTO geo = GeoTargetDTO.builder()
                .countryCode("US")
                .region("CA")
                .city("San Francisco")
                .latitude(37.7749)
                .longitude(-122.4194)
                .radiusKm(10)
                .include(true)
                .build();

        BioTargetDTO bio = BioTargetDTO.builder()
                .minAge(18)
                .maxAge(35)
                .gender("FEMALE")
                .occupation("Engineer")
                .educationLevel("Bachelor")
                .language("en")
                .interestCategory("Tech")
                .include(true)
                .build();

        MoodTargetDTO mood = MoodTargetDTO.builder()
                .mood("HAPPY")
                .intensityMin(3)
                .intensityMax(8)
                .timeOfDay("morning")
                .dayOfWeek("MONDAY")
                .season("SUMMER")
                .include(true)
                .build();

        YouTubeDetailsDTO yt = YouTubeDetailsDTO.builder()
                .videoId("vid123")
                .videoTitle("Great Video")
                .channelId("ch1")
                .channelTitle("Cool Channel")
                .durationSeconds(120)
                .thumbnailUrl("http://img")
                .publishedAt(now.minusDays(1).atZone(java.time.ZoneId.systemDefault()).toInstant())
                .build();

        AdvertisementDTO dto = AdvertisementDTO.builder()
                .id(42L)
                .title("Title")
                .description("Desc")
                .content("Content")
                .source(AdvertisementSource.YOUTUBE)
                .sourceIdentifier("vid123")
                .createdAt(now.minusDays(2))
                .updatedAt(now)
                .active(true)
                .targetUrl("https://example.com")
                .clickable(true)
                .weight(5)
                .overrideStart(now.minusDays(3))
                .overrideEnd(now.plusDays(3))
                .youtubeDetails(yt)
                .build();
        dto.getGeoTargets().add(geo);
        dto.getBioTargets().add(bio);
        dto.getMoodTargets().add(mood);

        Advertisement ad = mapper.toDomain(dto);

        assertNotNull(ad);
        assertEquals(dto.getId(), ad.getId());
        assertEquals(dto.getTitle(), ad.getTitle());
        assertEquals(dto.getDescription(), ad.getDescription());
        assertEquals(dto.getContent(), ad.getContent());
        assertEquals(dto.getSource(), ad.getSource());
        assertEquals(dto.getSourceIdentifier(), ad.getSourceIdentifier());
        assertEquals(dto.getCreatedAt(), ad.getCreatedAt());
        assertEquals(dto.getUpdatedAt(), ad.getUpdatedAt());
        assertEquals(dto.isActive(), ad.isActive());
        assertEquals(dto.getTargetUrl(), ad.getTargetUrl());
        assertEquals(dto.isClickable(), ad.isClickable());
        assertEquals(dto.getWeight(), ad.getWeight());
        assertEquals(dto.getOverrideStart(), ad.getOverrideStart());
        assertEquals(dto.getOverrideEnd(), ad.getOverrideEnd());

        assertNotNull(ad.getYoutubeDetails());
        assertEquals(yt.getVideoId(), ad.getYoutubeDetails().getVideoId());
        assertEquals(1, ad.getGeoTargets().size());
        assertEquals(1, ad.getBioTargets().size());
        assertEquals(1, ad.getMoodTargets().size());
    }

    @Test
    @DisplayName("Should tolerate optional enum fields being null during mapping")
    void enumNulls_tolerated() {
        BioTarget bio = BioTarget.builder()
                .minAge(20)
                .maxAge(30)
                .gender(null)
                .include(true)
                .build();
        BioTargetDTO bioDto = mapper.toDto(bio);
        assertNull(bioDto.getGender());

        BioTargetDTO in = BioTargetDTO.builder()
                .minAge(20)
                .maxAge(30)
                .gender(null)
                .include(true)
                .build();
        BioTarget out = mapper.toDomain(in);
        assertNull(out.getGender());

        MoodTargetDTO moodDto = MoodTargetDTO.builder()
                .mood(null)
                .include(true)
                .build();
        MoodTarget mood = mapper.toDomain(moodDto);
        assertNull(mood.getMood());

        MoodTarget moodDomain = MoodTarget.builder().mood(null).include(true).build();
        MoodTargetDTO moodDto2 = mapper.toDto(moodDomain);
        assertNull(moodDto2.getMood());
    }
}
