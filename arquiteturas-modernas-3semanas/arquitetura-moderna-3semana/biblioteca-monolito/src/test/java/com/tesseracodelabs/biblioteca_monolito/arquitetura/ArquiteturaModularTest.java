package com.tesseracodelabs.biblioteca_monolito.arquitetura;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Garante, em tempo de teste, as regras de fronteira do monólito modular
 * (ver {@code doc/02-ARQUITETURA-MONOLITO.md} §1):
 *
 * <ul>
 *   <li>Um módulo só enxerga o outro pela sua {@code application.port} pública —
 *       nunca por {@code domain} ou {@code infrastructure}.</li>
 *   <li>{@code domain} não conhece Spring nem JPA.</li>
 *   <li>Camadas respeitam a Dependency Rule (web → application → domain).</li>
 * </ul>
 */
class ArquiteturaModularTest {

    private static final String BASE = "com.tesseracodelabs.biblioteca_monolito";
    private static JavaClasses classes;

    @BeforeAll
    static void importar() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(BASE);
    }

    @Test
    void modulos_nao_acessam_domain_de_outro_modulo() {
        for (String modulo : new String[]{"autor", "editora", "livro", "aluguel"}) {
            noClasses().that().resideOutsideOfPackage(BASE + "." + modulo + "..")
                    .and().resideOutsideOfPackage(BASE + ".shared..")
                    .and().resideOutsideOfPackage(BASE + ".config..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            BASE + "." + modulo + ".domain..",
                            BASE + "." + modulo + ".infrastructure..")
                    .because(modulo + " so pode ser acessado pela sua application.port")
                    .check(classes);
        }
    }

    @Test
    void dominio_nao_depende_de_spring_nem_jpa() {
        noClasses().that().resideInAPackage(BASE + "..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..", "jakarta.persistence..", "org.hibernate..")
                .check(classes);
    }

    @Test
    void camadas_respeitam_a_dependency_rule() {
        Architectures.layeredArchitecture().consideringOnlyDependenciesInLayers()
                .layer("web").definedBy(BASE + "..web..")
                .layer("application").definedBy(BASE + "..application..")
                .layer("domain").definedBy(BASE + "..domain..")
                .layer("infrastructure").definedBy(BASE + "..infrastructure..")
                .whereLayer("web").mayNotBeAccessedByAnyLayer()
                .whereLayer("application").mayOnlyBeAccessedByLayers("web", "infrastructure")
                .whereLayer("infrastructure").mayNotBeAccessedByAnyLayer()
                .check(classes);
    }

    @Test
    void use_cases_terminam_com_UseCase() {
        classes().that().resideInAPackage(BASE + "..application..")
                .and().areAnnotatedWith(org.springframework.stereotype.Service.class)
                .should().haveSimpleNameEndingWith("UseCase")
                .check(classes);
    }
}
