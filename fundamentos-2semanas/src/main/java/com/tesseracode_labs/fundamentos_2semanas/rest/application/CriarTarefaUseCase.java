package com.tesseracode_labs.fundamentos_2semanas.rest.application;

import com.tesseracode_labs.fundamentos_2semanas.eda.application.PublicarTarefaCriadaUseCase;
import com.tesseracode_labs.fundamentos_2semanas.ntp.application.RelogioUtcService;
import com.tesseracode_labs.fundamentos_2semanas.rest.domain.TarefaEntity;
import org.springframework.stereotype.Service;

@Service
public class CriarTarefaUseCase {

	private final TarefaRepository tarefaRepository;
	private final PublicarTarefaCriadaUseCase publicarTarefaCriadaUseCase;
	private final RelogioUtcService relogioUtcService;

	public CriarTarefaUseCase(TarefaRepository tarefaRepository,
			PublicarTarefaCriadaUseCase publicarTarefaCriadaUseCase,
			RelogioUtcService relogioUtcService) {
		this.tarefaRepository = tarefaRepository;
		this.publicarTarefaCriadaUseCase = publicarTarefaCriadaUseCase;
		this.relogioUtcService = relogioUtcService;
	}

	public TarefaEntity executar(String titulo) {
		TarefaEntity tarefa = tarefaRepository.save(new TarefaEntity(titulo, relogioUtcService.agora()));
		publicarTarefaCriadaUseCase.executar(tarefa.getId());
		return tarefa;
	}
}
