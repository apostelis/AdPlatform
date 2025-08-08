# Code Style and Static Analysis

This project uses Checkstyle to enforce a consistent Java code style across the backend. The configuration is located at:
- config/checkstyle/checkstyle.xml

## How to run
- Full build with tests and style checks:
  - mvn clean verify
- Run only Checkstyle:
  - mvn -q checkstyle:check

By default, the build does not fail on style violations (to ease adoption). Use the `ci` Maven profile to fail the build on violations:
- mvn -Pci -q verify

## Scope
- Applies to main and test sources.
- Rules focus on basic formatting, imports, naming, and braces.
- Javadoc checks are currently lenient.

## Tooling and versions
- Java: 21
- Spring Boot: 3.x (latest compatible)
- JUnit: 5
- Checkstyle plugin: 3.3.1 (Checkstyle 10.17.0)

## Recent update
- Removed deprecated property `violateExecutionOnNonTightHtml` from Javadoc modules to ensure compatibility with Checkstyle 10.17.0.

## Rationale
- Aligns with our DDD/Hexagonal practices by providing consistent style without blocking development. We will gradually tighten rules over time.
