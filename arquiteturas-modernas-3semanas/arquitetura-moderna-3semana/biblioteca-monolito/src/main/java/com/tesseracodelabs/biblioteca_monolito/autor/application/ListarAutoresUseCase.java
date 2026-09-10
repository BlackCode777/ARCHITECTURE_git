package com.tesseracodelabs.biblioteca_monolito.autor.application;

import com.tesseracodelabs.biblioteca_monolito.autor.domain.Autor;
import com.tesseracodelabs.biblioteca_monolito.autor.domain.AutorRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.Pagina;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.PedidoPagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lista autores de forma paginada.
 */
@Service
public class ListarAutoresUseCase {

    private final AutorRepository autorRepository;

    public ListarAutoresUseCase(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    @Transactional(readOnly = true)
    public Pagina<Autor> executar(PedidoPagina pedido) {
        return autorRepository.listar(pedido);
    }
}
