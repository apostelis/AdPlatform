# Testing Coverage Improvements

Date: 2025-08-12

Summary of changes to increase testing code coverage:

- Backend (JUnit 5): Added focused unit tests for the infrastructure mapper:
  - `AdvertisementMapperTest` covers mapping between domain and DTO for Advertisement, GeoTarget, BioTarget, MoodTarget, and YouTubeDetails, including null/optional branches and nested collections.
- Frontend (React/Next.js with Jest + React Testing Library): Added tests for two critical UI components:
  - `AdvertisementCard.test.tsx`: renders core fields, toggles details dialog, exercises YouTube preview branch, and verifies onEdit/onDelete callbacks.
  - `AdvertisementForm.test.tsx`: validates required fields errors and a successful submission flow with mocked service calls.

How to run tests:

- Backend (Java 17+, Spring Boot, JUnit 5):
  - Using Maven: `mvn -q -DskipITs test`
  - Using IDE: Run tests under `src/test/java` (e.g., `AdvertisementMapperTest`).

- Frontend (Node.js + Jest):
  - From `frontend` directory: `npm install` (first time) then `npm test`
  - To see frontend coverage: `npm test -- --coverage`

Notes:
- Project follows DDD and hexagonal architecture. The new backend test targets the infrastructure mapper to ensure boundaries are well-covered without requiring Spring context.
- JaCoCo can be used to measure backend coverage (already configured in build). For frontend, Jest provides coverage via `npm test -- --coverage`.

FAQ:
- Q: What is the Java testing code coverage reported by Jest?
  A: Not applicable. Jest is a JavaScript/TypeScript test runner and does not measure Java coverage. Java coverage is reported by JaCoCo (via Maven/Gradle). Therefore, "Java testing coverage reported by Jest" is N/A.
