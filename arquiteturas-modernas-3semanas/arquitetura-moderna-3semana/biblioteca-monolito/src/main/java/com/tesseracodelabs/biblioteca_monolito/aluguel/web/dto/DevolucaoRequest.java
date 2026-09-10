package com.tesseracodelabs.biblioteca_monolito.aluguel.web.dto;

import java.time.LocalDate;

/**
 * Payload para registrar a devolução de um aluguel.
 *
 * @param dataDevolucao opcional (default: hoje)
 */
public record DevolucaoRequest(LocalDate dataDevolucao) {
}
