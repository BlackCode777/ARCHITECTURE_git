package com.tesseracode_labs.fundamentos_2semanas.metrics.infrastructure;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/** Contador customizado de tarefas criadas, exposto em /actuator/prometheus. */
@Component
public class TarefaMetrics {

	private final Counter tarefasCriadas;

	public TarefaMetrics(MeterRegistry registry) {
		this.tarefasCriadas = Counter.builder("tarefas.criadas.total")
				.description("Total de tarefas criadas")
				.register(registry);
	}

	public void incrementarTarefaCriada() {
		tarefasCriadas.increment();
	}
}
