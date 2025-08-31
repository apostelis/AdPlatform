package com.example.adplatform.infrastructure.web.validation;

import com.example.adplatform.infrastructure.web.dto.AdvertisementDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for the ValidClickableAdvertisement constraint.
 * Validates that clickable advertisements have a valid target URL.
 */
public class ClickableAdvertisementValidator implements ConstraintValidator<ValidClickableAdvertisement, AdvertisementDTO> {

    @Override
    public void initialize(ValidClickableAdvertisement constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(AdvertisementDTO advertisement, ConstraintValidatorContext context) {
        if (advertisement == null) {
            return true; // Let @NotNull handle null validation
        }

        // If advertisement is clickable, it must have a target URL
        if (advertisement.isClickable()) {
            String targetUrl = advertisement.getTargetUrl();
            
            if (targetUrl == null || targetUrl.trim().isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    "Target URL is required for clickable advertisements")
                    .addPropertyNode("targetUrl")
                    .addConstraintViolation();
                return false;
            }
            
            // Additional URL validation - must be HTTP or HTTPS
            if (!targetUrl.toLowerCase().startsWith("http://") && 
                !targetUrl.toLowerCase().startsWith("https://")) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    "Target URL must start with http:// or https://")
                    .addPropertyNode("targetUrl")
                    .addConstraintViolation();
                return false;
            }
        }
        
        return true;
    }
}