package com.tesseracodelabs.biblioteca_monolito.aluguel.application;

import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.Aluguel;
import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.AluguelRepository;
import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.tarifacao.ContextoTarifacao;
import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.tarifacao.PoliticaTarifacao;
import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.tarifacao.SeletorPolitica;
import com.tesseracodelabs.biblioteca_monolito.livro.application.port.LivroConsultaPort;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RecursoNaoEncontradoException;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Aluga um livro.
 *
 * <p>No monólito é tudo <b>uma transação ACID</b>: valida disponibilidade,
 * calcula a taxa (política polimórfica), grava o aluguel e baixa o exemplar do
 * livro pelo port público do módulo {@code livro}. Se qualquer passo falha,
 * rollback total — sem compensação manual (essa é a diferença para os microsserviços).
 */
@Service
public class AlugarUseCase {

    private final AluguelRepository aluguelRepository;
    private final LivroConsultaPort livroConsulta;
    private final SeletorPolitica seletorPolitica;

    public AlugarUseCase(AluguelRepository aluguelRepository,
                         LivroConsultaPort livroConsulta,
                         SeletorPolitica seletorPolitica) {
        this.aluguelRepository = aluguelRepository;
        this.livroConsulta = livroConsulta;
        this.seletorPolitica = seletorPolitica;
    }

    @Transactional
    public Aluguel executar(Comando c) {
        var disponibilidade = livroConsulta.disponibilidade(c.livroId())
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Livro", c.livroId()));

        if (!disponibilidade.disponivel()) {
            throw new RegraNegocioException("livro %d nao tem exemplares disponiveis".formatted(c.livroId()));
        }

        LocalDate retirada = c.dataRetirada() == null ? LocalDate.now() : c.dataRetirada();
        LocalDate devolucaoPrevista = retirada.plusDays(c.dias());

        var contexto = new ContextoTarifacao(retirada, devolucaoPrevista,
                disponibilidade.genero(), disponibilidade.numeroPaginas());
        PoliticaTarifacao politica = seletorPolitica.escolher(contexto);
        BigDecimal taxa = politica.calcular(contexto);

        Aluguel aluguel = Aluguel.abrir(c.livroId(), c.nomeLocatario(),
                retirada, devolucaoPrevista, taxa, politica.nome());

        Aluguel salvo = aluguelRepository.salvar(aluguel);
        livroConsulta.baixarExemplar(c.livroId());
        return salvo;
    }

    /**
     * @param dias duração do aluguel em dias (>= 1)
     */
    public record Comando(Long livroId, String nomeLocatario, LocalDate dataRetirada, int dias) {

        public Comando {
            if (dias < 1) {
                throw new RegraNegocioException("dias de aluguel deve ser no minimo 1");
            }
        }
    }
}
