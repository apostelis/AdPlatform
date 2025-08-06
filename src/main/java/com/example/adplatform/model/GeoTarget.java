package com.example.adplatform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embeddable class representing geolocation targeting criteria for advertisements.
 * This allows advertisements to be targeted based on country, region, city, etc.
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoTarget {

    @Column(nullable = false)
    private String countryCode; // ISO country code (e.g., "US", "CA", "UK")
    
    private String region; // State, province, etc.
    
    private String city;
    
    private Double latitude;
    
    private Double longitude;
    
    private Integer radiusKm; // Radius in kilometers for proximity targeting
    
    @Column(nullable = false)
    private boolean include; // True for inclusion, false for exclusion
}