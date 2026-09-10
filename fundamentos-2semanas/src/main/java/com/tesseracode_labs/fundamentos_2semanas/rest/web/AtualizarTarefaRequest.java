package com.tesseracode_labs.fundamentos_2semanas.rest.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarTarefaRequest(
		@NotBlank(message = "Título é obrigatório")
		@Size(max = 255, message = "Título pode ter no máximo 255 caracteres")
		String titulo) {
}
