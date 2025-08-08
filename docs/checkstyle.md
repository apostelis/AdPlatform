# Checkstyle Configuration

This project uses the Maven Checkstyle Plugin with Checkstyle 10.17.0. The configuration file is located at `config/checkstyle/checkstyle.xml`.

## Recent Update (2025-08-08)
- Removed the deprecated property `violateExecutionOnNonTightHtml` from `JavadocMethod` and `JavadocType` modules.
- This property was removed in newer Checkstyle versions and caused the following error during the `verify` phase:
  - `cannot initialize module JavadocMethod - Property 'violateExecutionOnNonTightHtml' does not exist`
- The configuration is now compatible with Checkstyle 10.17.0 bundled via the plugin in `pom.xml`.

## How to run
- Run full build with style checks and tests:
  - `mvn -q -DskipITs verify`

## Notes
- Java version: 21 (compatible with the guideline to use Java > 17)
- Testing: JUnit 5 via Surefire 3.2.5
- Architectural style: DDD + Hexagonal; Checkstyle rules are intentionally moderate to not hinder domain-driven iterations.
