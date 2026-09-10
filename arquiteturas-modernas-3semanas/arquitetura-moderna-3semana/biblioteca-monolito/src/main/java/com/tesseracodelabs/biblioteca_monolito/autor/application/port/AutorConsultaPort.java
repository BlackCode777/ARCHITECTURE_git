package com.tesseracodelabs.biblioteca_monolito.autor.application.port;

import java.util.Optional;

/**
 * API pública do módulo {@code autor} para os demais módulos do monólito
 * (hoje: {@code livro}). É o único ponto por onde outro módulo enxerga autor —
 * nunca importando {@code Autor}, {@code AutorEntity} ou {@code AutorRepository}.
 *
 * <p>No caminho para microsserviços, esta port vira um {@code AutorGateway} HTTP.
 */
public interface AutorConsultaPort {

    boolean existePorId(Long id);

    Optional<AutorResumo> buscarResumo(Long id);

    /**
     * Projeção mínima de um autor para consumo cross-módulo.
     */
    record AutorResumo(Long id, String nome, String nacionalidade) {
    }
}
