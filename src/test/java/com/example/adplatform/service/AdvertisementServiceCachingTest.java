package com.example.adplatform.service;

import com.example.adplatform.application.port.in.AdvertisementService;
import com.example.adplatform.application.port.in.TargetingService;
import com.example.adplatform.application.port.out.AdvertisementRepository;
import com.example.adplatform.application.service.AdvertisementServiceImpl;
import com.example.adplatform.config.CacheConfig;
import com.example.adplatform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for verifying the caching behavior of the AdvertisementServiceImpl.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AdvertisementServiceCachingTest.TestCacheConfig.class})
public class AdvertisementServiceCachingTest {

    @Configuration
    @EnableCaching
    static class TestCacheConfig {
        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager(
                    CacheConfig.CACHE_ALL_ADVERTISEMENTS,
                    CacheConfig.CACHE_ACTIVE_ADVERTISEMENTS,
                    CacheConfig.CACHE_ADVERTISEMENT_BY_ID,
                    CacheConfig.CACHE_ADVERTISEMENTS_BY_SOURCE,
                    CacheConfig.CACHE_ADVERTISEMENTS_BY_TITLE,
                    CacheConfig.CACHE_TARGETED_ADVERTISEMENTS,
                    CacheConfig.CACHE_GEO_TARGETED_ADVERTISEMENTS,
                    CacheConfig.CACHE_BIO_TARGETED_ADVERTISEMENTS,
                    CacheConfig.CACHE_MOOD_TARGETED_ADVERTISEMENTS
            );
        }

        @Bean
        public AdvertisementService advertisementService(AdvertisementRepository repository, TargetingService targetingService) {
            return new AdvertisementServiceImpl(repository, targetingService);
        }
    }

    @MockBean
    private AdvertisementRepository advertisementRepository;
    
    @MockBean
    private TargetingService targetingService;

    @Autowired
    private AdvertisementService advertisementService;
    
    @Autowired
    private CacheManager cacheManager;

    private Advertisement testAd;

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
                .build();
    }

    @Test
    void getAllAdvertisements_ShouldUseCaching() {
        // Arrange
        List<Advertisement> expectedAds = Collections.singletonList(testAd);
        when(advertisementRepository.findAll()).thenReturn(expectedAds);

        // Act - Call the method twice
        List<Advertisement> result1 = advertisementService.getAllAdvertisements();
        List<Advertisement> result2 = advertisementService.getAllAdvertisements();

        // Assert
        assertEquals(expectedAds, result1);
        assertEquals(expectedAds, result2);
        // Verify repository method was called only once (second call used cache)
        verify(advertisementRepository, times(1)).findAll();
    }

    @Test
    void getAdvertisementById_ShouldUseCaching() {
        // Arrange
        when(advertisementRepository.findById(1L)).thenReturn(Optional.of(testAd));

        // Act - Call the method twice with the same ID
        Optional<Advertisement> result1 = advertisementService.getAdvertisementById(1L);
        Optional<Advertisement> result2 = advertisementService.getAdvertisementById(1L);

        // Assert
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(testAd, result1.get());
        assertEquals(testAd, result2.get());
        // Verify repository method was called only once (second call used cache)
        verify(advertisementRepository, times(1)).findById(1L);
    }

    @Test
    void saveAdvertisement_ShouldCallRepository() {
        // Arrange
        when(advertisementRepository.save(any(Advertisement.class))).thenReturn(testAd);

        // Act
        Advertisement result = advertisementService.saveAdvertisement(testAd);

        // Assert
        assertEquals(testAd, result);
        verify(advertisementRepository, times(1)).save(testAd);
    }

    @Test
    void deleteAdvertisement_ShouldCallRepository() {
        // Arrange
        when(advertisementRepository.findById(1L)).thenReturn(Optional.of(testAd));
        doNothing().when(advertisementRepository).deleteById(1L);

        // Act
        advertisementService.deleteAdvertisement(1L);

        // Assert
        verify(advertisementRepository, times(1)).findById(1L);
        verify(advertisementRepository, times(1)).deleteById(1L);
    }
}