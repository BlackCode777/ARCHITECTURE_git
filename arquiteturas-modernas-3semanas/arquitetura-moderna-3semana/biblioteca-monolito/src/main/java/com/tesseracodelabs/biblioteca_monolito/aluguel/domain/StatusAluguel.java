package com.tesseracodelabs.biblioteca_monolito.aluguel.domain;

/**
 * Situação de um aluguel. Ver {@code doc/01-DOMINIO-MODELO.md} §2.
 */
public enum StatusAluguel {

    /** Livro retirado, dentro do prazo. */
    ATIVO,

    /** Livro devolvido. */
    DEVOLVIDO,

    /** Passou da data prevista e não foi devolvido. */
    ATRASADO
}
