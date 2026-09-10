package com.tesseracodelabs.biblioteca_monolito.aluguel.domain;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AluguelTest {

    private static final LocalDate RETIRADA = LocalDate.of(2026, 1, 1);
    private static final LocalDate PREVISTA = LocalDate.of(2026, 1, 8);

    private Aluguel novo() {
        return Aluguel.abrir(1L, "Ana", RETIRADA, PREVISTA, new BigDecimal("14.00"), "PADRAO");
    }

    @Test
    void abre_como_ativo() {
        Aluguel a = novo();
        assertThat(a.getStatus()).isEqualTo(StatusAluguel.ATIVO);
        assertThat(a.getDataDevolucaoReal()).isNull();
        assertThat(a.estaEmAberto()).isTrue();
    }

    @Test
    void rejeita_previsao_antes_da_retirada() {
        assertThatThrownBy(() -> Aluguel.abrir(1L, "Ana", PREVISTA, RETIRADA,
                BigDecimal.ONE, "PADRAO"))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void rejeita_taxa_negativa() {
        assertThatThrownBy(() -> Aluguel.abrir(1L, "Ana", RETIRADA, PREVISTA,
                new BigDecimal("-1"), "PADRAO"))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void devolver_grava_data_e_muda_status() {
        Aluguel a = novo();
        a.devolver(LocalDate.of(2026, 1, 6));

        assertThat(a.getStatus()).isEqualTo(StatusAluguel.DEVOLVIDO);
        assertThat(a.getDataDevolucaoReal()).isEqualTo(LocalDate.of(2026, 1, 6));
        assertThat(a.estaEmAberto()).isFalse();
    }

    @Test
    void devolver_duas_vezes_falha() {
        Aluguel a = novo();
        a.devolver(LocalDate.of(2026, 1, 6));
        assertThatThrownBy(() -> a.devolver(LocalDate.of(2026, 1, 7)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("ja foi devolvido");
    }

    @Test
    void devolver_antes_da_retirada_falha() {
        Aluguel a = novo();
        assertThatThrownBy(() -> a.devolver(LocalDate.of(2025, 12, 31)))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void marca_atraso_quando_ativo_e_vencido() {
        Aluguel a = novo();
        boolean mudou = a.marcarAtraso(LocalDate.of(2026, 1, 9));

        assertThat(mudou).isTrue();
        assertThat(a.getStatus()).isEqualTo(StatusAluguel.ATRASADO);
    }

    @Test
    void nao_marca_atraso_se_ainda_no_prazo() {
        Aluguel a = novo();
        assertThat(a.marcarAtraso(LocalDate.of(2026, 1, 5))).isFalse();
        assertThat(a.getStatus()).isEqualTo(StatusAluguel.ATIVO);
    }

    @Test
    void nao_marca_atraso_se_ja_devolvido() {
        Aluguel a = novo();
        a.devolver(LocalDate.of(2026, 1, 5));
        assertThat(a.marcarAtraso(LocalDate.of(2026, 2, 1))).isFalse();
        assertThat(a.getStatus()).isEqualTo(StatusAluguel.DEVOLVIDO);
    }
}
