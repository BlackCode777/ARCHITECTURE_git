package com.tesseracodelabs.biblioteca_monolito.editora.application.port;

import java.util.Optional;

/**
 * API pública do módulo {@code editora} para os demais módulos (hoje: {@code livro}).
 * No caminho para microsserviços, vira um {@code EditoraGateway} HTTP.
 */
public interface EditoraConsultaPort {

    boolean existePorId(Long id);

    Optional<EditoraResumo> buscarResumo(Long id);

    record EditoraResumo(Long id, String nome, String cidade) {
    }
}
