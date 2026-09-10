package com.tesseracodelabs.biblioteca_monolito.aluguel.domain.tarifacao;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Estratégia de cálculo da taxa de aluguel.
 *
 * <p>Exercita três pilares de OOP:
 * <ul>
 *   <li><b>Herança</b>: cada política concreta estende esta classe.</li>
 *   <li><b>Polimorfismo</b>: o use case de aluguel usa {@code PoliticaTarifacao}
 *       sem saber qual implementação recebeu.</li>
 *   <li><b>Template method</b>: {@link #calcular(ContextoTarifacao)} é {@code final}
 *       e fixa o algoritmo — subtotal por dias, depois {@link #ajustar}. As
 *       subclasses só preenchem os pontos de variação.</li>
 * </ul>
 */
public abstract class PoliticaTarifacao {

    /** Identificador persistido no aluguel (para auditoria/relatório). */
    public abstract String nome();

    /** Valor base por dia de aluguel. Cada subclasse define. */
    protected abstract BigDecimal valorDiario();

    /**
     * Ponto de extensão para a subclasse ajustar o subtotal (multiplicador,
     * desconto, período grátis...). Por padrão, não altera nada.
     */
    protected BigDecimal ajustar(BigDecimal subtotal, ContextoTarifacao ctx) {
        return subtotal;
    }

    /**
     * Template method — o esqueleto do cálculo. Não sobrescrever.
     */
    public final BigDecimal calcular(ContextoTarifacao ctx) {
        BigDecimal subtotal = valorDiario().multiply(BigDecimal.valueOf(ctx.dias()));
        BigDecimal total = ajustar(subtotal, ctx);
        if (total.signum() < 0) {
            total = BigDecimal.ZERO;
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
