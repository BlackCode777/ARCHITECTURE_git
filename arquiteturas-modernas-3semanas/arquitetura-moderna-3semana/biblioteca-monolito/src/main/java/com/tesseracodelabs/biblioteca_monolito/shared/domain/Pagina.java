package com.tesseracodelabs.biblioteca_monolito.shared.domain;

import java.util.List;
import java.util.function.Function;

/**
 * Página de resultados, independente de framework. Usada pelos ports de saída
 * do domínio para não vazar {@code org.springframework.data.domain.Page}.
 *
 * @param itens        conteúdo da página
 * @param pagina       índice base 0
 * @param tamanho      tamanho solicitado
 * @param totalItens   total de registros
 */
public record Pagina<T>(List<T> itens, int pagina, int tamanho, long totalItens) {

    public int totalPaginas() {
        return tamanho == 0 ? 0 : (int) Math.ceil((double) totalItens / tamanho);
    }

    public <R> Pagina<R> mapear(Function<? super T, ? extends R> mapper) {
        return new Pagina<>(itens.stream().<R>map(mapper).toList(), pagina, tamanho, totalItens);
    }
}
