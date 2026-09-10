package com.tesseracodelabs.biblioteca_monolito.editora.web.dto;

/**
 * Representação de uma editora devolvida pela API. O CNPJ vai formatado.
 */
public record EditoraResponse(
        Long id,
        String nome,
        String cnpj,
        String cidade,
        String site
) {
}
