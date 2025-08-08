# Code Style and Static Analysis

This project uses Checkstyle to enforce a consistent Java code style across the backend codebase. The configuration is located at:

- config/checkstyle/checkstyle.xml

## How to run

- Run during build verification:
  - mvn -q -DskipTests verify
  This will execute Checkstyle and print any warnings to the console.

- Run just Checkstyle:
  - mvn -q checkstyle:check

By default, the build does not fail on style violations (to ease adoption). We will tighten the rules and enable build failure over time.

## Scope

- Applies to main and test sources.
- Rules focus on basic formatting, imports, naming, and braces.
- Javadoc checks are lenient for now; comprehensive documentation is tracked in Task 10.

## Rationale

- Aligns with .junie/guidelines.md (Java 21, JUnit 5, DDD/hexagonal friendly) by providing consistent practices without blocking development.

## Next steps

- Gradually increase strictness.
- Turn on failOnViolation once the codebase conforms.
