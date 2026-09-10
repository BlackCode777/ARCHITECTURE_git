package com.tesseracodelabs.biblioteca_monolito.aluguel.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Port de saída para persistência de {@code Aluguel}.
 */
public interface AluguelRepository {

    Aluguel salvar(Aluguel aluguel);

    Optional<Aluguel> buscarPorId(Long id);

    List<Aluguel> listarPorLocatario(String nomeLocatario);

    /**
     * Aluguéis em aberto ({@code ATIVO}/{@code ATRASADO}) com devolução prevista
     * anterior a {@code referencia}.
     */
    List<Aluguel> listarAtrasados(LocalDate referencia);
}
