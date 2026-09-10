package com.tesseracodelabs.biblioteca_monolito.editora.application;

import com.tesseracodelabs.biblioteca_monolito.editora.domain.Editora;
import com.tesseracodelabs.biblioteca_monolito.editora.domain.EditoraRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Busca uma editora pelo ID.
 */
@Service
public class BuscarEditoraUseCase {

    private final EditoraRepository editoraRepository;

    public BuscarEditoraUseCase(EditoraRepository editoraRepository) {
        this.editoraRepository = editoraRepository;
    }

    @Transactional(readOnly = true)
    public Editora executar(Long id) {
        return editoraRepository.buscarPorId(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Editora", id));
    }
}
