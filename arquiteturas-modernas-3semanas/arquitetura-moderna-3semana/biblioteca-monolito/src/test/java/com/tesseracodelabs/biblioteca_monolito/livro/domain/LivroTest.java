package com.tesseracodelabs.biblioteca_monolito.livro.domain;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.GeneroLiterario;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LivroTest {

    private Livro livroCom(int totais) {
        return Livro.novo("Fundacao", "978-85-359-0277-2", 244, 1951,
                GeneroLiterario.FICCAO_CIENTIFICA, "sinopse", 1L, 2L, totais);
    }

    @Test
    void novo_livro_tem_todos_exemplares_disponiveis() {
        Livro l = livroCom(3);
        assertThat(l.getExemplaresDisponiveis()).isEqualTo(3);
        assertThat(l.getIsbn()).isEqualTo("9788535902772");
        assertThat(l.temExemplarDisponivel()).isTrue();
    }

    @Test
    void rejeita_paginas_nao_positivas() {
        assertThatThrownBy(() -> Livro.novo("X", "123", 0, null,
                GeneroLiterario.ACAO, null, 1L, 1L, 1))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("numeroPaginas");
    }

    @Test
    void rejeita_genero_nulo() {
        assertThatThrownBy(() -> Livro.novo("X", "123", 10, null,
                null, null, 1L, 1L, 1))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("genero");
    }

    @Test
    void rejeita_ano_no_futuro() {
        assertThatThrownBy(() -> Livro.novo("X", "123", 10, 3000,
                GeneroLiterario.ACAO, null, 1L, 1L, 1))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("anoPublicacao");
    }

    @Test
    void baixar_exemplar_ate_zerar_depois_falha() {
        Livro l = livroCom(2);
        l.baixarExemplar();
        l.baixarExemplar();
        assertThat(l.getExemplaresDisponiveis()).isZero();
        assertThat(l.temExemplarDisponivel()).isFalse();

        assertThatThrownBy(l::baixarExemplar)
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("nao ha exemplares");
    }

    @Test
    void retornar_exemplar_nao_passa_do_total() {
        Livro l = livroCom(1);
        assertThatThrownBy(l::retornarExemplar)
                .isInstanceOf(RegraNegocioException.class);

        l.baixarExemplar();
        l.retornarExemplar();
        assertThat(l.getExemplaresDisponiveis()).isEqualTo(1);
    }

    @Test
    void atualizar_nao_pode_reduzir_total_abaixo_dos_emprestados() {
        Livro l = livroCom(3);
        l.baixarExemplar();
        l.baixarExemplar(); // 2 emprestados, 1 disponivel

        assertThatThrownBy(() -> l.atualizar("Fundacao", "9788535902772", 244, 1951,
                GeneroLiterario.FICCAO_CIENTIFICA, "s", 1L, 2L, 1))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("emprestados");
    }

    @Test
    void atualizar_reduz_total_mantendo_emprestados() {
        Livro l = livroCom(5);
        l.baixarExemplar(); // 1 emprestado

        l.atualizar("Fundacao", "9788535902772", 244, 1951,
                GeneroLiterario.FICCAO_CIENTIFICA, "s", 1L, 2L, 3);

        assertThat(l.getExemplaresTotais()).isEqualTo(3);
        assertThat(l.getExemplaresDisponiveis()).isEqualTo(2); // 3 - 1 emprestado
    }

    @Test
    void reidratacao_valida_intervalo_de_exemplares() {
        assertThatThrownBy(() -> new Livro(1L, "X", "123", 10, null,
                GeneroLiterario.ACAO, null, 1L, 1L, 2, 5))
                .isInstanceOf(RegraNegocioException.class);
    }
}
