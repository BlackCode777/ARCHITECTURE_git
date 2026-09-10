package com.tesseracodelabs.biblioteca_monolito;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Sobe um PostgreSQL real via Testcontainers e verifica que o contexto Spring
 * carrega. No Windows com Podman, exportar antes de rodar:
 *   $env:DOCKER_HOST = "npipe:////./pipe/podman-machine-default"
 *   $env:TESTCONTAINERS_RYUK_DISABLED = "true"
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class BibliotecaMonolitoApplicationTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres =
			new PostgreSQLContainer<>("postgres:17-alpine");

	@Test
	void contextLoads() {
		// sobe o contexto completo com Flyway (schema, sem seed) sobre Postgres real
	}

}
