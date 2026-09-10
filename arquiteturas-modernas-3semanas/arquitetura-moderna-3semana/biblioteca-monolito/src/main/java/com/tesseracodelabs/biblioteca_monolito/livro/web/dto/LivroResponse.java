package com.tesseracodelabs.biblioteca_monolito.livro.web.dto;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.GeneroLiterario;

/**
 * Representação de um livro devolvida pela API, com o nome do autor e da
 * editora já resolvidos (via os ports públicos dos módulos {@code autor} e
 * {@code editora}).
 */
public record LivroResponse(
        Long id,
        String titulo,
        String isbn,
        int numeroPaginas,
        Integer anoPublicacao,
        GeneroLiterario genero,
        String sinopse,
        Long autorId,
        String autorNome,
        Long editoraId,
        String editoraNome,
        int exemplaresTotais,
        int exemplaresDisponiveis
) {
}
