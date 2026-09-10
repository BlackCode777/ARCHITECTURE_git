package com.tesseracodelabs.biblioteca_monolito.autor.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Payload de criação de autor.
 */
public record CriarAutorRequest(

        @NotBlank(message = "nome e obrigatorio")
        @Size(max = 200, message = "nome deve ter no maximo 200 caracteres")
        String nome,

        @Size(max = 100, message = "nacionalidade deve ter no maximo 100 caracteres")
        String nacionalidade,

        @Past(message = "nascimento deve estar no passado")
        LocalDate nascimento,

        @Size(max = 4000, message = "biografia deve ter no maximo 4000 caracteres")
        String biografia
) {
}
