package com.tesseracodelabs.biblioteca_monolito.shared.domain;

/**
 * Raiz das exceções de domínio do monólito. Toda regra de negócio violada
 * lança uma subclasse desta — nunca {@code RuntimeException} genérica.
 *
 * <p>É traduzida para {@code ProblemDetail} (RFC 9457) pelo
 * {@code GlobalExceptionHandler} na camada web.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String mensagem) {
        super(mensagem);
    }

    protected DomainException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
