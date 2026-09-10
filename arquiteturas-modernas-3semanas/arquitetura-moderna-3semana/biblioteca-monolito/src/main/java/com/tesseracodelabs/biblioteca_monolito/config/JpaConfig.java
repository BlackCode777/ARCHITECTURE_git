package com.tesseracodelabs.biblioteca_monolito.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Habilita a auditoria JPA para os campos {@code criadoEm} / {@code atualizadoEm}
 * de {@code EntidadeBase}. Os {@code @SequenceGenerator} ficam em cada
 * {@code *Entity} concreta (nunca {@code IDENTITY}).
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
