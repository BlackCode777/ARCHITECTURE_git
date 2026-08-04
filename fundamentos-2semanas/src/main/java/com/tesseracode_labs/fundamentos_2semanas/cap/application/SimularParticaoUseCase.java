package com.tesseracode_labs.fundamentos_2semanas.cap.application;

import com.tesseracode_labs.fundamentos_2semanas.cap.domain.ConsistencyMode;
import org.springframework.stereotype.Service;

/**
 * Caso de uso que documenta o trade-off C x A durante uma partição simulada.
 * Ver docs/01-cap-service.md e ADR-0001 para o racional da escolha de modo.
 */
@Service
public class SimularParticaoUseCase {

	private final ConsistencyMode modoAtivo = ConsistencyMode.EVENTUAL;

	public String executar() {
		return "Partição simulada. Modo de consistência ativo: " + modoAtivo;
	}
}
