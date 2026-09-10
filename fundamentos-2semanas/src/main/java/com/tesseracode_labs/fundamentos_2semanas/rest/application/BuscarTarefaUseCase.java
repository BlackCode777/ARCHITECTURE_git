package com.tesseracode_labs.fundamentos_2semanas.rest.application;

import com.tesseracode_labs.fundamentos_2semanas.rest.domain.TarefaEntity;
import com.tesseracode_labs.fundamentos_2semanas.shared.exception.TarefaNaoEncontradaException;
import org.springframework.stereotype.Service;

@Service
public class BuscarTarefaUseCase {

	private final TarefaRepository tarefaRepository;

	public BuscarTarefaUseCase(TarefaRepository tarefaRepository) {
		this.tarefaRepository = tarefaRepository;
	}

	public TarefaEntity executar(Long id) {
		return tarefaRepository.findById(id)
				.orElseThrow(() -> new TarefaNaoEncontradaException(id));
	}
}
