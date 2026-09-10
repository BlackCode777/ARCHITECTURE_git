package com.tesseracodelabs.biblioteca_monolito.editora.application;

import com.tesseracodelabs.biblioteca_monolito.editora.domain.Cnpj;
import com.tesseracodelabs.biblioteca_monolito.editora.domain.Editora;
import com.tesseracodelabs.biblioteca_monolito.editora.domain.EditoraRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.ConflitoException;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Atualiza uma editora existente, mantendo a unicidade do CNPJ.
 */
@Service
public class AtualizarEditoraUseCase {

    private final EditoraRepository editoraRepository;

    public AtualizarEditoraUseCase(EditoraRepository editoraRepository) {
        this.editoraRepository = editoraRepository;
    }

    @Transactional
    public Editora executar(Long id, Comando comando) {
        Editora editora = editoraRepository.buscarPorId(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Editora", id));
        Cnpj cnpj = new Cnpj(comando.cnpj());
        if (editoraRepository.existePorCnpj(cnpj.valor(), id)) {
            throw new ConflitoException("ja existe outra editora com o CNPJ " + cnpj.formatado());
        }
        editora.atualizar(comando.nome(), cnpj, comando.cidade(), comando.site());
        return editoraRepository.salvar(editora);
    }

    public record Comando(String nome, String cnpj, String cidade, String site) {
    }
}
