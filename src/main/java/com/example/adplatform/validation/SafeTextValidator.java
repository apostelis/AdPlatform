package com.example.adplatform.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Validator implementation for SafeText annotation.
 * Provides comprehensive validation to prevent injection attacks including XSS, SQL injection,
 * and other malicious content while allowing legitimate text content.
 */
@Component
public class SafeTextValidator implements ConstraintValidator<SafeText, String> {

    // Patterns for detecting potential security threats
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i).*(\\b(SELECT|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|EXEC|UNION|SCRIPT|JAVASCRIPT|VBSCRIPT)\\b|--|/\\*|\\*/|;|'|\")", 
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(?i).*(\\b(script|javascript|vbscript|onload|onerror|onclick|onmouseover|onfocus|onblur)\\b|<|>|&lt;|&gt;)", 
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile(
        "<[^>]+>", 
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern COMMAND_INJECTION_PATTERN = Pattern.compile(
        "(?i).*(\\b(cmd|powershell|bash|sh|exec|eval|system)\\b|\\||&|;|`|\\$\\(|\\$\\{)", 
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern LDAP_INJECTION_PATTERN = Pattern.compile(
        "(?i).*[\\(\\)\\*\\\\\\|&]", 
        Pattern.CASE_INSENSITIVE
    );

    private boolean allowHtml;
    private int maxLength;

    @Override
    public void initialize(SafeText constraintAnnotation) {
        this.allowHtml = constraintAnnotation.allowHtml();
        this.maxLength = constraintAnnotation.maxLength();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null values are handled by @NotNull annotation
        if (value == null) {
            return true;
        }
        
        // Check length constraint
        if (value.length() > maxLength) {
            addViolation(context, "Text exceeds maximum length of " + maxLength + " characters");
            return false;
        }
        
        // Normalize whitespace and trim
        String normalizedValue = value.trim().replaceAll("\\s+", " ");
        
        // Check for SQL injection attempts
        if (SQL_INJECTION_PATTERN.matcher(normalizedValue).find()) {
            addViolation(context, "Text contains potential SQL injection patterns");
            return false;
        }
        
        // Check for XSS attempts
        if (XSS_PATTERN.matcher(normalizedValue).find()) {
            addViolation(context, "Text contains potential XSS patterns");
            return false;
        }
        
        // Check for HTML tags if not allowed
        if (!allowHtml && HTML_TAG_PATTERN.matcher(normalizedValue).find()) {
            addViolation(context, "HTML tags are not allowed in this field");
            return false;
        }
        
        // Check for command injection attempts
        if (COMMAND_INJECTION_PATTERN.matcher(normalizedValue).find()) {
            addViolation(context, "Text contains potential command injection patterns");
            return false;
        }
        
        // Check for LDAP injection attempts
        if (LDAP_INJECTION_PATTERN.matcher(normalizedValue).find()) {
            addViolation(context, "Text contains potential LDAP injection patterns");
            return false;
        }
        
        // Check for encoded/obfuscated content
        if (containsEncodedContent(normalizedValue)) {
            addViolation(context, "Text contains potentially encoded malicious content");
            return false;
        }
        
        // Check for excessive special characters (potential obfuscation)
        if (hasExcessiveSpecialCharacters(normalizedValue)) {
            addViolation(context, "Text contains excessive special characters");
            return false;
        }
        
        return true;
    }
    
    /**
     * Check for encoded content that might be used to bypass filters.
     */
    private boolean containsEncodedContent(String value) {
        // Check for URL encoding patterns
        if (value.contains("%") && Pattern.compile("%[0-9a-fA-F]{2}").matcher(value).find()) {
            return true;
        }
        
        // Check for HTML entity encoding
        if (value.contains("&") && Pattern.compile("&[a-zA-Z]+;|&#[0-9]+;|&#x[0-9a-fA-F]+;").matcher(value).find()) {
            return true;
        }
        
        // Check for Unicode escape sequences
        if (value.contains("\\u") && Pattern.compile("\\\\u[0-9a-fA-F]{4}").matcher(value).find()) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Check for excessive special characters that might indicate obfuscation attempts.
     */
    private boolean hasExcessiveSpecialCharacters(String value) {
        if (value.length() == 0) {
            return false;
        }
        
        long specialCharCount = value.chars()
            .filter(ch -> !Character.isLetterOrDigit(ch) && !Character.isWhitespace(ch))
            .count();
            
        // If more than 30% of characters are special characters, flag as suspicious
        double ratio = (double) specialCharCount / value.length();
        return ratio > 0.3;
    }
    
    /**
     * Add a custom violation message to the context.
     */
    private void addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}