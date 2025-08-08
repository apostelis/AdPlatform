package com.example.adplatform.testutil;

import com.example.adplatform.domain.model.Advertisement;
import com.example.adplatform.domain.model.AdvertisementSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Test data builder utilities for Advertisement domain objects.
 * This class centralizes creation of commonly used Advertisement fixtures
 * to keep tests concise and consistent.
 */
public final class AdvertisementTestData {

    private AdvertisementTestData() {
        // utility
    }

    /**
     * Creates a default valid Advertisement instance.
     */
    public static Advertisement defaultAd() {
        LocalDateTime now = LocalDateTime.now();
        return Advertisement.builder()
                .id(1L)
                .title("Test Advertisement")
                .description("Test Description")
                .content("Test Content")
                .source(AdvertisementSource.STORAGE)
                .sourceIdentifier("test.mp4")
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Creates a second valid Advertisement instance with different values.
     */
    public static Advertisement anotherAd() {
        LocalDateTime now = LocalDateTime.now();
        return Advertisement.builder()
                .id(2L)
                .title("Another Ad")
                .description("Another Description")
                .content("Another Content")
                .source(AdvertisementSource.YOUTUBE)
                .sourceIdentifier("youtube123")
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Creates a list with the two default ads.
     */
    public static List<Advertisement> defaultAds() {
        List<Advertisement> ads = new ArrayList<>();
        ads.add(defaultAd());
        ads.add(anotherAd());
        return ads;
    }
}
