package com.tesseracodelabs.biblioteca_monolito.livro.domain;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.GeneroLiterario;

/**
 * Critérios opcionais de filtragem da listagem de livros.
 * Campos nulos são ignorados.
 */
public record FiltroLivro(GeneroLiterario genero, Long autorId) {

    public static FiltroLivro vazio() {
        return new FiltroLivro(null, null);
    }
}
