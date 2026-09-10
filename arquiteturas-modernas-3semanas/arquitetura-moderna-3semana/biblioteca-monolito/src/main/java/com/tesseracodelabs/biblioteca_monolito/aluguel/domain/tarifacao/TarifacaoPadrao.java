package com.tesseracodelabs.biblioteca_monolito.aluguel.domain.tarifacao;

import java.math.BigDecimal;

/**
 * Política padrão: valor fixo por dia, sem ajustes.
 */
public class TarifacaoPadrao extends PoliticaTarifacao {

    static final BigDecimal VALOR_DIARIO = new BigDecimal("2.00");

    @Override
    public String nome() {
        return "PADRAO";
    }

    @Override
    protected BigDecimal valorDiario() {
        return VALOR_DIARIO;
    }
}
