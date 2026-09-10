package com.tesseracodelabs.biblioteca_monolito.aluguel.application;

import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.Aluguel;
import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.AluguelRepository;
import com.tesseracodelabs.biblioteca_monolito.livro.application.port.LivroConsultaPort;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Registra a devolução de um livro alugado e devolve o exemplar à circulação —
 * tudo em uma transação.
 */
@Service
public class DevolverUseCase {

    private final AluguelRepository aluguelRepository;
    private final LivroConsultaPort livroConsulta;

    public DevolverUseCase(AluguelRepository aluguelRepository, LivroConsultaPort livroConsulta) {
        this.aluguelRepository = aluguelRepository;
        this.livroConsulta = livroConsulta;
    }

    @Transactional
    public Aluguel executar(Long aluguelId, LocalDate quando) {
        Aluguel aluguel = aluguelRepository.buscarPorId(aluguelId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Aluguel", aluguelId));

        LocalDate data = quando == null ? LocalDate.now() : quando;
        aluguel.devolver(data);

        Aluguel salvo = aluguelRepository.salvar(aluguel);
        livroConsulta.retornarExemplar(aluguel.getLivroId());
        return salvo;
    }
}
