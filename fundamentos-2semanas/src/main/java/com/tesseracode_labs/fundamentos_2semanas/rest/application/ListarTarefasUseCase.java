package com.tesseracode_labs.fundamentos_2semanas.rest.application;

import com.tesseracode_labs.fundamentos_2semanas.rest.domain.TarefaEntity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListarTarefasUseCase {

	private final TarefaRepository tarefaRepository;

	public ListarTarefasUseCase(TarefaRepository tarefaRepository) {
		this.tarefaRepository = tarefaRepository;
	}

	public List<TarefaEntity> executar() {
		return tarefaRepository.findAll();
	}
}
