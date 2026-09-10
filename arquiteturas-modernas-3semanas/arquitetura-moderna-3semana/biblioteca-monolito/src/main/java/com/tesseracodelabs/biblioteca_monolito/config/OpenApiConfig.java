package com.tesseracodelabs.biblioteca_monolito.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metadados do documento OpenAPI servido pelo springdoc.
 * Swagger UI: {@code http://localhost:8080/swagger-ui.html}.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bibliotecaOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Biblioteca Monolito API")
                .description("""
                        Sistema de reserva/aluguel de livros — versao monolito modular.
                        Estudo comparativo Monolito x Microsservicos. Sem autenticacao.
                        """)
                .version("v1")
                .contact(new Contact().name("tesseracodelabs"))
                .license(new License().name("Uso educacional")));
    }
}
