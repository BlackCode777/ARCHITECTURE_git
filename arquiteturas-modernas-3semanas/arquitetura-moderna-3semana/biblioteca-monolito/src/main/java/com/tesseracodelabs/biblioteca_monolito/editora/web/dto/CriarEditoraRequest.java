package com.tesseracodelabs.biblioteca_monolito.editora.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload de criação de editora.
 */
public record CriarEditoraRequest(

        @NotBlank(message = "nome e obrigatorio")
        @Size(max = 200)
        String nome,

        @NotBlank(message = "cnpj e obrigatorio")
        @Size(max = 18, message = "cnpj deve ter no maximo 18 caracteres")
        String cnpj,

        @Size(max = 100)
        String cidade,

        @Size(max = 200)
        String site
) {
}
