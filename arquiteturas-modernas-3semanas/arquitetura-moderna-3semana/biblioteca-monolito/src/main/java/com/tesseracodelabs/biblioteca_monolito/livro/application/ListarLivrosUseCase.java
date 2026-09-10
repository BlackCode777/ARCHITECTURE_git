package com.tesseracodelabs.biblioteca_monolito.livro.application;

import com.tesseracodelabs.biblioteca_monolito.livro.domain.FiltroLivro;
import com.tesseracodelabs.biblioteca_monolito.livro.domain.Livro;
import com.tesseracodelabs.biblioteca_monolito.livro.domain.LivroRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.Pagina;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.PedidoPagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lista livros com filtro opcional por gênero e/ou autor.
 */
@Service
public class ListarLivrosUseCase {

    private final LivroRepository livroRepository;

    public ListarLivrosUseCase(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @Transactional(readOnly = true)
    public Pagina<Livro> executar(FiltroLivro filtro, PedidoPagina pedido) {
        return livroRepository.listar(filtro, pedido);
    }
}
