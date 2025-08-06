package com.example.adplatform.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain value object representing geolocation targeting criteria for advertisements.
 * This allows advertisements to be targeted based on country, region, city, etc.
 * This is a persistence-ignorant domain class following DDD principles.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoTarget {

    private String countryCode; // ISO country code (e.g., "US", "CA", "UK")
    private String region; // State, province, etc.
    private String city;
    private Double latitude;
    private Double longitude;
    private Integer radiusKm; // Radius in kilometers for proximity targeting
    private boolean include; // True for inclusion, false for exclusion

    /**
     * Check if this geo target matches the given location criteria.
     *
     * @param countryCode The country code to check
     * @param region The region to check
     * @param city The city to check
     * @param latitude The latitude to check
     * @param longitude The longitude to check
     * @return True if the location matches this target
     */
    public boolean matches(
            String countryCode,
            String region,
            String city,
            Double latitude,
            Double longitude
    ) {
        // Country code match
        if (countryCode != null && this.countryCode != null) {
            if (!this.countryCode.equalsIgnoreCase(countryCode)) {
                return false;
            }
        }

        // Region match (if specified)
        if (region != null && this.region != null) {
            if (!this.region.equalsIgnoreCase(region)) {
                return false;
            }
        }

        // City match (if specified)
        if (city != null && this.city != null) {
            if (!this.city.equalsIgnoreCase(city)) {
                return false;
            }
        }

        // Proximity match (if coordinates and radius are specified)
        if (latitude != null && longitude != null && 
            this.latitude != null && this.longitude != null && 
            this.radiusKm != null) {
            double distance = calculateDistance(
                    latitude, longitude, 
                    this.latitude, this.longitude);
            if (distance > this.radiusKm) {
                return false;
            }
        }

        return true;
    }

    /**
     * Calculate the distance between two points in kilometers using the Haversine formula.
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}