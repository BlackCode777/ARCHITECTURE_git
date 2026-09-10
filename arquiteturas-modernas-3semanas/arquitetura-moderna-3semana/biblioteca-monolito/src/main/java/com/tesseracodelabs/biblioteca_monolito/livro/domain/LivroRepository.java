package com.tesseracodelabs.biblioteca_monolito.livro.domain;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.Pagina;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.PedidoPagina;

import java.util.Optional;

/**
 * Port de saída para persistência de {@code Livro}.
 */
public interface LivroRepository {

    Livro salvar(Livro livro);

    Optional<Livro> buscarPorId(Long id);

    Pagina<Livro> listar(FiltroLivro filtro, PedidoPagina pedido);

    void remover(Long id);

    boolean existePorId(Long id);

    boolean existePorIsbn(String isbn, Long ignorarId);
}
