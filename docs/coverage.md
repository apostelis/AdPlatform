# Test Coverage Configuration

This project uses JaCoCo to compute unit test coverage as part of the Maven build. To ensure coverage reflects business and domain logic rather than framework boilerplate, we exclude certain Spring Boot framework files from the coverage report.

## Rationale
Some classes are either:
- Bootstrapping/entry-point classes (e.g., the Spring Boot `*Application` class), or
- Framework configuration scaffolding (e.g., `@Configuration` classes in the `config` package),

and therefore provide little value when counted in code coverage metrics. Excluding them helps produce more meaningful coverage numbers.

## What is excluded
The following patterns are excluded from JaCoCo reports:

- `**/*Application.class` — Spring Boot application entry points
- `**/config/**` — configuration classes (e.g., Spring `@Configuration`, Web, Cache, API docs configs)
- `**/*Configuration.class` — meta configuration classes

These patterns aim to capture boilerplate framework code only. Domain, application services, repositories, controllers, and other functional components remain included in coverage.

If you add additional framework-only scaffolding that should be excluded, consider placing it under a `config` package or propose an update to the exclusion list.

## How to run tests and generate coverage
With Maven installed:

```
mvn clean verify
```

- The `jacoco-maven-plugin` attaches the Java agent during tests and generates a coverage report at:
  - `target/site/jacoco/index.html` (HTML report)
  - `target/jacoco.exec` (binary execution data)

## Tooling versions
- Java: 21
- Spring Boot: 3.1.5
- JUnit: 5.x
- Surefire plugin: 3.2.5
- JaCoCo plugin: 0.8.12

## Notes
- We follow DDD and hexagonal architecture practices; coverage should primarily reflect domain and application layers.
- Integration tests can be included in the same lifecycle as needed; adjust surefire/failsafe configuration accordingly if you introduce integration tests.
