package com.tesseracodelabs.biblioteca_monolito.shared;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Gera CNPJs válidos (com dígitos verificadores corretos) e distintos para os
 * testes de integração que precisam persistir editoras sem colidir na constraint
 * de unicidade.
 */
public final class CnpjFake {

    private static final AtomicInteger SEQ = new AtomicInteger(1);

    private CnpjFake() {
    }

    /**
     * @return 14 dígitos, válidos, únicos por chamada nesta JVM
     */
    public static String proximo() {
        String base12 = "%08d0001".formatted(SEQ.getAndIncrement());
        int dv1 = digito(base12, new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        int dv2 = digito(base12 + dv1, new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        return base12 + dv1 + dv2;
    }

    private static int digito(String base, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            soma += (base.charAt(i) - '0') * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
