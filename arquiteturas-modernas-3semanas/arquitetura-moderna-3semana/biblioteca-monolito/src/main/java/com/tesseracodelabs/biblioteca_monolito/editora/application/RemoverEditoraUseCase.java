package com.tesseracodelabs.biblioteca_monolito.editora.application;

import com.tesseracodelabs.biblioteca_monolito.editora.domain.EditoraRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Remove uma editora. A integridade com {@code livro} é garantida pela FK no banco.
 */
@Service
public class RemoverEditoraUseCase {

    private final EditoraRepository editoraRepository;

    public RemoverEditoraUseCase(EditoraRepository editoraRepository) {
        this.editoraRepository = editoraRepository;
    }

    @Transactional
    public void executar(Long id) {
        if (!editoraRepository.existePorId(id)) {
            throw RecursoNaoEncontradoException.de("Editora", id);
        }
        editoraRepository.remover(id);
    }
}
