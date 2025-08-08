# ADR 0001: Architecture Style and Hexagonal Boundaries

Date: 2025-08-08

Status: Accepted

Context
- We need a clear architecture style to support maintainability, testability, and evolution of the Advertisement Platform.
- The system involves domain logic, web APIs, persistence, and integration with external systems.

Decision
- Adopt Domain-Driven Design (DDD) and Hexagonal (Ports & Adapters) architecture.
- Organize code into domain (core), application (use cases/services), and infrastructure (adapters) layers.
- Define ports (interfaces) in domain/application; implement adapters in infrastructure (web, persistence, messaging).
- Use Spring Boot for dependency injection and configuration, ensuring domain layer stays framework-agnostic.
- Use ArchUnit tests to enforce boundaries.

Consequences
- Improves modularity and testability; domain logic can be tested without infrastructure.
- Requires discipline in dependencies and package structure; violations will be caught by ArchUnit tests.
- Facilitates future additions (e.g., alternative persistence) by adding adapters without changing domain.

References
- Hexagonal Architecture by Alistair Cockburn
- DDD by Eric Evans
