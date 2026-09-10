package com.tesseracodelabs.biblioteca_monolito.editora.web;

import com.tesseracodelabs.biblioteca_monolito.editora.application.AtualizarEditoraUseCase;
import com.tesseracodelabs.biblioteca_monolito.editora.application.CriarEditoraUseCase;
import com.tesseracodelabs.biblioteca_monolito.editora.domain.Editora;
import com.tesseracodelabs.biblioteca_monolito.editora.web.dto.AtualizarEditoraRequest;
import com.tesseracodelabs.biblioteca_monolito.editora.web.dto.CriarEditoraRequest;
import com.tesseracodelabs.biblioteca_monolito.editora.web.dto.EditoraResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct: DTOs da web ↔ objetos de aplicação/domínio de editora.
 */
@Mapper(componentModel = "spring")
public interface EditoraWebMapper {

    CriarEditoraUseCase.Comando paraComando(CriarEditoraRequest request);

    AtualizarEditoraUseCase.Comando paraComando(AtualizarEditoraRequest request);

    @Mapping(target = "cnpj", expression = "java(editora.getCnpj().formatado())")
    EditoraResponse paraResponse(Editora editora);
}
