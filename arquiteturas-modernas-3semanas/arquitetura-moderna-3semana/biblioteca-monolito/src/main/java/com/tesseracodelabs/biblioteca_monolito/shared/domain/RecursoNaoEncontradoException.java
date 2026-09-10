package com.tesseracodelabs.biblioteca_monolito.shared.domain;

/**
 * Lançada quando um recurso referenciado por ID não existe.
 * Mapeada para HTTP 404 pelo {@code GlobalExceptionHandler}.
 */
public class RecursoNaoEncontradoException extends DomainException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    /**
     * @param recurso nome do tipo do recurso (ex.: {@code "Autor"})
     * @param id      identificador procurado
     */
    public static RecursoNaoEncontradoException de(String recurso, Object id) {
        return new RecursoNaoEncontradoException("%s %s nao encontrado".formatted(recurso, id));
    }
}
