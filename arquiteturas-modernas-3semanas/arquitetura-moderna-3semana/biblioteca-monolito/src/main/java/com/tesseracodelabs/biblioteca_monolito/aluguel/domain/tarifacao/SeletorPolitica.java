package com.tesseracodelabs.biblioteca_monolito.aluguel.domain.tarifacao;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.GeneroLiterario;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;

/**
 * Escolhe a {@link PoliticaTarifacao} para um aluguel.
 *
 * <p>Regra do estudo: livro de gênero {@code TECNICO} ou {@code INFANTIL} usa a
 * política por gênero; os demais usam a padrão. A política promocional é
 * aplicada quando o período é longo (&gt; 14 dias), para incentivar devolução
 * dentro do prazo padrão. A escolha é polimórfica — quem chama só recebe uma
 * {@code PoliticaTarifacao}.
 */
public class SeletorPolitica {

    private static final int LIMIAR_PROMOCIONAL_DIAS = 14;

    public PoliticaTarifacao escolher(ContextoTarifacao ctx) {
        if (ctx == null) {
            throw new RegraNegocioException("contexto de tarifacao e obrigatorio");
        }
        if (ctx.dias() > LIMIAR_PROMOCIONAL_DIAS) {
            return new TarifacaoPromocional();
        }
        if (ctx.genero() == GeneroLiterario.TECNICO || ctx.genero() == GeneroLiterario.INFANTIL) {
            return new TarifacaoPorGenero();
        }
        return new TarifacaoPadrao();
    }

    /**
     * Reconstrói a política a partir do nome persistido (uso em relatórios).
     */
    public PoliticaTarifacao porNome(String nome) {
        return switch (nome) {
            case "PADRAO" -> new TarifacaoPadrao();
            case "POR_GENERO" -> new TarifacaoPorGenero();
            case "PROMOCIONAL" -> new TarifacaoPromocional();
            default -> throw new RegraNegocioException("politica de tarifacao desconhecida: " + nome);
        };
    }
}
