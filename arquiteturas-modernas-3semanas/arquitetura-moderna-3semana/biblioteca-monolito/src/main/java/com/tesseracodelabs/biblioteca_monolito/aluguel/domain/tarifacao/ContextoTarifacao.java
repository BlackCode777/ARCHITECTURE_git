package com.tesseracodelabs.biblioteca_monolito.aluguel.domain.tarifacao;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.GeneroLiterario;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Dados de entrada, imutáveis, para o cálculo da taxa de um aluguel.
 *
 * @param retirada          data de retirada do livro
 * @param devolucaoPrevista data prevista de devolução (deve ser posterior à retirada)
 * @param genero            gênero do livro alugado
 * @param numeroPaginas     nº de páginas do livro
 */
public record ContextoTarifacao(
        LocalDate retirada,
        LocalDate devolucaoPrevista,
        GeneroLiterario genero,
        int numeroPaginas
) {

    public ContextoTarifacao {
        if (retirada == null || devolucaoPrevista == null) {
            throw new RegraNegocioException("datas de retirada e devolucao sao obrigatorias");
        }
        if (!devolucaoPrevista.isAfter(retirada)) {
            throw new RegraNegocioException("devolucao prevista deve ser posterior a retirada");
        }
        if (genero == null) {
            throw new RegraNegocioException("genero e obrigatorio para tarifar");
        }
    }

    /**
     * Número de dias do período de aluguel (>= 1).
     */
    public long dias() {
        return ChronoUnit.DAYS.between(retirada, devolucaoPrevista);
    }
}
