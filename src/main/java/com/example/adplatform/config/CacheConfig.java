package com.example.adplatform.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Configuration class for caching in the application.
 * Uses Caffeine as the caching provider.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Cache names used throughout the application.
     * These constants should be used when applying caching annotations.
     */
    public static final String CACHE_ALL_ADVERTISEMENTS = "allAdvertisements";
    public static final String CACHE_ACTIVE_ADVERTISEMENTS = "activeAdvertisements";
    public static final String CACHE_ADVERTISEMENT_BY_ID = "advertisementById";
    public static final String CACHE_ADVERTISEMENTS_BY_SOURCE = "advertisementsBySource";
    public static final String CACHE_ADVERTISEMENTS_BY_TITLE = "advertisementsByTitle";
    public static final String CACHE_TARGETED_ADVERTISEMENTS = "targetedAdvertisements";
    public static final String CACHE_GEO_TARGETED_ADVERTISEMENTS = "geoTargetedAdvertisements";
    public static final String CACHE_BIO_TARGETED_ADVERTISEMENTS = "bioTargetedAdvertisements";
    public static final String CACHE_MOOD_TARGETED_ADVERTISEMENTS = "moodTargetedAdvertisements";

    /**
     * Configures the Caffeine cache manager with appropriate settings.
     * 
     * @return the configured cache manager
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Set default cache specification
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000));
        
        // Add all cache names
        cacheManager.setCacheNames(Arrays.asList(
                CACHE_ALL_ADVERTISEMENTS,
                CACHE_ACTIVE_ADVERTISEMENTS,
                CACHE_ADVERTISEMENT_BY_ID,
                CACHE_ADVERTISEMENTS_BY_SOURCE,
                CACHE_ADVERTISEMENTS_BY_TITLE,
                CACHE_TARGETED_ADVERTISEMENTS,
                CACHE_GEO_TARGETED_ADVERTISEMENTS,
                CACHE_BIO_TARGETED_ADVERTISEMENTS,
                CACHE_MOOD_TARGETED_ADVERTISEMENTS
        ));
        
        return cacheManager;
    }
}