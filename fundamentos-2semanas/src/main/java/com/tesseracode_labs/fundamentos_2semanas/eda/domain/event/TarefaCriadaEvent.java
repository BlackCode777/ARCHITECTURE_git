package com.tesseracode_labs.fundamentos_2semanas.eda.domain.event;

import java.time.Instant;

/**
 * Domain Event publicado via ApplicationEventPublisher do Spring
 * (docs/03-eda-service.md) — ainda sem broker externo (Kafka/RabbitMQ virão
 * em fase futura do roadmap).
 */
public record TarefaCriadaEvent(Long tarefaId, Instant ocorridoEm) {
}
