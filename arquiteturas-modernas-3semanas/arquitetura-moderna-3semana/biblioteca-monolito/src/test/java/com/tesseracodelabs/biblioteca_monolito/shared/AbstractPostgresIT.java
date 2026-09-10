package com.tesseracodelabs.biblioteca_monolito.shared;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Base para testes de integração que precisam de um PostgreSQL real.
 *
 * <p>Usa o padrão <em>Singleton Container</em>: um único contêiner é iniciado
 * uma vez por execução da JVM de testes e reaproveitado por todas as classes
 * que estendem esta — o Ryuk fica desativado (Podman/Windows) e o contêiner
 * morre com a JVM.
 *
 * <p>No Windows com Podman, exportar antes:
 * <pre>
 *   $env:DOCKER_HOST = "npipe:////./pipe/podman-machine-default"
 *   $env:TESTCONTAINERS_RYUK_DISABLED = "true"
 * </pre>
 *
 * <p>Ativa o profile {@code test} para que o seed {@code db/seed/V6} NÃO rode
 * nos testes (só o schema em {@code db/migration}).
 */
@ActiveProfiles("test")
public abstract class AbstractPostgresIT {

    protected static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17-alpine")
                    .withDatabaseName("biblioteca_db")
                    .withUsername("biblioteca")
                    .withPassword("biblioteca");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
