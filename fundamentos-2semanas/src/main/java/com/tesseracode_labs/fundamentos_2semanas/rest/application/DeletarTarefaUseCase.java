package com.tesseracode_labs.fundamentos_2semanas.rest.application;

import com.tesseracode_labs.fundamentos_2semanas.shared.exception.TarefaNaoEncontradaException;
import org.springframework.stereotype.Service;

@Service
public class DeletarTarefaUseCase {

	private final TarefaRepository tarefaRepository;

	public DeletarTarefaUseCase(TarefaRepository tarefaRepository) {
		this.tarefaRepository = tarefaRepository;
	}

	public void executar(Long id) {
		if (!tarefaRepository.existsById(id)) {
			throw new TarefaNaoEncontradaException(id);
		}
		tarefaRepository.deleteById(id);
	}
}
