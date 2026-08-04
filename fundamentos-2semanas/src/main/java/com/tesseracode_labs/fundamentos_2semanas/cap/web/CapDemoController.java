package com.tesseracode_labs.fundamentos_2semanas.cap.web;

import com.tesseracode_labs.fundamentos_2semanas.cap.application.SimularParticaoUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint didático para observar o comportamento da API sob o CAP Theorem:
 * força uma partição simulada (banco indisponível) e retorna o modo de
 * consistência aplicado (strong x eventual) conforme a config ativa.
 */
@RestController
public class CapDemoController {

	private final SimularParticaoUseCase simularParticaoUseCase;

	public CapDemoController(SimularParticaoUseCase simularParticaoUseCase) {
		this.simularParticaoUseCase = simularParticaoUseCase;
	}

	@GetMapping("/cap/simular-particao")
	public String simularParticao() {
		return simularParticaoUseCase.executar();
	}
}
