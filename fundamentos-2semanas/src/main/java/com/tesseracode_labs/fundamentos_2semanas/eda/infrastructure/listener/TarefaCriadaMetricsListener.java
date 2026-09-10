package com.tesseracode_labs.fundamentos_2semanas.eda.infrastructure.listener;

import com.tesseracode_labs.fundamentos_2semanas.eda.domain.event.TarefaCriadaEvent;
import com.tesseracode_labs.fundamentos_2semanas.metrics.infrastructure.TarefaMetrics;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Segundo consumer do mesmo evento — demonstra Publish/Subscribe: múltiplos
 * listeners reagem independentemente ao mesmo TarefaCriadaEvent, sem que o
 * producer (PublicarTarefaCriadaUseCase) precise conhecê-los.
 * Ver docs/03-eda-service.md.
 */
@Component
public class TarefaCriadaMetricsListener {

	private final TarefaMetrics tarefaMetrics;

	public TarefaCriadaMetricsListener(TarefaMetrics tarefaMetrics) {
		this.tarefaMetrics = tarefaMetrics;
	}

	@EventListener
	public void aoReceber(TarefaCriadaEvent event) {
		tarefaMetrics.incrementarTarefaCriada();
	}
}
