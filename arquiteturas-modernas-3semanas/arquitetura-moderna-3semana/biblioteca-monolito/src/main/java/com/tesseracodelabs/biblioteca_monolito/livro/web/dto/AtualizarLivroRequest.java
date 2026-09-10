package com.tesseracodelabs.biblioteca_monolito.livro.web.dto;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.GeneroLiterario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Payload de atualização de livro.
 */
public record AtualizarLivroRequest(

        @NotBlank(message = "titulo e obrigatorio")
        @Size(max = 300)
        String titulo,

        @NotBlank(message = "isbn e obrigatorio")
        @Size(max = 20)
        String isbn,

        @Positive(message = "numeroPaginas deve ser maior que zero")
        int numeroPaginas,

        Integer anoPublicacao,

        @NotNull(message = "genero e obrigatorio")
        GeneroLiterario genero,

        @Size(max = 4000)
        String sinopse,

        @NotNull(message = "autorId e obrigatorio")
        Long autorId,

        @NotNull(message = "editoraId e obrigatorio")
        Long editoraId,

        @PositiveOrZero(message = "exemplaresTotais nao pode ser negativo")
        int exemplaresTotais
) {
}
