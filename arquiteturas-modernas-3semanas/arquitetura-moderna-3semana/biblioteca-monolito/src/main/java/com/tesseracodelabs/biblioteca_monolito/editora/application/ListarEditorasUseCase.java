package com.tesseracodelabs.biblioteca_monolito.editora.application;

import com.tesseracodelabs.biblioteca_monolito.editora.domain.Editora;
import com.tesseracodelabs.biblioteca_monolito.editora.domain.EditoraRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.Pagina;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.PedidoPagina;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lista editoras de forma paginada.
 */
@Service
public class ListarEditorasUseCase {

    private final EditoraRepository editoraRepository;

    public ListarEditorasUseCase(EditoraRepository editoraRepository) {
        this.editoraRepository = editoraRepository;
    }

    @Transactional(readOnly = true)
    public Pagina<Editora> executar(PedidoPagina pedido) {
        return editoraRepository.listar(pedido);
    }
}
