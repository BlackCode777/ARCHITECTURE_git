package com.tesseracodelabs.biblioteca_monolito.autor.web;

import com.tesseracodelabs.biblioteca_monolito.autor.application.AtualizarAutorUseCase;
import com.tesseracodelabs.biblioteca_monolito.autor.application.CriarAutorUseCase;
import com.tesseracodelabs.biblioteca_monolito.autor.domain.Autor;
import com.tesseracodelabs.biblioteca_monolito.autor.web.dto.AtualizarAutorRequest;
import com.tesseracodelabs.biblioteca_monolito.autor.web.dto.AutorResponse;
import com.tesseracodelabs.biblioteca_monolito.autor.web.dto.CriarAutorRequest;
import org.mapstruct.Mapper;

/**
 * MapStruct: DTOs da web ↔ objetos da camada de aplicação/domínio.
 */
@Mapper(componentModel = "spring")
public interface AutorWebMapper {

    CriarAutorUseCase.Comando paraComando(CriarAutorRequest request);

    AtualizarAutorUseCase.Comando paraComando(AtualizarAutorRequest request);

    AutorResponse paraResponse(Autor autor);
}
