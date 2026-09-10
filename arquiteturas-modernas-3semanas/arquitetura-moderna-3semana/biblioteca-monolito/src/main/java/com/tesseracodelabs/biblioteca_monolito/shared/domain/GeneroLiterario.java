package com.tesseracodelabs.biblioteca_monolito.shared.domain;

/**
 * Gênero literário de um {@code Livro}. Compartilhado entre os módulos no
 * monólito (nos microsserviços vira contrato copiado por serviço).
 *
 * <p>Ver {@code doc/01-DOMINIO-MODELO.md} §2.
 */
public enum GeneroLiterario {
    AVENTURA,
    ACAO,
    FICCAO_CIENTIFICA,
    FANTASIA,
    ROMANCE,
    TERROR,
    SUSPENSE,
    BIOGRAFIA,
    HISTORIA,
    TECNICO,
    INFANTIL,
    POESIA
}
