package com.example.adplatform.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embeddable JPA entity representing biographical targeting criteria for advertisements.
 * This is an infrastructure entity in the hexagonal architecture.
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BioTargetJpaEntity {

    private Integer minAge;
    
    private Integer maxAge;
    
    @Enumerated(EnumType.STRING)
    private GenderJpaEnum gender;
    
    private String occupation;
    
    private String educationLevel;
    
    private String language;
    
    private String interestCategory;
    
    @Column(nullable = false)
    private boolean include; // True for inclusion, false for exclusion
    
    /**
     * Enum representing gender options for targeting.
     */
    public enum GenderJpaEnum {
        MALE,
        FEMALE,
        NON_BINARY,
        ALL
    }
}