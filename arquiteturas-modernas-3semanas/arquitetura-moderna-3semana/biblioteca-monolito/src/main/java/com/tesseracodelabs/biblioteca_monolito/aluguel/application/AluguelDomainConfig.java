package com.tesseracodelabs.biblioteca_monolito.aluguel.application;

import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.tarifacao.SeletorPolitica;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Expõe os objetos de domínio sem anotação Spring como beans para injeção
 * nos use cases do módulo {@code aluguel}.
 */
@Configuration
class AluguelDomainConfig {

    @Bean
    SeletorPolitica seletorPolitica() {
        return new SeletorPolitica();
    }
}
