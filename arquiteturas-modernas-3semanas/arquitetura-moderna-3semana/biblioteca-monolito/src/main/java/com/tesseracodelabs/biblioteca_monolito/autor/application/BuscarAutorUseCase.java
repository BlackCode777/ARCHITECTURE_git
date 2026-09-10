package com.tesseracodelabs.biblioteca_monolito.autor.application;

import com.tesseracodelabs.biblioteca_monolito.autor.domain.Autor;
import com.tesseracodelabs.biblioteca_monolito.autor.domain.AutorRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Busca um autor pelo ID.
 */
@Service
public class BuscarAutorUseCase {

    private final AutorRepository autorRepository;

    public BuscarAutorUseCase(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    @Transactional(readOnly = true)
    public Autor executar(Long id) {
        return autorRepository.buscarPorId(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Autor", id));
    }
}
