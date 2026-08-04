package com.tesseracode_labs.fundamentos_2semanas.rest.application;

import com.tesseracode_labs.fundamentos_2semanas.rest.domain.StatusResponse;
import org.springframework.stereotype.Service;

@Service
public class ConsultarStatusUseCase {

	public StatusResponse executar() {
		return new StatusResponse("rest-service", "UP");
	}
}
