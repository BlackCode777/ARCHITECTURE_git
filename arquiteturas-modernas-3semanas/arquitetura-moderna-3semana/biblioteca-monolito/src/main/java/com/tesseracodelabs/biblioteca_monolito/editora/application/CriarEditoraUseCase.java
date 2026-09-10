package com.tesseracodelabs.biblioteca_monolito.editora.application;

import com.tesseracodelabs.biblioteca_monolito.editora.domain.Cnpj;
import com.tesseracodelabs.biblioteca_monolito.editora.domain.Editora;
import com.tesseracodelabs.biblioteca_monolito.editora.domain.EditoraRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.ConflitoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cria uma editora, garantindo unicidade de CNPJ.
 */
@Service
public class CriarEditoraUseCase {

    private final EditoraRepository editoraRepository;

    public CriarEditoraUseCase(EditoraRepository editoraRepository) {
        this.editoraRepository = editoraRepository;
    }

    @Transactional
    public Editora executar(Comando comando) {
        Cnpj cnpj = new Cnpj(comando.cnpj());
        if (editoraRepository.existePorCnpj(cnpj.valor(), null)) {
            throw new ConflitoException("ja existe editora com o CNPJ " + cnpj.formatado());
        }
        Editora editora = Editora.nova(comando.nome(), cnpj, comando.cidade(), comando.site());
        return editoraRepository.salvar(editora);
    }

    public record Comando(String nome, String cnpj, String cidade, String site) {
    }
}
