package com.tesseracode_labs.fundamentos_2semanas.hexagonal.web;

import com.tesseracode_labs.fundamentos_2semanas.hexagonal.application.port.in.ConcluirTarefaPort;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** Adapter de entrada — depende apenas do Port, nunca do UseCase concreto. */
@RestController
public class ConcluirTarefaController {

	private final ConcluirTarefaPort concluirTarefaPort;

	public ConcluirTarefaController(ConcluirTarefaPort concluirTarefaPort) {
		this.concluirTarefaPort = concluirTarefaPort;
	}

	@PatchMapping("/hexagonal/tarefas/{id}/concluir")
	public void concluir(@PathVariable Long id) {
		concluirTarefaPort.concluir(id);
	}
}
