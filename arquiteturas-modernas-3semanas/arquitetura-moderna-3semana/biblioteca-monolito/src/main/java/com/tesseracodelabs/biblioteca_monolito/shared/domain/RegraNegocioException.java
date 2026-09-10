package com.tesseracodelabs.biblioteca_monolito.shared.domain;

/**
 * Lançada quando uma invariante ou regra de negócio é violada
 * (ex.: alugar um livro sem exemplares disponíveis, CNPJ inválido).
 * Mapeada para HTTP 422 pelo {@code GlobalExceptionHandler}.
 */
public class RegraNegocioException extends DomainException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
