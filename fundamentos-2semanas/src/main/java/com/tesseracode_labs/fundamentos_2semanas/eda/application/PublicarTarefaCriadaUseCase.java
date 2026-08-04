package com.tesseracode_labs.fundamentos_2semanas.eda.application;

import com.tesseracode_labs.fundamentos_2semanas.eda.domain.event.TarefaCriadaEvent;
import java.time.Instant;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/** Producer: publica o evento de domínio (padrão Publish/Subscribe). */
@Service
public class PublicarTarefaCriadaUseCase {

	private final ApplicationEventPublisher eventPublisher;

	public PublicarTarefaCriadaUseCase(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public void executar(Long tarefaId) {
		eventPublisher.publishEvent(new TarefaCriadaEvent(tarefaId, Instant.now()));
	}
}
