package com.tesseracodelabs.biblioteca_monolito.editora.domain;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CnpjTest {

    @Test
    void aceita_cnpj_valido_com_ou_sem_mascara() {
        Cnpj comMascara = new Cnpj("11.222.333/0001-81");
        Cnpj semMascara = new Cnpj("11222333000181");

        assertThat(comMascara.valor()).isEqualTo("11222333000181");
        assertThat(semMascara.valor()).isEqualTo("11222333000181");
        assertThat(comMascara.formatado()).isEqualTo("11.222.333/0001-81");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "11222333000180",   // DV errado
            "11111111111111",   // todos iguais
            "123",              // curto demais
            "1122233300018199"  // longo demais
    })
    void rejeita_cnpj_invalido(String invalido) {
        assertThatThrownBy(() -> new Cnpj(invalido))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void rejeita_nulo() {
        assertThatThrownBy(() -> new Cnpj(null))
                .isInstanceOf(RegraNegocioException.class);
    }
}
