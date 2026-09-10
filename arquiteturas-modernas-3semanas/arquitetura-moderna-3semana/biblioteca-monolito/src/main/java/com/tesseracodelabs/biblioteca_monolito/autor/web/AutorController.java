package com.tesseracodelabs.biblioteca_monolito.autor.web;

import com.tesseracodelabs.biblioteca_monolito.autor.application.AtualizarAutorUseCase;
import com.tesseracodelabs.biblioteca_monolito.autor.application.BuscarAutorUseCase;
import com.tesseracodelabs.biblioteca_monolito.autor.application.CriarAutorUseCase;
import com.tesseracodelabs.biblioteca_monolito.autor.application.ListarAutoresUseCase;
import com.tesseracodelabs.biblioteca_monolito.autor.application.RemoverAutorUseCase;
import com.tesseracodelabs.biblioteca_monolito.autor.domain.Autor;
import com.tesseracodelabs.biblioteca_monolito.autor.web.dto.AtualizarAutorRequest;
import com.tesseracodelabs.biblioteca_monolito.autor.web.dto.AutorResponse;
import com.tesseracodelabs.biblioteca_monolito.autor.web.dto.CriarAutorRequest;
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
 * Endpoints REST de autores, sob {@code /api/autores}.
 */
@RestController
@RequestMapping("/api/autores")
@Tag(name = "Autores", description = "CRUD de autores")
public class AutorController {

    private final CriarAutorUseCase criarAutor;
    private final AtualizarAutorUseCase atualizarAutor;
    private final BuscarAutorUseCase buscarAutor;
    private final ListarAutoresUseCase listarAutores;
    private final RemoverAutorUseCase removerAutor;
    private final AutorWebMapper mapper;

    public AutorController(CriarAutorUseCase criarAutor,
                           AtualizarAutorUseCase atualizarAutor,
                           BuscarAutorUseCase buscarAutor,
                           ListarAutoresUseCase listarAutores,
                           RemoverAutorUseCase removerAutor,
                           AutorWebMapper mapper) {
        this.criarAutor = criarAutor;
        this.atualizarAutor = atualizarAutor;
        this.buscarAutor = buscarAutor;
        this.listarAutores = listarAutores;
        this.removerAutor = removerAutor;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Cria um autor")
    public ResponseEntity<AutorResponse> criar(@Valid @RequestBody CriarAutorRequest request,
                                               UriComponentsBuilder uriBuilder) {
        Autor autor = criarAutor.executar(mapper.paraComando(request));
        URI location = uriBuilder.path("/api/autores/{id}").buildAndExpand(autor.getId()).toUri();
        return ResponseEntity.created(location).body(mapper.paraResponse(autor));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um autor pelo ID")
    public AutorResponse buscar(@PathVariable Long id) {
        return mapper.paraResponse(buscarAutor.executar(id));
    }

    @GetMapping
    @Operation(summary = "Lista autores (paginado)")
    public PageResponse<AutorResponse> listar(@RequestParam(defaultValue = "0") Integer page,
                                              @RequestParam(defaultValue = "20") Integer size) {
        return PageResponse.de(listarAutores.executar(PedidoPagina.de(page, size)), mapper::paraResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um autor")
    public AutorResponse atualizar(@PathVariable Long id,
                                   @Valid @RequestBody AtualizarAutorRequest request) {
        return mapper.paraResponse(atualizarAutor.executar(id, mapper.paraComando(request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove um autor")
    public void remover(@PathVariable Long id) {
        removerAutor.executar(id);
    }
}
