package com.tesseracodelabs.biblioteca_monolito.aluguel.application;

import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.Aluguel;
import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.AluguelRepository;
import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.StatusAluguel;
import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.tarifacao.SeletorPolitica;
import com.tesseracodelabs.biblioteca_monolito.livro.application.port.LivroConsultaPort;
import com.tesseracodelabs.biblioteca_monolito.livro.application.port.LivroConsultaPort.LivroDisponibilidade;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.GeneroLiterario;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RecursoNaoEncontradoException;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlugarUseCaseTest {

    @Mock
    AluguelRepository aluguelRepository;
    @Mock
    LivroConsultaPort livroConsulta;

    // política real — o cálculo faz parte do que queremos verificar
    AlugarUseCase useCase() {
        return new AlugarUseCase(aluguelRepository, livroConsulta, new SeletorPolitica());
    }

    @Test
    void aluga_calcula_taxa_e_baixa_exemplar_na_mesma_transacao() {
        when(livroConsulta.disponibilidade(1L)).thenReturn(Optional.of(
                new LivroDisponibilidade(1L, true, 2, GeneroLiterario.ROMANCE, 300)));
        when(aluguelRepository.salvar(any(Aluguel.class))).thenAnswer(inv -> inv.getArgument(0));

        Aluguel aluguel = useCase().executar(new AlugarUseCase.Comando(1L, "Ana", null, 7));

        assertThat(aluguel.getStatus()).isEqualTo(StatusAluguel.ATIVO);
        assertThat(aluguel.getPolitica()).isEqualTo("PADRAO");
        assertThat(aluguel.getTaxa()).isEqualByComparingTo(new BigDecimal("14.00"));
        verify(aluguelRepository).salvar(any(Aluguel.class));
        verify(livroConsulta).baixarExemplar(1L);
    }

    @Test
    void livro_tecnico_de_periodo_curto_usa_politica_por_genero() {
        when(livroConsulta.disponibilidade(9L)).thenReturn(Optional.of(
                new LivroDisponibilidade(9L, true, 1, GeneroLiterario.TECNICO, 500)));
        when(aluguelRepository.salvar(any(Aluguel.class))).thenAnswer(inv -> inv.getArgument(0));

        Aluguel aluguel = useCase().executar(new AlugarUseCase.Comando(9L, "Bea", null, 7));

        assertThat(aluguel.getPolitica()).isEqualTo("POR_GENERO");
        assertThat(aluguel.getTaxa()).isEqualByComparingTo(new BigDecimal("28.00"));
    }

    @Test
    void recusa_quando_livro_indisponivel_e_nao_baixa_exemplar() {
        when(livroConsulta.disponibilidade(2L)).thenReturn(Optional.of(
                new LivroDisponibilidade(2L, false, 0, GeneroLiterario.ACAO, 100)));

        assertThatThrownBy(() -> useCase().executar(new AlugarUseCase.Comando(2L, "Ana", null, 5)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("nao tem exemplares");

        verify(aluguelRepository, never()).salvar(any());
        verify(livroConsulta, never()).baixarExemplar(any());
    }

    @Test
    void recusa_quando_livro_nao_existe() {
        when(livroConsulta.disponibilidade(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase().executar(new AlugarUseCase.Comando(404L, "Ana", null, 5)))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(aluguelRepository, never()).salvar(any());
    }

    @Test
    void comando_rejeita_dias_menor_que_um() {
        assertThatThrownBy(() -> new AlugarUseCase.Comando(1L, "Ana", null, 0))
                .isInstanceOf(RegraNegocioException.class);
    }
}
