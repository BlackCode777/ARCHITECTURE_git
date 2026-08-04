package com.tesseracode_labs.fundamentos_2semanas.eda.infrastructure.listener;

import com.tesseracode_labs.fundamentos_2semanas.eda.domain.event.TarefaCriadaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Consumer: reage ao evento publicado, hoje apenas registrando log estruturado. */
@Component
public class TarefaCriadaEventListener {

	private static final Logger log = LoggerFactory.getLogger(TarefaCriadaEventListener.class);

	@EventListener
	public void aoReceber(TarefaCriadaEvent event) {
		log.info("Evento recebido: tarefaId={} ocorridoEm={}", event.tarefaId(), event.ocorridoEm());
	}
}
