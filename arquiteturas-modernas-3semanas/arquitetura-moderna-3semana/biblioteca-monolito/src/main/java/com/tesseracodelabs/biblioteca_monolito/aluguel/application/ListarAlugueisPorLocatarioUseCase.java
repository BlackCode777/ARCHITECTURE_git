package com.tesseracodelabs.biblioteca_monolito.aluguel.application;

import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.Aluguel;
import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.AluguelRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lista os aluguéis de um locatário.
 */
@Service
public class ListarAlugueisPorLocatarioUseCase {

    private final AluguelRepository aluguelRepository;

    public ListarAlugueisPorLocatarioUseCase(AluguelRepository aluguelRepository) {
        this.aluguelRepository = aluguelRepository;
    }

    @Transactional(readOnly = true)
    public List<Aluguel> executar(String nomeLocatario) {
        if (nomeLocatario == null || nomeLocatario.isBlank()) {
            throw new RegraNegocioException("informe o nome do locatario");
        }
        return aluguelRepository.listarPorLocatario(nomeLocatario.strip());
    }
}
