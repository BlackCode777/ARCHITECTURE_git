package com.tesseracode_labs.fundamentos_2semanas.lb.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Resolve o identificador da instância a partir da variável de ambiente
 * INSTANCE_ID, definida por instância no podman-compose (docs/06-lb-service.md).
 */
@Service
public class InstanceIdProvider {

	private final String instanceId;

	public InstanceIdProvider(@Value("${instance.id:unknown}") String instanceId) {
		this.instanceId = instanceId;
	}

	public String instanceId() {
		return instanceId;
	}
}
