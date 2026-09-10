package com.tesseracodelabs.biblioteca_monolito.autor.web.dto;

import java.time.LocalDate;

/**
 * Representação de um autor devolvida pela API.
 */
public record AutorResponse(
        Long id,
        String nome,
        String nacionalidade,
        LocalDate nascimento,
        String biografia
) {
}
