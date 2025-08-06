package com.example.adplatform.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for MoodTarget.
 * This is used for communication between the controller and the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoodTargetDTO {

    private String mood;
    private Integer intensityMin;
    private Integer intensityMax;
    private String timeOfDay;
    private String dayOfWeek;
    private String season;
    private boolean include;
}