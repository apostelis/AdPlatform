package com.example.adplatform.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for BioTarget.
 * This is used for communication between the controller and the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BioTargetDTO {

    private Integer minAge;
    private Integer maxAge;
    private String gender;
    private String occupation;
    private String educationLevel;
    private String language;
    private String interestCategory;
    private boolean include;
}