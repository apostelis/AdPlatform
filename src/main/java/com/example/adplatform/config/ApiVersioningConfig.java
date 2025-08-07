package com.example.adplatform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for API versioning.
 * This class configures the API versioning strategy for the application.
 * 
 * The application uses URI path versioning (e.g., /api/v1/resource) as the primary versioning strategy.
 * This approach is simple, explicit, and makes it easy to route requests to the appropriate controller.
 */
@Configuration
public class ApiVersioningConfig implements WebMvcConfigurer {
    // The versioning is implemented through URI paths (/api/v1/resource)
    // This configuration class serves as documentation and can be extended
    // in the future to support additional versioning strategies.
}