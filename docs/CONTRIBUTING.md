# Contribution Guidelines

Thank you for considering contributing to the Advertisement Platform!

## Tech Stack and Versions
- Java: 21+
- Spring Boot: 3.x (latest compatible)
- JUnit: 5
- Frontend: React + Next.js

## Architecture Principles
We follow Domain-Driven Design (DDD) and Hexagonal (Ports & Adapters) architecture:
- Domain and Application layers must not depend on frameworks or infrastructure.
- Controllers depend on application ports only.
- Adapters implement output ports (e.g., persistence, messaging, cache).
- Use configuration to wire adapters to ports.

## Development Workflow
1. Create a feature branch from `main`.
2. Implement changes with small, focused commits.
3. Add/Update tests (unit/integration). Target 80%+ service coverage.
4. Run the test suite locally.
5. Ensure static analysis passes (Checkstyle). See docs/code-style.md.
6. Open a PR with a clear description and link to related tasks.

## Code Style
- Use meaningful names and small methods.
- Prefer immutability and defensive programming for public APIs.
- Add JavaDoc to all public classes and methods.
- Keep controllers thin; move logic to application services.
- Follow project Checkstyle rules; see docs/code-style.md.

## Testing
- Unit tests for domain and application services (no infrastructure).
- Integration tests for adapters (repositories, messaging, configuration).
- Use test data builders/fixtures for readability.
- Use ArchUnit tests to enforce architectural boundaries.

## Commit Messages
- Use conventional style where possible:
  - feat: new feature
  - fix: bug fix
  - docs: documentation only changes
  - test: adding or refactoring tests
  - refactor: changes that neither fix a bug nor add a feature
  - chore: maintenance tasks

## Pull Request Checklist
- [ ] Tests added/updated and passing
- [ ] README/docs updated when applicable
- [ ] Follows DDD/Hexagonal guidelines
- [ ] No direct framework dependencies in domain/application layers

## Running Locally
- Backend: `./mvnw spring-boot:run`
- Frontend: `cd frontend && npm install && npm run dev`
- Docker Compose: `docker-compose up -d`

## Contact
Open an issue for questions or discussions.
