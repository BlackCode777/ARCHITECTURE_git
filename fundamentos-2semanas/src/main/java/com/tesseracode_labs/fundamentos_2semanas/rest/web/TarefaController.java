package com.tesseracode_labs.fundamentos_2semanas.rest.web;

import com.tesseracode_labs.fundamentos_2semanas.rest.application.AtualizarTarefaUseCase;
import com.tesseracode_labs.fundamentos_2semanas.rest.application.BuscarTarefaUseCase;
import com.tesseracode_labs.fundamentos_2semanas.rest.application.ConcluirTarefaRestUseCase;
import com.tesseracode_labs.fundamentos_2semanas.rest.application.CriarTarefaUseCase;
import com.tesseracode_labs.fundamentos_2semanas.rest.application.DeletarTarefaUseCase;
import com.tesseracode_labs.fundamentos_2semanas.rest.application.ListarTarefasUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CRUD REST completo de Tarefas (docs/02-rest-service.md, Task 03 do roadmap):
 * verbos e códigos de status seguindo a semântica REST clássica.
 */
@RestController
@RequestMapping("/rest/tarefas")
@Tag(name = "Tarefas", description = "CRUD de tarefas — referência REST/HTTP")
public class TarefaController {

	private final CriarTarefaUseCase criarTarefaUseCase;
	private final ListarTarefasUseCase listarTarefasUseCase;
	private final BuscarTarefaUseCase buscarTarefaUseCase;
	private final AtualizarTarefaUseCase atualizarTarefaUseCase;
	private final ConcluirTarefaRestUseCase concluirTarefaUseCase;
	private final DeletarTarefaUseCase deletarTarefaUseCase;

	public TarefaController(CriarTarefaUseCase criarTarefaUseCase,
			ListarTarefasUseCase listarTarefasUseCase,
			BuscarTarefaUseCase buscarTarefaUseCase,
			AtualizarTarefaUseCase atualizarTarefaUseCase,
			ConcluirTarefaRestUseCase concluirTarefaUseCase,
			DeletarTarefaUseCase deletarTarefaUseCase) {
		this.criarTarefaUseCase = criarTarefaUseCase;
		this.listarTarefasUseCase = listarTarefasUseCase;
		this.buscarTarefaUseCase = buscarTarefaUseCase;
		this.atualizarTarefaUseCase = atualizarTarefaUseCase;
		this.concluirTarefaUseCase = concluirTarefaUseCase;
		this.deletarTarefaUseCase = deletarTarefaUseCase;
	}

	@PostMapping
	@Operation(summary = "Cria uma nova tarefa e publica TarefaCriadaEvent (EDA)")
	public ResponseEntity<TarefaResponse> criar(@RequestBody @Valid CriarTarefaRequest request) {
		var tarefa = criarTarefaUseCase.executar(request.titulo());
		return ResponseEntity.created(URI.create("/rest/tarefas/" + tarefa.getId()))
				.body(TarefaResponse.from(tarefa));
	}

	@GetMapping
	@Operation(summary = "Lista todas as tarefas")
	public ResponseEntity<List<TarefaResponse>> listar() {
		var tarefas = listarTarefasUseCase.executar().stream().map(TarefaResponse::from).toList();
		return ResponseEntity.ok(tarefas);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Busca uma tarefa por ID")
	public ResponseEntity<TarefaResponse> buscar(@PathVariable Long id) {
		return ResponseEntity.ok(TarefaResponse.from(buscarTarefaUseCase.executar(id)));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Atualiza o título de uma tarefa")
	public ResponseEntity<TarefaResponse> atualizar(@PathVariable Long id,
			@RequestBody @Valid AtualizarTarefaRequest request) {
		var tarefa = atualizarTarefaUseCase.executar(id, request.titulo());
		return ResponseEntity.ok(TarefaResponse.from(tarefa));
	}

	@PatchMapping("/{id}/concluir")
	@Operation(summary = "Marca uma tarefa como concluída")
	public ResponseEntity<TarefaResponse> concluir(@PathVariable Long id) {
		var tarefa = concluirTarefaUseCase.executar(id);
		return ResponseEntity.ok(TarefaResponse.from(tarefa));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Remove uma tarefa")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		deletarTarefaUseCase.executar(id);
		return ResponseEntity.noContent().build();
	}
}
