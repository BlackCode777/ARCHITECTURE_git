package com.tesseracode_labs.fundamentos_2semanas.rest.web;

import com.tesseracode_labs.fundamentos_2semanas.rest.application.ConsultarStatusUseCase;
import com.tesseracode_labs.fundamentos_2semanas.rest.domain.StatusResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller de referência REST/HTTP (docs/02-rest-service.md): recursos,
 * verbos e códigos de status seguindo os princípios REST clássicos.
 */
@RestController
public class RestDemoController {

	private final ConsultarStatusUseCase consultarStatusUseCase;

	public RestDemoController(ConsultarStatusUseCase consultarStatusUseCase) {
		this.consultarStatusUseCase = consultarStatusUseCase;
	}

	@GetMapping("/rest/status")
	public StatusResponse status() {
		return consultarStatusUseCase.executar();
	}
}
