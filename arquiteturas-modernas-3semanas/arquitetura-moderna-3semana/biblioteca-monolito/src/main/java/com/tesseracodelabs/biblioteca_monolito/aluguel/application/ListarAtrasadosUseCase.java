package com.tesseracodelabs.biblioteca_monolito.aluguel.application;

import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.Aluguel;
import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.AluguelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Lista os aluguéis em aberto cuja devolução prevista já passou e, de quebra,
 * marca cada um como {@code ATRASADO} (persistindo a mudança).
 */
@Service
public class ListarAtrasadosUseCase {

    private final AluguelRepository aluguelRepository;

    public ListarAtrasadosUseCase(AluguelRepository aluguelRepository) {
        this.aluguelRepository = aluguelRepository;
    }

    @Transactional
    public List<Aluguel> executar() {
        LocalDate hoje = LocalDate.now();
        List<Aluguel> atrasados = aluguelRepository.listarAtrasados(hoje);
        atrasados.forEach(a -> {
            if (a.marcarAtraso(hoje)) {
                aluguelRepository.salvar(a);
            }
        });
        return atrasados;
    }
}
