package com.tesseracodelabs.biblioteca_monolito.editora.domain;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EditoraTest {

    private static final Cnpj CNPJ_OK = new Cnpj("11222333000181");

    @Test
    void cria_editora_valida() {
        Editora e = Editora.nova("Companhia das Letras", CNPJ_OK, "Sao Paulo", "https://companhiadasletras.com.br");
        assertThat(e.getId()).isNull();
        assertThat(e.getNome()).isEqualTo("Companhia das Letras");
        assertThat(e.getCnpj().valor()).isEqualTo("11222333000181");
    }

    @Test
    void rejeita_nome_em_branco() {
        assertThatThrownBy(() -> Editora.nova(" ", CNPJ_OK, null, null))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("nome");
    }

    @Test
    void rejeita_cnpj_nulo() {
        assertThatThrownBy(() -> Editora.nova("Editora X", null, null, null))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("cnpj");
    }

    @Test
    void atualizar_troca_dados() {
        Editora e = new Editora(3L, "Antiga", CNPJ_OK, "RJ", null);
        Cnpj novo = new Cnpj("45.723.174/0001-10");
        e.atualizar("Nova", novo, "SP", "site");

        assertThat(e.getId()).isEqualTo(3L);
        assertThat(e.getNome()).isEqualTo("Nova");
        assertThat(e.getCnpj()).isEqualTo(novo);
    }
}
