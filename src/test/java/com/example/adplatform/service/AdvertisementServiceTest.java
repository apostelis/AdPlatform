package com.example.adplatform.service;

import com.example.adplatform.application.port.in.AdvertisementService;
import com.example.adplatform.application.port.in.TargetingService;
import com.example.adplatform.application.port.out.AdvertisementRepository;
import com.example.adplatform.application.service.AdvertisementServiceImpl;
import com.example.adplatform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdvertisementServiceTest {

    @Mock
    private AdvertisementRepository advertisementRepository;
    
    @Mock
    private TargetingService targetingService;

    private AdvertisementService advertisementService;
    
    @BeforeEach
    void initService() {
        advertisementService = new AdvertisementServiceImpl(advertisementRepository, targetingService);
    }

    private Advertisement testAd;
    private Advertisement geoTargetedAd;
    private Advertisement bioTargetedAd;
    private Advertisement moodTargetedAd;

    @BeforeEach
    void setUp() {
        // Create a basic test advertisement
        testAd = Advertisement.builder()
                .id(1L)
                .title("Test Advertisement")
                .description("Test Description")
                .content("Test Content")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("test.mp4")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .geoTargets(new HashSet<>())
                .bioTargets(new HashSet<>())
                .moodTargets(new HashSet<>())
                .build();

        // Create a geo-targeted advertisement
        geoTargetedAd = Advertisement.builder()
                .id(2L)
                .title("Geo Targeted Ad")
                .description("Advertisement targeted by location")
                .content("Geo Content")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("geo.mp4")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Set<GeoTarget> geoTargets = new HashSet<>();
        geoTargets.add(GeoTarget.builder()
                .countryCode("US")
                .region("California")
                .city("San Francisco")
                .include(true)
                .build());
        geoTargets.add(GeoTarget.builder()
                .countryCode("CA")
                .include(true)
                .build());
        geoTargetedAd.setGeoTargets(geoTargets);

        // Create a bio-targeted advertisement
        bioTargetedAd = Advertisement.builder()
                .id(3L)
                .title("Bio Targeted Ad")
                .description("Advertisement targeted by demographics")
                .content("Bio Content")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("bio.mp4")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Set<BioTarget> bioTargets = new HashSet<>();
        bioTargets.add(BioTarget.builder()
                .minAge(25)
                .maxAge(45)
                .gender(Gender.MALE)
                .occupation("Engineer")
                .include(true)
                .build());
        bioTargets.add(BioTarget.builder()
                .language("English")
                .interestCategory("Technology")
                .include(true)
                .build());
        bioTargetedAd.setBioTargets(bioTargets);

        // Create a mood-targeted advertisement
        moodTargetedAd = Advertisement.builder()
                .id(4L)
                .title("Mood Targeted Ad")
                .description("Advertisement targeted by mood")
                .content("Mood Content")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("mood.mp4")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Set<MoodTarget> moodTargets = new HashSet<>();
        moodTargets.add(MoodTarget.builder()
                .mood(Mood.HAPPY)
                .intensityMin(5)
                .intensityMax(10)
                .timeOfDay("Morning")
                .include(true)
                .build());
        moodTargets.add(MoodTarget.builder()
                .mood(Mood.RELAXED)
                .dayOfWeek("Weekend")
                .include(true)
                .build());
        moodTargetedAd.setMoodTargets(moodTargets);
    }

    @Test
    void getAllAdvertisements_ShouldReturnAllAds() {
        // Arrange
        List<Advertisement> expectedAds = Arrays.asList(testAd, geoTargetedAd, bioTargetedAd, moodTargetedAd);
        when(advertisementRepository.findAll()).thenReturn(expectedAds);

        // Act
        List<Advertisement> actualAds = advertisementService.getAllAdvertisements();

        // Assert
        assertEquals(expectedAds.size(), actualAds.size());
        assertEquals(expectedAds, actualAds);
    }

    @Test
    void getActiveAdvertisements_ShouldReturnOnlyActiveAds() {
        // Arrange
        List<Advertisement> activeAds = Arrays.asList(testAd, geoTargetedAd, bioTargetedAd);
        when(advertisementRepository.findByActiveTrue()).thenReturn(activeAds);

        // Act
        List<Advertisement> result = advertisementService.getActiveAdvertisements();

        // Assert
        assertEquals(activeAds.size(), result.size());
        assertEquals(activeAds, result);
    }

    @Test
    void getGeoTargetedAdvertisements_ShouldReturnMatchingAds() {
        // Arrange
        List<Advertisement> activeAds = Arrays.asList(testAd, geoTargetedAd, bioTargetedAd, moodTargetedAd);
        List<Advertisement> expectedResult = Collections.singletonList(geoTargetedAd);
        
        when(advertisementRepository.findByActiveTrue()).thenReturn(activeAds);
        when(targetingService.filterByGeoTargeting(
                eq(activeAds), 
                eq("US"), 
                eq("California"), 
                eq("San Francisco"), 
                isNull(), 
                isNull()
        )).thenReturn(expectedResult);

        // Act
        List<Advertisement> result = advertisementService.getGeoTargetedAdvertisements("US", "California", "San Francisco", null, null);

        // Assert
        assertEquals(expectedResult, result);
        
        // Verify targeting service was called with correct parameters
        verify(targetingService).filterByGeoTargeting(
                eq(activeAds), 
                eq("US"), 
                eq("California"), 
                eq("San Francisco"), 
                isNull(), 
                isNull()
        );
    }

    @Test
    void getBioTargetedAdvertisements_ShouldReturnMatchingAds() {
        // Arrange
        List<Advertisement> activeAds = Arrays.asList(testAd, geoTargetedAd, bioTargetedAd, moodTargetedAd);
        List<Advertisement> expectedResult = Collections.singletonList(bioTargetedAd);
        
        when(advertisementRepository.findByActiveTrue()).thenReturn(activeAds);
        when(targetingService.filterByBioTargeting(
                eq(activeAds), 
                eq(30), 
                eq("MALE"), 
                eq("Engineer"), 
                isNull(), 
                isNull(),
                isNull()
        )).thenReturn(expectedResult);

        // Act
        List<Advertisement> result = advertisementService.getBioTargetedAdvertisements(30, "MALE", "Engineer", null, null, null);

        // Assert
        assertEquals(expectedResult, result);
        
        // Verify targeting service was called with correct parameters
        verify(targetingService).filterByBioTargeting(
                eq(activeAds), 
                eq(30), 
                eq("MALE"), 
                eq("Engineer"), 
                isNull(), 
                isNull(),
                isNull()
        );
    }

    @Test
    void getMoodTargetedAdvertisements_ShouldReturnMatchingAds() {
        // Arrange
        List<Advertisement> activeAds = Arrays.asList(testAd, geoTargetedAd, bioTargetedAd, moodTargetedAd);
        List<Advertisement> expectedResult = Collections.singletonList(moodTargetedAd);
        
        when(advertisementRepository.findByActiveTrue()).thenReturn(activeAds);
        when(targetingService.filterByMoodTargeting(
                eq(activeAds), 
                eq(Mood.HAPPY), 
                eq(7), 
                eq("Morning"), 
                isNull(), 
                isNull()
        )).thenReturn(expectedResult);

        // Act
        List<Advertisement> result = advertisementService.getMoodTargetedAdvertisements(Mood.HAPPY, 7, "Morning", null, null);

        // Assert
        assertEquals(expectedResult, result);
        
        // Verify targeting service was called with correct parameters
        verify(targetingService).filterByMoodTargeting(
                eq(activeAds), 
                eq(Mood.HAPPY), 
                eq(7), 
                eq("Morning"), 
                isNull(), 
                isNull()
        );
    }

    @Test
    void getTargetedAdvertisements_ShouldCombineTargeting() {
        // Arrange
        List<Advertisement> activeAds = Arrays.asList(testAd, geoTargetedAd, bioTargetedAd, moodTargetedAd);
        List<Advertisement> expectedResult = Collections.singletonList(bioTargetedAd);
        
        Map<String, Object> userBioData = new HashMap<>();
        userBioData.put("age", 30);
        userBioData.put("gender", "MALE");
        userBioData.put("occupation", "Engineer");
        
        when(advertisementRepository.findByActiveTrue()).thenReturn(activeAds);
        when(targetingService.filterByTargetingCriteria(
                eq(activeAds), 
                eq("US"), 
                eq(userBioData), 
                eq(Mood.HAPPY)
        )).thenReturn(expectedResult);

        // Act
        List<Advertisement> result = advertisementService.getTargetedAdvertisements("US", userBioData, Mood.HAPPY);

        // Assert
        assertEquals(expectedResult, result);
        
        // Verify targeting service was called with correct parameters
        verify(targetingService).filterByTargetingCriteria(
                eq(activeAds), 
                eq("US"), 
                eq(userBioData), 
                eq(Mood.HAPPY)
        );
    }

    @Test
    void saveAdvertisement_ShouldReturnSavedAd() {
        // Arrange
        when(advertisementRepository.save(any(Advertisement.class))).thenReturn(testAd);

        // Act
        Advertisement result = advertisementService.saveAdvertisement(testAd);

        // Assert
        assertEquals(testAd, result);
    }

    @Test
    void deleteAdvertisement_ShouldCallRepository() {
        // Arrange
        when(advertisementRepository.findById(1L)).thenReturn(Optional.of(testAd));
        
        // Act & Assert - No exception should be thrown
        assertDoesNotThrow(() -> advertisementService.deleteAdvertisement(1L));
    }
}
