package com.tesseracodelabs.biblioteca_monolito.livro.web;

import com.tesseracodelabs.biblioteca_monolito.livro.application.AtualizarLivroUseCase;
import com.tesseracodelabs.biblioteca_monolito.livro.application.CriarLivroUseCase;
import com.tesseracodelabs.biblioteca_monolito.livro.domain.Livro;
import com.tesseracodelabs.biblioteca_monolito.livro.web.dto.AtualizarLivroRequest;
import com.tesseracodelabs.biblioteca_monolito.livro.web.dto.CriarLivroRequest;
import com.tesseracodelabs.biblioteca_monolito.livro.web.dto.LivroResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct: DTOs da web ↔ objetos de aplicação/domínio de livro.
 * O nome do autor/editora é preenchido pelo controller (vem de outro módulo).
 */
@Mapper(componentModel = "spring")
public interface LivroWebMapper {

    CriarLivroUseCase.Comando paraComando(CriarLivroRequest request);

    AtualizarLivroUseCase.Comando paraComando(AtualizarLivroRequest request);

    @Mapping(target = "autorNome", source = "autorNome")
    @Mapping(target = "editoraNome", source = "editoraNome")
    LivroResponse paraResponse(Livro livro, String autorNome, String editoraNome);
}
