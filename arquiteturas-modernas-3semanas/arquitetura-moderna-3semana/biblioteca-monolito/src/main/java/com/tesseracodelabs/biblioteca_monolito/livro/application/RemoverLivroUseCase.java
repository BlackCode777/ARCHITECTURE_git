package com.tesseracodelabs.biblioteca_monolito.livro.application;

import com.tesseracodelabs.biblioteca_monolito.livro.domain.LivroRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Remove um livro. A FK de aluguel impede remover livro com aluguéis registrados.
 */
@Service
public class RemoverLivroUseCase {

    private final LivroRepository livroRepository;

    public RemoverLivroUseCase(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @Transactional
    public void executar(Long id) {
        if (!livroRepository.existePorId(id)) {
            throw RecursoNaoEncontradoException.de("Livro", id);
        }
        livroRepository.remover(id);
    }
}
