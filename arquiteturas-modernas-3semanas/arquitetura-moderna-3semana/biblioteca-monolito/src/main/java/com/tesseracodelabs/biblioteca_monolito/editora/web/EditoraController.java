package com.tesseracodelabs.biblioteca_monolito.editora.web;

import com.tesseracodelabs.biblioteca_monolito.editora.application.AtualizarEditoraUseCase;
import com.tesseracodelabs.biblioteca_monolito.editora.application.BuscarEditoraUseCase;
import com.tesseracodelabs.biblioteca_monolito.editora.application.CriarEditoraUseCase;
import com.tesseracodelabs.biblioteca_monolito.editora.application.ListarEditorasUseCase;
import com.tesseracodelabs.biblioteca_monolito.editora.application.RemoverEditoraUseCase;
import com.tesseracodelabs.biblioteca_monolito.editora.domain.Editora;
import com.tesseracodelabs.biblioteca_monolito.editora.web.dto.AtualizarEditoraRequest;
import com.tesseracodelabs.biblioteca_monolito.editora.web.dto.CriarEditoraRequest;
import com.tesseracodelabs.biblioteca_monolito.editora.web.dto.EditoraResponse;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.PedidoPagina;
import com.tesseracodelabs.biblioteca_monolito.shared.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Endpoints REST de editoras, sob {@code /api/editoras}.
 */
@RestController
@RequestMapping("/api/editoras")
@Tag(name = "Editoras", description = "CRUD de editoras")
public class EditoraController {

    private final CriarEditoraUseCase criarEditora;
    private final AtualizarEditoraUseCase atualizarEditora;
    private final BuscarEditoraUseCase buscarEditora;
    private final ListarEditorasUseCase listarEditoras;
    private final RemoverEditoraUseCase removerEditora;
    private final EditoraWebMapper mapper;

    public EditoraController(CriarEditoraUseCase criarEditora,
                            AtualizarEditoraUseCase atualizarEditora,
                            BuscarEditoraUseCase buscarEditora,
                            ListarEditorasUseCase listarEditoras,
                            RemoverEditoraUseCase removerEditora,
                            EditoraWebMapper mapper) {
        this.criarEditora = criarEditora;
        this.atualizarEditora = atualizarEditora;
        this.buscarEditora = buscarEditora;
        this.listarEditoras = listarEditoras;
        this.removerEditora = removerEditora;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Cria uma editora")
    public ResponseEntity<EditoraResponse> criar(@Valid @RequestBody CriarEditoraRequest request,
                                                 UriComponentsBuilder uriBuilder) {
        Editora editora = criarEditora.executar(mapper.paraComando(request));
        URI location = uriBuilder.path("/api/editoras/{id}").buildAndExpand(editora.getId()).toUri();
        return ResponseEntity.created(location).body(mapper.paraResponse(editora));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma editora pelo ID")
    public EditoraResponse buscar(@PathVariable Long id) {
        return mapper.paraResponse(buscarEditora.executar(id));
    }

    @GetMapping
    @Operation(summary = "Lista editoras (paginado)")
    public PageResponse<EditoraResponse> listar(@RequestParam(defaultValue = "0") Integer page,
                                                @RequestParam(defaultValue = "20") Integer size) {
        return PageResponse.de(listarEditoras.executar(PedidoPagina.de(page, size)), mapper::paraResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma editora")
    public EditoraResponse atualizar(@PathVariable Long id,
                                     @Valid @RequestBody AtualizarEditoraRequest request) {
        return mapper.paraResponse(atualizarEditora.executar(id, mapper.paraComando(request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove uma editora")
    public void remover(@PathVariable Long id) {
        removerEditora.executar(id);
    }
}
