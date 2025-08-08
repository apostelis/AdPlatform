package com.example.adplatform.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) configuration and metadata for the Advertisement Platform.
 * This configuration is an adapter configuration in the hexagonal architecture,
 * describing the web API surface of the application.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Advertisement Platform API",
                version = "v1",
                description = "REST API for managing advertisements, including targeting and analytics endpoints.",
                contact = @Contact(name = "Ad Platform Team", email = "support@example.com"),
                license = @License(name = "Apache-2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")
        )
)
public class ApiDocsConfig {
    // No explicit beans required for basic springdoc setup.
}
