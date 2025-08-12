# Documentation Index

Date: 2025-08-12

Welcome to the Advertisement Platform documentation. This index consolidates the most relevant docs and provides a single entry point.

Guiding principles and stack:
- Java 21+
- Spring Boot 3.x (latest compatible)
- React + Next.js 15
- JUnit 5
- DDD and Hexagonal (Ports & Adapters)

## 1. Architecture
- ADR: Architecture overview — docs/adr/0001-architecture-overview.md
- Targeting algorithms and business rules — docs/targeting-algorithms.md
- Events (tracking views/interactions, ports and adapters) — docs/events.md

## 2. Backend
- API Documentation (OpenAPI/Swagger) — docs/api-documentation.md
- Database schema (Liquibase) — docs/database-schema.md
- Error handling (frontend boundary exists; backend uses Spring exception handling) — docs/error-handling.md

## 3. Frontend
- User guide — docs/frontend-user-guide.md
- State management — docs/frontend-state.md
- Internationalization (i18n) — docs/i18n.md
- Next.js configuration notes — docs/nextjs-config.md

## 4. Quality and Standards
- Code style and static analysis (Checkstyle) — docs/code-style.md
  - Note: docs/checkstyle.md redirects here.
- Test coverage configuration (JaCoCo, Maven) — docs/coverage.md
- Testing coverage improvements and how-tos — docs/testing-coverage.md
- Frontend testing (Jest + RTL) — docs/testing-frontend.md

## 5. Deployment
- Deployment process and requirements — docs/deployment.md

## 6. Contribution
- Contributing guidelines — docs/CONTRIBUTING.md
- Task backlog (improvements) — docs/tasks.md

## Notes on consolidation
- YouTube details are documented centrally within API docs; see section "YouTube-specific details (Advertisements)" in docs/api-documentation.md. The stub docs/api-youtube-details.md links there.
- Checkstyle details are consolidated under docs/code-style.md (docs/checkstyle.md is a short redirect).

## Running tests (quick reference)
- Backend (Maven + JUnit 5):
  - mvn -q -DskipITs test
  - Coverage report via mvn clean verify → target/site/jacoco/index.html
- Frontend (Jest + RTL):
  - cd frontend && npm install && npm test
  - Coverage: npm test -- --coverage

## DDD and Hexagonal practices
- Domain and application layers are framework-agnostic; infrastructure contains web/persistence adapters.
- Use ports (interfaces) in the domain/application; implement adapters in infrastructure.
- ArchUnit tests enforce layering boundaries.
