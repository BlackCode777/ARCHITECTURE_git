package com.tesseracodelabs.biblioteca_monolito.editora.domain;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;

/**
 * Value object de CNPJ. Guarda só os 14 dígitos e valida os dígitos
 * verificadores. Imutável.
 */
public record Cnpj(String valor) {

    public Cnpj {
        String digitos = valor == null ? "" : valor.replaceAll("\\D", "");
        if (digitos.length() != 14) {
            throw new RegraNegocioException("CNPJ deve ter 14 digitos");
        }
        if (digitos.chars().distinct().count() == 1) {
            throw new RegraNegocioException("CNPJ invalido");
        }
        if (!digitosVerificadoresOk(digitos)) {
            throw new RegraNegocioException("CNPJ invalido");
        }
        valor = digitos;
    }

    /**
     * Formato {@code 00.000.000/0000-00}.
     */
    public String formatado() {
        return "%s.%s.%s/%s-%s".formatted(
                valor.substring(0, 2), valor.substring(2, 5), valor.substring(5, 8),
                valor.substring(8, 12), valor.substring(12, 14));
    }

    private static boolean digitosVerificadoresOk(String cnpj) {
        int dv1 = calcularDigito(cnpj.substring(0, 12), new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        int dv2 = calcularDigito(cnpj.substring(0, 12) + dv1, new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        return dv1 == (cnpj.charAt(12) - '0') && dv2 == (cnpj.charAt(13) - '0');
    }

    private static int calcularDigito(String base, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            soma += (base.charAt(i) - '0') * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
