package com.example.adplatform.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embeddable JPA entity representing geolocation targeting criteria for advertisements.
 * This is an infrastructure entity in the hexagonal architecture.
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoTargetJpaEntity {

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