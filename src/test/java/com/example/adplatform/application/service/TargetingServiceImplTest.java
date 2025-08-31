package com.example.adplatform.application.service;

import com.example.adplatform.application.service.targeting.BioTargetingStrategy;
import com.example.adplatform.application.service.targeting.GeoTargetingStrategy;
import com.example.adplatform.application.service.targeting.MoodTargetingStrategy;
import com.example.adplatform.application.service.targeting.TargetingStrategy;
import com.example.adplatform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetingServiceImplTest {

    @Mock
    private GeoTargetingStrategy geoTargetingStrategy;

    @Mock
    private BioTargetingStrategy bioTargetingStrategy;

    @Mock
    private MoodTargetingStrategy moodTargetingStrategy;

    private TargetingServiceImpl targetingService;
    private Advertisement advertisement;
    private List<Advertisement> advertisements;

    @BeforeEach
    void setUp() {
        // Create the service with mocked strategies
        List<TargetingStrategy> strategyList = List.of(geoTargetingStrategy, bioTargetingStrategy, moodTargetingStrategy);
        targetingService = new TargetingServiceImpl(strategyList);

        // Mock strategy keys
        when(geoTargetingStrategy.key()).thenReturn("geo");
        when(bioTargetingStrategy.key()).thenReturn("bio");
        when(moodTargetingStrategy.key()).thenReturn("mood");

        // Create test advertisement with proper model structure
        advertisement = Advertisement.builder()
                .id(1L)
                .title("Test Ad")
                .description("Test Description")
                .content("Test Content")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("test-ad-123")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .build();

        advertisements = List.of(advertisement);
    }

    @Test
    void strategies_shouldReturnMapWithAllStrategies() {
        // When
        Map<String, Object> strategies = (Map<String, Object>) targetingService.strategies();

        // Then
        assertThat(strategies).hasSize(3);
        assertThat(strategies).containsKeys("geo", "bio", "mood");
    }

    @Test
    void filterByTargetingCriteria_withValidCriteria_shouldReturnFilteredAds() {
        // Given
        String countryCode = "US";
        Map<String, Object> userBioData = Map.of(
                "age", 25,
                "gender", "MALE",
                "occupation", "engineer"
        );
        Mood mood = Mood.HAPPY;

        when(geoTargetingStrategy.matches(eq(advertisement), eq(countryCode), any(), any(), any(), any()))
                .thenReturn(true);
        when(bioTargetingStrategy.matches(eq(advertisement), any(Map.class)))
                .thenReturn(true);
        when(moodTargetingStrategy.matches(eq(advertisement), eq(mood), any(), any(), any(), any()))
                .thenReturn(true);

        // When
        List<Advertisement> result = targetingService.filterByTargetingCriteria(advertisements, countryCode, userBioData, mood);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).contains(advertisement);
    }

    @Test
    void filterByTargetingCriteria_withNonMatchingCriteria_shouldReturnEmptyList() {
        // Given
        String countryCode = "FR";
        Map<String, Object> userBioData = Map.of("age", 17);
        Mood mood = Mood.SAD;

        when(geoTargetingStrategy.matches(any(), any(), any(), any(), any(), any()))
                .thenReturn(false);

        // When
        List<Advertisement> result = targetingService.filterByTargetingCriteria(advertisements, countryCode, userBioData, mood);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void filterByGeoTargeting_withValidLocation_shouldReturnFilteredAds() {
        // Given
        String countryCode = "US";
        String region = "NY";
        String city = "New York";
        Double latitude = 40.7128;
        Double longitude = -74.0060;

        when(geoTargetingStrategy.matches(advertisement, countryCode, region, city, latitude, longitude))
                .thenReturn(true);

        // When
        List<Advertisement> result = targetingService.filterByGeoTargeting(advertisements, countryCode, region, city, latitude, longitude);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).contains(advertisement);
    }

    @Test
    void filterByGeoTargeting_withInvalidLocation_shouldReturnEmptyList() {
        // Given
        String countryCode = "FR";
        String region = "IDF";
        String city = "Paris";
        Double latitude = 48.8566;
        Double longitude = 2.3522;

        when(geoTargetingStrategy.matches(any(), any(), any(), any(), any(), any()))
                .thenReturn(false);

        // When
        List<Advertisement> result = targetingService.filterByGeoTargeting(advertisements, countryCode, region, city, latitude, longitude);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void filterByBioTargeting_withValidBioData_shouldReturnFilteredAds() {
        // Given
        Integer age = 25;
        String gender = "MALE";
        String occupation = "engineer";
        String educationLevel = "bachelor";
        String language = "en";
        Set<String> interests = Set.of("technology");

        Map<String, Object> bioData = Map.of(
                "age", age,
                "gender", gender,
                "occupation", occupation,
                "educationLevel", educationLevel,
                "language", language,
                "interests", interests
        );

        when(bioTargetingStrategy.matches(advertisement, bioData))
                .thenReturn(true);

        // When
        List<Advertisement> result = targetingService.filterByBioTargeting(advertisements, age, gender, occupation, educationLevel, language, interests);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).contains(advertisement);
    }

    @Test
    void filterByBioTargeting_withInvalidBioData_shouldReturnEmptyList() {
        // Given
        Integer age = 17;
        String gender = "OTHER";
        String occupation = "student";
        String educationLevel = "highschool";
        String language = "fr";
        Set<String> interests = Set.of("gaming");

        when(bioTargetingStrategy.matches(any(), any()))
                .thenReturn(false);

        // When
        List<Advertisement> result = targetingService.filterByBioTargeting(advertisements, age, gender, occupation, educationLevel, language, interests);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void filterByMoodTargeting_withValidMoodData_shouldReturnFilteredAds() {
        // Given
        Mood mood = Mood.HAPPY;
        Integer intensity = 8;
        String timeOfDay = "morning";
        String dayOfWeek = "monday";
        String season = "spring";

        when(moodTargetingStrategy.matches(advertisement, mood, intensity, timeOfDay, dayOfWeek, season))
                .thenReturn(true);

        // When
        List<Advertisement> result = targetingService.filterByMoodTargeting(advertisements, mood, intensity, timeOfDay, dayOfWeek, season);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).contains(advertisement);
    }

    @Test
    void filterByMoodTargeting_withInvalidMoodData_shouldReturnEmptyList() {
        // Given
        Mood mood = Mood.SAD;
        Integer intensity = 2;
        String timeOfDay = "night";
        String dayOfWeek = "sunday";
        String season = "winter";

        when(moodTargetingStrategy.matches(any(), any(), any(), any(), any(), any()))
                .thenReturn(false);

        // When
        List<Advertisement> result = targetingService.filterByMoodTargeting(advertisements, mood, intensity, timeOfDay, dayOfWeek, season);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void matchesTargeting_withAllMatchingCriteria_shouldReturnTrue() {
        // Given
        String countryCode = "US";
        Integer age = 25;
        String gender = "MALE";
        String occupation = "engineer";
        String educationLevel = "bachelor";
        String language = "en";
        Set<String> interests = Set.of("technology");
        Mood mood = Mood.HAPPY;
        Integer intensity = 8;
        String timeOfDay = "morning";
        String dayOfWeek = "monday";
        String season = "spring";

        when(geoTargetingStrategy.matches(advertisement, countryCode, null, null, null, null))
                .thenReturn(true);
        when(bioTargetingStrategy.matches(eq(advertisement), any(Map.class)))
                .thenReturn(true);
        when(moodTargetingStrategy.matches(advertisement, mood, intensity, timeOfDay, dayOfWeek, season))
                .thenReturn(true);

        // When
        boolean result = targetingService.matchesTargeting(advertisement, countryCode, age, gender, occupation, educationLevel, language, interests, mood, intensity, timeOfDay, dayOfWeek, season);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void matchesTargeting_withNonMatchingGeo_shouldReturnFalse() {
        // Given
        String countryCode = "FR";
        Integer age = 25;
        String gender = "MALE";
        String occupation = "engineer";
        String educationLevel = "bachelor";
        String language = "en";
        Set<String> interests = Set.of("technology");
        Mood mood = Mood.HAPPY;
        Integer intensity = 8;
        String timeOfDay = "morning";
        String dayOfWeek = "monday";
        String season = "spring";

        when(geoTargetingStrategy.matches(advertisement, countryCode, null, null, null, null))
                .thenReturn(false);

        // When
        boolean result = targetingService.matchesTargeting(advertisement, countryCode, age, gender, occupation, educationLevel, language, interests, mood, intensity, timeOfDay, dayOfWeek, season);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void matchesGeoTargeting_withValidGeoData_shouldReturnTrue() {
        // Given
        String countryCode = "US";
        String region = "NY";
        String city = "New York";
        Double latitude = 40.7128;
        Double longitude = -74.0060;

        when(geoTargetingStrategy.matches(advertisement, countryCode, region, city, latitude, longitude))
                .thenReturn(true);

        // When
        boolean result = targetingService.matchesGeoTargeting(advertisement, countryCode, region, city, latitude, longitude);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void matchesBioTargeting_withValidBioData_shouldReturnTrue() {
        // Given
        Integer age = 25;
        String genderStr = "MALE";
        String occupation = "engineer";
        String educationLevel = "bachelor";
        String language = "en";
        Set<String> interests = Set.of("technology");

        when(bioTargetingStrategy.matches(eq(advertisement), any(Map.class)))
                .thenReturn(true);

        // When
        boolean result = targetingService.matchesBioTargeting(advertisement, age, genderStr, occupation, educationLevel, language, interests);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void matchesMoodTargeting_withValidMoodData_shouldReturnTrue() {
        // Given
        Mood mood = Mood.HAPPY;
        Integer intensity = 8;
        String timeOfDay = "morning";
        String dayOfWeek = "monday";
        String season = "spring";

        when(moodTargetingStrategy.matches(advertisement, mood, intensity, timeOfDay, dayOfWeek, season))
                .thenReturn(true);

        // When
        boolean result = targetingService.matchesMoodTargeting(advertisement, mood, intensity, timeOfDay, dayOfWeek, season);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void filterByTargetingCriteria_withNullInputs_shouldHandleGracefully() {
        // Given
        List<Advertisement> nullList = null;
        String countryCode = "US";
        Map<String, Object> userBioData = new HashMap<>();
        Mood mood = Mood.HAPPY;

        // When
        List<Advertisement> result = targetingService.filterByTargetingCriteria(nullList, countryCode, userBioData, mood);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void filterByTargetingCriteria_withEmptyBioData_shouldStillFilter() {
        // Given
        String countryCode = "US";
        Map<String, Object> emptyBioData = new HashMap<>();
        Mood mood = Mood.HAPPY;

        when(geoTargetingStrategy.matches(eq(advertisement), eq(countryCode), any(), any(), any(), any()))
                .thenReturn(true);
        when(bioTargetingStrategy.matches(eq(advertisement), any(Map.class)))
                .thenReturn(true);
        when(moodTargetingStrategy.matches(eq(advertisement), eq(mood), any(), any(), any(), any()))
                .thenReturn(true);

        // When
        List<Advertisement> result = targetingService.filterByTargetingCriteria(advertisements, countryCode, emptyBioData, mood);

        // Then
        assertThat(result).hasSize(1);
    }
}