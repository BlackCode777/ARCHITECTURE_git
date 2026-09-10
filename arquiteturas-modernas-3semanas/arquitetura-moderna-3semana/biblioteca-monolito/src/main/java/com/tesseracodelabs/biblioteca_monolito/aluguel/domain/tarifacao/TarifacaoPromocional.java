package com.tesseracodelabs.biblioteca_monolito.aluguel.domain.tarifacao;

import java.math.BigDecimal;

/**
 * Política promocional: os primeiros 7 dias são grátis e o restante do período
 * tem 10% de desconto.
 */
public class TarifacaoPromocional extends PoliticaTarifacao {

    private static final int DIAS_GRATIS = 7;
    private static final BigDecimal FATOR_DESCONTO = new BigDecimal("0.90");

    @Override
    public String nome() {
        return "PROMOCIONAL";
    }

    @Override
    protected BigDecimal valorDiario() {
        return TarifacaoPadrao.VALOR_DIARIO;
    }

    @Override
    protected BigDecimal ajustar(BigDecimal subtotal, ContextoTarifacao ctx) {
        long diasCobrados = Math.max(0, ctx.dias() - DIAS_GRATIS);
        BigDecimal semGratis = valorDiario().multiply(BigDecimal.valueOf(diasCobrados));
        return semGratis.multiply(FATOR_DESCONTO);
    }
}
