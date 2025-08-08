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
- Rate limiting: API endpoints under `/api/**` are rate limited to 100 requests per minute per client IP. Exceeding the limit returns HTTP 429.
- Event tracking endpoints (v1): POST `/api/v1/advertisements/{id}/view`, POST `/api/v1/advertisements/{id}/interactions?type=CLICK`. See docs/events.md.

## YouTube-specific details (Advertisements)

When an advertisement has `source = YOUTUBE`, the payload may include an optional `youtubeDetails` object to enrich frontend rendering. Shape (excerpt):

```
{
  "id": 1,
  "title": "...",
  "source": "YOUTUBE",
  "sourceIdentifier": "<videoId>",
  "youtubeDetails": {
    "videoId": "<videoId>",
    "videoTitle": "...",
    "channelId": "...",
    "channelTitle": "...",
    "durationSeconds": 123,
    "thumbnailUrl": "https://...",
    "publishedAt": "2025-08-08T15:00:00Z"
  }
}
```

Rules:
- All fields under `youtubeDetails` are optional.
- If provided, `youtubeDetails.videoId` should match `sourceIdentifier` when `source = YOUTUBE`.
- No external API integration is required; details are accepted/stored/returned if supplied.

## Running locally

1. Build and run the backend:
   - `mvn spring-boot:run` (from the project root)
2. Open the Swagger UI in your browser: `http://localhost:8080/swagger-ui.html`

## Frontend/Backend contract

- This OpenAPI specification can be used to generate API clients for the frontend (Next.js) or for external consumers.
- Consider pinning schema versions when introducing breaking changes and leverage existing API versioning at `/api/v1/**`.

## Viewing Policy (Advertisement ordering)

The backend exposes advertisement lists ordered by a viewing policy:
- Ads with an active override window (now within [overrideStart, overrideEnd], inclusive) are returned first.
- Within each group (override-active and others), ads are ordered by:
  1) weight descending (higher weight first), then
  2) createdAt descending (newer first). Null createdAt values come last.

Notes:
- If either overrideStart or overrideEnd is null, the override is considered inactive.
- Edge times are inclusive: at start and end timestamps, the override is active.
