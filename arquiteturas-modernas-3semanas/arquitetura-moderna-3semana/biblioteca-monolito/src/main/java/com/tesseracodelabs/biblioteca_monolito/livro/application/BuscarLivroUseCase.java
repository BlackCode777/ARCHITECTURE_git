package com.tesseracodelabs.biblioteca_monolito.livro.application;

import com.tesseracodelabs.biblioteca_monolito.livro.domain.Livro;
import com.tesseracodelabs.biblioteca_monolito.livro.domain.LivroRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Busca um livro pelo ID.
 */
@Service
public class BuscarLivroUseCase {

    private final LivroRepository livroRepository;

    public BuscarLivroUseCase(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @Transactional(readOnly = true)
    public Livro executar(Long id) {
        return livroRepository.buscarPorId(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Livro", id));
    }
}
