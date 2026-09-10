package com.tesseracodelabs.biblioteca_monolito.shared.domain;

/**
 * Lançada quando uma operação conflita com o estado atual — tipicamente
 * violação de unicidade (ISBN de livro, CNPJ de editora já cadastrados).
 * Mapeada para HTTP 409 pelo {@code GlobalExceptionHandler}.
 */
public class ConflitoException extends DomainException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
