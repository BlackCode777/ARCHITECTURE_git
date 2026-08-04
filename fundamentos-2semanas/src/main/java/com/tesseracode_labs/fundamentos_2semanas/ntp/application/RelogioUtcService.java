package com.tesseracode_labs.fundamentos_2semanas.ntp.application;

import java.time.Instant;
import org.springframework.stereotype.Service;

/**
 * Fonte única de tempo do sistema, sempre em UTC (docs/05-ntp-service.md).
 * Toda auditoria (createdAt/updatedAt) deve usar este serviço em vez de
 * chamar Instant.now() diretamente, para centralizar a política de clock.
 */
@Service
public class RelogioUtcService {

	public Instant agora() {
		return Instant.now();
	}
}
