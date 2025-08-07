package com.example.adplatform.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Domain entity representing statistics for an Advertisement.
 * This class tracks views, clicks, and other interactions with an advertisement.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementStats {
    
    @Builder.Default
    private Long viewCount = 0L;
    
    @Builder.Default
    private Long clickCount = 0L;
    
    @Builder.Default
    private Long completeViewCount = 0L;
    
    @Builder.Default
    private Long conversionCount = 0L;
    
    @Builder.Default
    private Long skipCount = 0L;
    
    @Builder.Default
    private Long shareCount = 0L;
    
    @Builder.Default
    private Long likeCount = 0L;
    
    @Builder.Default
    private Long dislikeCount = 0L;
    
    @Builder.Default
    private Double clickThroughRate = 0.0;
    
    @Builder.Default
    private Double conversionRate = 0.0;
    
    @Builder.Default
    private Map<String, Long> viewsByDevice = new HashMap<>();
    
    @Builder.Default
    private Map<String, Long> viewsByBrowser = new HashMap<>();
    
    @Builder.Default
    private Map<String, Long> viewsByCountry = new HashMap<>();
    
    private LocalDateTime lastUpdated;
    
    /**
     * Increments the view count and updates related statistics.
     */
    public void incrementViewCount() {
        viewCount++;
        updateRates();
        lastUpdated = LocalDateTime.now();
    }
    
    /**
     * Increments the complete view count and updates related statistics.
     */
    public void incrementCompleteViewCount() {
        completeViewCount++;
        viewCount++; // A complete view is also a view
        updateRates();
        lastUpdated = LocalDateTime.now();
    }
    
    /**
     * Increments the click count and updates related statistics.
     */
    public void incrementClickCount() {
        clickCount++;
        updateRates();
        lastUpdated = LocalDateTime.now();
    }
    
    /**
     * Increments the conversion count and updates related statistics.
     */
    public void incrementConversionCount() {
        conversionCount++;
        clickCount++; // A conversion is also a click
        updateRates();
        lastUpdated = LocalDateTime.now();
    }
    
    /**
     * Increments the skip count.
     */
    public void incrementSkipCount() {
        skipCount++;
        lastUpdated = LocalDateTime.now();
    }
    
    /**
     * Increments the share count.
     */
    public void incrementShareCount() {
        shareCount++;
        lastUpdated = LocalDateTime.now();
    }
    
    /**
     * Increments the like count.
     */
    public void incrementLikeCount() {
        likeCount++;
        lastUpdated = LocalDateTime.now();
    }
    
    /**
     * Increments the dislike count.
     */
    public void incrementDislikeCount() {
        dislikeCount++;
        lastUpdated = LocalDateTime.now();
    }
    
    /**
     * Increments the view count for a specific device.
     *
     * @param deviceType the type of device
     */
    public void incrementViewsByDevice(String deviceType) {
        if (deviceType != null) {
            viewsByDevice.put(deviceType, viewsByDevice.getOrDefault(deviceType, 0L) + 1);
        }
    }
    
    /**
     * Increments the view count for a specific browser.
     *
     * @param browserInfo the browser information
     */
    public void incrementViewsByBrowser(String browserInfo) {
        if (browserInfo != null) {
            viewsByBrowser.put(browserInfo, viewsByBrowser.getOrDefault(browserInfo, 0L) + 1);
        }
    }
    
    /**
     * Increments the view count for a specific country.
     *
     * @param countryCode the country code
     */
    public void incrementViewsByCountry(String countryCode) {
        if (countryCode != null) {
            viewsByCountry.put(countryCode, viewsByCountry.getOrDefault(countryCode, 0L) + 1);
        }
    }
    
    /**
     * Updates the click-through rate and conversion rate.
     */
    private void updateRates() {
        if (viewCount > 0) {
            clickThroughRate = (double) clickCount / viewCount;
            conversionRate = (double) conversionCount / viewCount;
        }
    }
}