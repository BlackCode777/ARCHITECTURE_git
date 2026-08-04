package com.tesseracode_labs.fundamentos_2semanas.ntp.infrastructure;

import java.time.Instant;

/**
 * Value object de auditoria temporal (createdAt/updatedAt) usado pelas
 * entidades JPA. Ver docs/05-ntp-service.md.
 */
public record Auditoria(Instant createdAt, Instant updatedAt) {

	public Auditoria atualizar(Instant novoTimestamp) {
		return new Auditoria(this.createdAt, novoTimestamp);
	}
}
