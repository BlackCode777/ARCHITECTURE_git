package com.tesseracode_labs.fundamentos_2semanas.hexagonal.application;

import com.tesseracode_labs.fundamentos_2semanas.hexagonal.application.port.in.ConcluirTarefaPort;
import com.tesseracode_labs.fundamentos_2semanas.hexagonal.application.port.out.TarefaRepository;
import com.tesseracode_labs.fundamentos_2semanas.hexagonal.domain.Tarefa;
import org.springframework.stereotype.Service;

@Service
public class ConcluirTarefaUseCase implements ConcluirTarefaPort {

	private final TarefaRepository tarefaRepository;

	public ConcluirTarefaUseCase(TarefaRepository tarefaRepository) {
		this.tarefaRepository = tarefaRepository;
	}

	@Override
	public void concluir(Long tarefaId) {
		Tarefa tarefa = tarefaRepository.buscarPorId(tarefaId)
				.orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada: " + tarefaId));
		tarefa.concluir();
		tarefaRepository.salvar(tarefa);
	}
}
