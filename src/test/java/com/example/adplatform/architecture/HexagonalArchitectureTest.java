package com.example.adplatform.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * ArchUnit rules to assert Hexagonal Architecture boundaries.
 *
 * Package conventions in this project:
 * - Domain: com.example.adplatform.domain..
 * - Application: com.example.adplatform.application..
 * - Infrastructure: com.example.adplatform.infrastructure..
 * - Web (controllers, mappers, dtos, exceptions) is under infrastructure.web..
 */
public class HexagonalArchitectureTest {

    private final JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.example.adplatform");

    @Test
    @DisplayName("Domain must be independent from application and infrastructure")
    void domainShouldNotDependOnApplicationOrInfrastructure() {
        ArchRule rule = noClasses().that().resideInAnyPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..application..",
                        "..infrastructure.."
                );
        rule.check(classes);
    }

    @Test
    @DisplayName("Application layer should depend on domain, but not on infrastructure")
    void applicationShouldOnlyDependOnDomainAndNotInfrastructure() {
        ArchRule noInfra = noClasses().that().resideInAnyPackage("..application..")
                .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..")
                .because("Application services talk to domain via ports; infra implements adapters");
        noInfra.check(classes);
    }

    @Test
    @DisplayName("Domain must not use Spring annotations")
    void domainShouldNotUseSpringAnnotations() {
        ArchRule rule = noClasses().that().resideInAnyPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage("org.springframework..")
                .because("Domain must be framework-agnostic");
        rule.check(classes);
    }

    @Test
    @DisplayName("Controllers must not access repositories or JPA entities directly")
    void controllersShouldNotAccessRepositoriesOrEntitiesDirectly() {
        ArchRule rule = noClasses().that().resideInAnyPackage("..infrastructure.web.controller..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..infrastructure.persistence.repository..",
                        "..infrastructure.persistence.entity..",
                        "..repository.." // legacy package kept as a guard
                );
        rule.check(classes);
    }
}
