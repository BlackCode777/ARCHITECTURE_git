package com.tesseracodelabs.biblioteca_monolito.aluguel.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Payload para alugar um livro. A taxa NÃO vem no request — é calculada pela
 * política de tarifação no servidor.
 *
 * @param dataRetirada opcional (default: hoje)
 * @param dias         duração do aluguel (1 a 90)
 */
public record AlugarRequest(

        @NotNull(message = "livroId e obrigatorio")
        Long livroId,

        @NotBlank(message = "nomeLocatario e obrigatorio")
        String nomeLocatario,

        LocalDate dataRetirada,

        @Min(value = 1, message = "dias deve ser no minimo 1")
        @Max(value = 90, message = "dias deve ser no maximo 90")
        int dias
) {
}
