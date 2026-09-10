package com.tesseracodelabs.biblioteca_monolito.autor.domain;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.Pagina;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.PedidoPagina;

import java.util.Optional;

/**
 * Port de saída para persistência de {@code Autor}. Implementado por
 * {@code AutorJpaAdapter} na camada {@code infrastructure}.
 *
 * <p>O domínio depende desta interface, nunca de JPA / Spring Data diretamente.
 */
public interface AutorRepository {

    Autor salvar(Autor autor);

    Optional<Autor> buscarPorId(Long id);

    Pagina<Autor> listar(PedidoPagina pedido);

    void remover(Long id);

    boolean existePorId(Long id);
}
