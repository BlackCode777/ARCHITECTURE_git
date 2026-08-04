package com.tesseracode_labs.fundamentos_2semanas.hexagonal.infrastructure.persistence;

import com.tesseracode_labs.fundamentos_2semanas.hexagonal.application.port.out.TarefaRepository;
import com.tesseracode_labs.fundamentos_2semanas.hexagonal.domain.Tarefa;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * Adapter de saída em memória — placeholder até a versão JPA (JpaAdapter)
 * ser implementada. Ver docs/04-hexagonal-service.md.
 */
@Component
public class InMemoryTarefaRepositoryAdapter implements TarefaRepository {

	private final Map<Long, Tarefa> tarefas = new ConcurrentHashMap<>();

	@Override
	public Optional<Tarefa> buscarPorId(Long id) {
		return Optional.ofNullable(tarefas.get(id));
	}

	@Override
	public void salvar(Tarefa tarefa) {
		tarefas.put(tarefa.id(), tarefa);
	}
}
