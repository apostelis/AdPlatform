package com.example.adplatform.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Application configuration class.
 * This ensures that Spring scans all the necessary packages for components.
 */
@Configuration
@ComponentScan(basePackages = {
        "com.example.adplatform.domain",
        "com.example.adplatform.application",
        "com.example.adplatform.infrastructure"
})
public class ApplicationConfig {
    // Configuration beans can be added here if needed
}