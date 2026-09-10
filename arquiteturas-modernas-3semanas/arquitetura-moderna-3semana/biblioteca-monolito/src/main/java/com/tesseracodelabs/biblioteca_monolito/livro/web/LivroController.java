package com.tesseracodelabs.biblioteca_monolito.livro.web;

import com.tesseracodelabs.biblioteca_monolito.autor.application.port.AutorConsultaPort;
import com.tesseracodelabs.biblioteca_monolito.editora.application.port.EditoraConsultaPort;
import com.tesseracodelabs.biblioteca_monolito.livro.application.AtualizarLivroUseCase;
import com.tesseracodelabs.biblioteca_monolito.livro.application.BuscarLivroUseCase;
import com.tesseracodelabs.biblioteca_monolito.livro.application.CriarLivroUseCase;
import com.tesseracodelabs.biblioteca_monolito.livro.application.ListarLivrosUseCase;
import com.tesseracodelabs.biblioteca_monolito.livro.application.RemoverLivroUseCase;
import com.tesseracodelabs.biblioteca_monolito.livro.domain.FiltroLivro;
import com.tesseracodelabs.biblioteca_monolito.livro.domain.Livro;
import com.tesseracodelabs.biblioteca_monolito.livro.web.dto.AtualizarLivroRequest;
import com.tesseracodelabs.biblioteca_monolito.livro.web.dto.CriarLivroRequest;
import com.tesseracodelabs.biblioteca_monolito.livro.web.dto.LivroResponse;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.GeneroLiterario;
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
 * Endpoints REST de livros, sob {@code /api/livros}.
 *
 * <p>O nome do autor e da editora vem dos ports públicos dos módulos {@code autor}
 * e {@code editora} — este controller nunca importa as entidades deles.
 */
@RestController
@RequestMapping("/api/livros")
@Tag(name = "Livros", description = "CRUD de livros do catalogo")
public class LivroController {

    private final CriarLivroUseCase criarLivro;
    private final AtualizarLivroUseCase atualizarLivro;
    private final BuscarLivroUseCase buscarLivro;
    private final ListarLivrosUseCase listarLivros;
    private final RemoverLivroUseCase removerLivro;
    private final LivroWebMapper mapper;
    private final AutorConsultaPort autorConsulta;
    private final EditoraConsultaPort editoraConsulta;

    public LivroController(CriarLivroUseCase criarLivro,
                           AtualizarLivroUseCase atualizarLivro,
                           BuscarLivroUseCase buscarLivro,
                           ListarLivrosUseCase listarLivros,
                           RemoverLivroUseCase removerLivro,
                           LivroWebMapper mapper,
                           AutorConsultaPort autorConsulta,
                           EditoraConsultaPort editoraConsulta) {
        this.criarLivro = criarLivro;
        this.atualizarLivro = atualizarLivro;
        this.buscarLivro = buscarLivro;
        this.listarLivros = listarLivros;
        this.removerLivro = removerLivro;
        this.mapper = mapper;
        this.autorConsulta = autorConsulta;
        this.editoraConsulta = editoraConsulta;
    }

    @PostMapping
    @Operation(summary = "Cria um livro")
    public ResponseEntity<LivroResponse> criar(@Valid @RequestBody CriarLivroRequest request,
                                               UriComponentsBuilder uriBuilder) {
        Livro livro = criarLivro.executar(mapper.paraComando(request));
        URI location = uriBuilder.path("/api/livros/{id}").buildAndExpand(livro.getId()).toUri();
        return ResponseEntity.created(location).body(enriquecer(livro));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um livro pelo ID")
    public LivroResponse buscar(@PathVariable Long id) {
        return enriquecer(buscarLivro.executar(id));
    }

    @GetMapping
    @Operation(summary = "Lista livros (filtro opcional por genero e autor)")
    public PageResponse<LivroResponse> listar(@RequestParam(required = false) GeneroLiterario genero,
                                              @RequestParam(required = false) Long autorId,
                                              @RequestParam(defaultValue = "0") Integer page,
                                              @RequestParam(defaultValue = "20") Integer size) {
        var pagina = listarLivros.executar(new FiltroLivro(genero, autorId), PedidoPagina.de(page, size));
        return PageResponse.de(pagina, this::enriquecer);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um livro")
    public LivroResponse atualizar(@PathVariable Long id,
                                   @Valid @RequestBody AtualizarLivroRequest request) {
        return enriquecer(atualizarLivro.executar(id, mapper.paraComando(request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove um livro")
    public void remover(@PathVariable Long id) {
        removerLivro.executar(id);
    }

    private LivroResponse enriquecer(Livro livro) {
        String autorNome = autorConsulta.buscarResumo(livro.getAutorId())
                .map(AutorConsultaPort.AutorResumo::nome).orElse(null);
        String editoraNome = editoraConsulta.buscarResumo(livro.getEditoraId())
                .map(EditoraConsultaPort.EditoraResumo::nome).orElse(null);
        return mapper.paraResponse(livro, autorNome, editoraNome);
    }
}
