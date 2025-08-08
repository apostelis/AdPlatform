# API Documentation

This project provides comprehensive, always-up-to-date API documentation using OpenAPI (Swagger) via springdoc-openapi.

## How to access

- Swagger UI (interactive docs):
  - http://localhost:8080/swagger-ui.html
  - Alternatively: http://localhost:8080/swagger-ui/index.html
- Raw OpenAPI JSON:
  - http://localhost:8080/v3/api-docs
- Raw OpenAPI YAML:
  - http://localhost:8080/v3/api-docs.yaml

## Notes

- The documentation is generated automatically from the Spring Web annotations and type signatures.
- API metadata (title, version, description) is configured in `src/main/java/com/example/adplatform/config/ApiDocsConfig.java` using `@OpenAPIDefinition`.
- No extra annotations are required to see basic documentation; however, you can enhance schemas and operations with `@Schema`, `@Operation`, and related annotations from `io.swagger.v3.oas.annotations`.
- Base path: The backend runs at root context (no server.servlet.context-path), so the URLs above are correct out of the box.

## Running locally

1. Build and run the backend:
   - `mvn spring-boot:run` (from the project root)
2. Open the Swagger UI in your browser: `http://localhost:8080/swagger-ui.html`

## Frontend/Backend contract

- This OpenAPI specification can be used to generate API clients for the frontend (Next.js) or for external consumers.
- Consider pinning schema versions when introducing breaking changes and leverage existing API versioning at `/api/v1/**`.
