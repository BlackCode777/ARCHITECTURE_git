package com.tesseracodelabs.biblioteca_monolito.autor.application;

import com.tesseracodelabs.biblioteca_monolito.autor.domain.AutorRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Remove um autor. Falha com 404 se o autor não existe.
 *
 * <p>Nota: a integridade referencial com {@code livro} é garantida pela FK no
 * banco (a remoção falha se houver livros do autor) — o módulo {@code autor}
 * não conhece {@code livro}.
 */
@Service
public class RemoverAutorUseCase {

    private final AutorRepository autorRepository;

    public RemoverAutorUseCase(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    @Transactional
    public void executar(Long id) {
        if (!autorRepository.existePorId(id)) {
            throw RecursoNaoEncontradoException.de("Autor", id);
        }
        autorRepository.remover(id);
    }
}
