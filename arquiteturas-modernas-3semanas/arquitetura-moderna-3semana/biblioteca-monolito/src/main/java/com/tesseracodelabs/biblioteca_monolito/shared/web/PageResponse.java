package com.tesseracodelabs.biblioteca_monolito.shared.web;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.Pagina;

import java.util.List;
import java.util.function.Function;

/**
 * Envelope de paginação exposto pela API. Converte a {@code Pagina<T>} do
 * domínio para o contrato REST.
 *
 * @param conteudo     itens da página atual
 * @param pagina       índice da página (base 0)
 * @param tamanho      tamanho solicitado da página
 * @param totalItens   total de registros
 * @param totalPaginas total de páginas
 */
public record PageResponse<T>(
        List<T> conteudo,
        int pagina,
        int tamanho,
        long totalItens,
        int totalPaginas
) {

    public static <E, T> PageResponse<T> de(Pagina<E> pagina, Function<? super E, ? extends T> mapper) {
        return new PageResponse<>(
                pagina.itens().stream().<T>map(mapper).toList(),
                pagina.pagina(),
                pagina.tamanho(),
                pagina.totalItens(),
                pagina.totalPaginas()
        );
    }
}
