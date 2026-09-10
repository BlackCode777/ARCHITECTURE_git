package com.tesseracodelabs.biblioteca_monolito.aluguel.web.dto;

import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.StatusAluguel;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representação de um aluguel devolvida pela API.
 */
public record AluguelResponse(
        Long id,
        Long livroId,
        String nomeLocatario,
        LocalDate dataRetirada,
        LocalDate dataDevolucaoPrevista,
        LocalDate dataDevolucaoReal,
        BigDecimal taxa,
        StatusAluguel status,
        String politica
) {
}
