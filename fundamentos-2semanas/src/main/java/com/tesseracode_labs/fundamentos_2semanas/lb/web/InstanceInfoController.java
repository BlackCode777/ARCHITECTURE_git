package com.tesseracode_labs.fundamentos_2semanas.lb.web;

import com.tesseracode_labs.fundamentos_2semanas.lb.application.InstanceIdProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint usado para observar o balanceamento entre instâncias via Nginx
 * (docs/06-lb-service.md): cada resposta identifica qual instância atendeu.
 */
@RestController
public class InstanceInfoController {

	private final InstanceIdProvider instanceIdProvider;

	public InstanceInfoController(InstanceIdProvider instanceIdProvider) {
		this.instanceIdProvider = instanceIdProvider;
	}

	@GetMapping("/lb/instance")
	public String instancia() {
		return instanceIdProvider.instanceId();
	}
}
