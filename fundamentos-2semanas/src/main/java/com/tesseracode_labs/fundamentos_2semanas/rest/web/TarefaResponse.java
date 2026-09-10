package com.tesseracode_labs.fundamentos_2semanas.rest.web;

import com.tesseracode_labs.fundamentos_2semanas.rest.domain.TarefaEntity;
import java.time.Instant;

public record TarefaResponse(
		Long id,
		String titulo,
		boolean concluida,
		Instant createdAt,
		Instant updatedAt) {

	public static TarefaResponse from(TarefaEntity entity) {
		return new TarefaResponse(
				entity.getId(),
				entity.getTitulo(),
				entity.isConcluida(),
				entity.getCreatedAt(),
				entity.getUpdatedAt());
	}
}
