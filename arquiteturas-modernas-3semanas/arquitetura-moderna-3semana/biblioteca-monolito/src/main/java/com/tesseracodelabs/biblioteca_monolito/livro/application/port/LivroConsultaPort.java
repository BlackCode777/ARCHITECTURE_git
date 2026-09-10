package com.tesseracodelabs.biblioteca_monolito.livro.application.port;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.GeneroLiterario;

import java.util.Optional;

/**
 * API pública do módulo {@code livro} para o módulo {@code aluguel}.
 *
 * <p>No caminho para microsserviços, vira um {@code LivroGateway} HTTP com
 * {@code baixarExemplar}/{@code retornarExemplar} virando {@code PATCH}.
 */
public interface LivroConsultaPort {

    Optional<LivroDisponibilidade> disponibilidade(Long livroId);

    /**
     * Retira um exemplar de circulação. Lança se o livro não existe ou não há
     * exemplar disponível.
     */
    void baixarExemplar(Long livroId);

    /**
     * Devolve um exemplar à circulação.
     */
    void retornarExemplar(Long livroId);

    /**
     * @param disponivel             há ao menos um exemplar livre
     * @param exemplaresDisponiveis  quantos exemplares livres
     * @param genero                 gênero do livro (para a política de tarifação)
     * @param numeroPaginas          nº de páginas (idem)
     */
    record LivroDisponibilidade(Long livroId, boolean disponivel, int exemplaresDisponiveis,
                                GeneroLiterario genero, int numeroPaginas) {
    }
}
