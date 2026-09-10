package com.tesseracodelabs.biblioteca_monolito.aluguel.domain.tarifacao;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.GeneroLiterario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class PoliticaTarifacaoTest {

    private static ContextoTarifacao ctx(int dias, GeneroLiterario genero) {
        LocalDate retirada = LocalDate.of(2026, 1, 1);
        return new ContextoTarifacao(retirada, retirada.plusDays(dias), genero, 200);
    }

    static Stream<Arguments> cenarios() {
        return Stream.of(
                arguments(new TarifacaoPadrao(), ctx(7, GeneroLiterario.ROMANCE), "14.00"),
                arguments(new TarifacaoPadrao(), ctx(10, GeneroLiterario.TECNICO), "20.00"),
                arguments(new TarifacaoPorGenero(), ctx(7, GeneroLiterario.TECNICO), "28.00"),
                arguments(new TarifacaoPorGenero(), ctx(7, GeneroLiterario.INFANTIL), "7.00"),
                arguments(new TarifacaoPorGenero(), ctx(7, GeneroLiterario.ROMANCE), "14.00"),
                arguments(new TarifacaoPromocional(), ctx(7, GeneroLiterario.ROMANCE), "0.00"),
                arguments(new TarifacaoPromocional(), ctx(10, GeneroLiterario.ROMANCE), "5.40"),
                arguments(new TarifacaoPromocional(), ctx(30, GeneroLiterario.ROMANCE), "41.40")
        );
    }

    @ParameterizedTest(name = "{0} / {1} -> R$ {2}")
    @MethodSource("cenarios")
    void calcula_taxa_conforme_politica(PoliticaTarifacao politica, ContextoTarifacao ctx, String esperado) {
        assertThat(politica.calcular(ctx)).isEqualByComparingTo(new BigDecimal(esperado));
    }

    @Test
    void taxa_nunca_e_negativa() {
        // periodo curtissimo na promocional: subtotal viraria negativo sem o guard
        var contexto = ctx(1, GeneroLiterario.ROMANCE);
        assertThat(new TarifacaoPromocional().calcular(contexto)).isEqualByComparingTo("0.00");
    }

    @Test
    void resultado_tem_sempre_2_casas() {
        BigDecimal taxa = new TarifacaoPadrao().calcular(ctx(3, GeneroLiterario.ACAO));
        assertThat(taxa.scale()).isEqualTo(2);
    }

    @Test
    void polimorfismo_o_chamador_nao_sabe_a_implementacao() {
        PoliticaTarifacao politica = new SeletorPolitica().escolher(ctx(5, GeneroLiterario.TECNICO));
        assertThat(politica).isInstanceOf(TarifacaoPorGenero.class);
        assertThat(politica.nome()).isEqualTo("POR_GENERO");
    }
}
