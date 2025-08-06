package com.example.adplatform.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for GeoTarget.
 * This is used for communication between the controller and the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoTargetDTO {

    private String countryCode;
    private String region;
    private String city;
    private Double latitude;
    private Double longitude;
    private Integer radiusKm;
    private boolean include;
}