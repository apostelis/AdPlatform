package com.example.adplatform.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Domain value object representing biographical targeting criteria for advertisements.
 * This allows advertisements to be targeted based on age, gender, education, etc.
 * This is a persistence-ignorant domain class following DDD principles.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BioTarget {

    private Integer minAge;
    private Integer maxAge;
    private Gender gender;
    private String occupation;
    private String educationLevel;
    private String language;
    private String interestCategory;
    private boolean include; // True for inclusion, false for exclusion

    /**
     * Check if this bio target matches the given biographical criteria.
     *
     * @param age The age to check
     * @param gender The gender to check
     * @param occupation The occupation to check
     * @param educationLevel The education level to check
     * @param language The language to check
     * @param interests The interests to check
     * @return True if the biographical data matches this target
     */
    public boolean matches(
            Integer age,
            Gender gender,
            String occupation,
            String educationLevel,
            String language,
            Set<String> interests
    ) {
        // Age range match
        if (age != null && (minAge != null || maxAge != null)) {
            if (minAge != null && age < minAge) {
                return false;
            }
            if (maxAge != null && age > maxAge) {
                return false;
            }
        }

        // Gender match
        if (gender != null && this.gender != null) {
            if (this.gender != Gender.ALL && this.gender != gender) {
                return false;
            }
        }

        // Occupation match
        if (occupation != null && this.occupation != null) {
            if (!this.occupation.equalsIgnoreCase(occupation)) {
                return false;
            }
        }

        // Education level match
        if (educationLevel != null && this.educationLevel != null) {
            if (!this.educationLevel.equalsIgnoreCase(educationLevel)) {
                return false;
            }
        }

        // Language match
        if (language != null && this.language != null) {
            if (!this.language.equalsIgnoreCase(language)) {
                return false;
            }
        }

        // Interest category match
        if (interests != null && !interests.isEmpty() && this.interestCategory != null) {
            if (interests.stream().noneMatch(interest -> 
                    interest.equalsIgnoreCase(this.interestCategory))) {
                return false;
            }
        }

        return true;
    }
}