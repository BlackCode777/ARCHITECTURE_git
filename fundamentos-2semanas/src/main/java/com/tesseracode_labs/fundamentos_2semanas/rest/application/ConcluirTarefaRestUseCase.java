package com.tesseracode_labs.fundamentos_2semanas.rest.application;

import com.tesseracode_labs.fundamentos_2semanas.ntp.application.RelogioUtcService;
import com.tesseracode_labs.fundamentos_2semanas.rest.domain.TarefaEntity;
import com.tesseracode_labs.fundamentos_2semanas.shared.exception.TarefaNaoEncontradaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConcluirTarefaRestUseCase {

	private final TarefaRepository tarefaRepository;
	private final RelogioUtcService relogioUtcService;

	public ConcluirTarefaRestUseCase(TarefaRepository tarefaRepository, RelogioUtcService relogioUtcService) {
		this.tarefaRepository = tarefaRepository;
		this.relogioUtcService = relogioUtcService;
	}

	@Transactional
	public TarefaEntity executar(Long id) {
		TarefaEntity tarefa = tarefaRepository.findById(id)
				.orElseThrow(() -> new TarefaNaoEncontradaException(id));
		tarefa.concluir(relogioUtcService.agora());
		return tarefa;
	}
}
