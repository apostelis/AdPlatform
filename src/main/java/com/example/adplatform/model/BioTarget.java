package com.example.adplatform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embeddable class representing biographical targeting criteria for advertisements.
 * This allows advertisements to be targeted based on age, gender, education, etc.
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BioTarget {

    private Integer minAge;
    
    private Integer maxAge;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    private String occupation;
    
    private String educationLevel;
    
    private String language;
    
    private String interestCategory;
    
    @Column(nullable = false)
    private boolean include; // True for inclusion, false for exclusion
    
    /**
     * Enum representing gender options for targeting.
     */
    public enum Gender {
        MALE,
        FEMALE,
        OTHER,
        ALL
    }
}