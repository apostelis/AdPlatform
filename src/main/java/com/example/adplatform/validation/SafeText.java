package com.example.adplatform.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation to ensure text content is safe and free from injection attacks.
 * Prevents XSS, SQL injection, and other malicious content.
 */
@Documented
@Constraint(validatedBy = SafeTextValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface SafeText {
    
    String message() default "Text contains potentially unsafe content";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Whether HTML tags are allowed in the text.
     * Default is false for maximum security.
     */
    boolean allowHtml() default false;
    
    /**
     * Maximum length of the text.
     * Default is 5000 characters.
     */
    int maxLength() default 5000;
}