package com.example.adplatform.infrastructure.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation to validate clickable advertisement constraints.
 * Ensures that if an advertisement is clickable, it must have a valid target URL.
 */
@Documented
@Constraint(validatedBy = ClickableAdvertisementValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidClickableAdvertisement {
    
    String message() default "Clickable advertisements must have a valid target URL";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}