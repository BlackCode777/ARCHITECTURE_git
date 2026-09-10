package com.tesseracodelabs.biblioteca_monolito.aluguel.domain.tarifacao;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.GeneroLiterario;

import java.math.BigDecimal;

/**
 * Política por gênero: multiplica o subtotal por um fator conforme o gênero
 * do livro. Livro técnico custa o dobro; infantil, metade; o resto, valor cheio.
 */
public class TarifacaoPorGenero extends PoliticaTarifacao {

    private static final BigDecimal FATOR_TECNICO = new BigDecimal("2.0");
    private static final BigDecimal FATOR_INFANTIL = new BigDecimal("0.5");
    private static final BigDecimal FATOR_PADRAO = BigDecimal.ONE;

    @Override
    public String nome() {
        return "POR_GENERO";
    }

    @Override
    protected BigDecimal valorDiario() {
        return TarifacaoPadrao.VALOR_DIARIO;
    }

    @Override
    protected BigDecimal ajustar(BigDecimal subtotal, ContextoTarifacao ctx) {
        return subtotal.multiply(fator(ctx.genero()));
    }

    private BigDecimal fator(GeneroLiterario genero) {
        return switch (genero) {
            case TECNICO -> FATOR_TECNICO;
            case INFANTIL -> FATOR_INFANTIL;
            default -> FATOR_PADRAO;
        };
    }
}
