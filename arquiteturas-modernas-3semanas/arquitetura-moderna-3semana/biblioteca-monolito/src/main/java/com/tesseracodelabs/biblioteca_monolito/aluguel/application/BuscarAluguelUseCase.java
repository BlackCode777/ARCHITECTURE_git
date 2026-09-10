package com.tesseracodelabs.biblioteca_monolito.aluguel.application;

import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.Aluguel;
import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.AluguelRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Busca um aluguel pelo ID.
 */
@Service
public class BuscarAluguelUseCase {

    private final AluguelRepository aluguelRepository;

    public BuscarAluguelUseCase(AluguelRepository aluguelRepository) {
        this.aluguelRepository = aluguelRepository;
    }

    @Transactional(readOnly = true)
    public Aluguel executar(Long id) {
        return aluguelRepository.buscarPorId(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Aluguel", id));
    }
}
