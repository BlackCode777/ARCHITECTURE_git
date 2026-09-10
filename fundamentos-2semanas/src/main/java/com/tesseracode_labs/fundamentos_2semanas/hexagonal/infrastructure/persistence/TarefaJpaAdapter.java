package com.tesseracode_labs.fundamentos_2semanas.hexagonal.infrastructure.persistence;

import com.tesseracode_labs.fundamentos_2semanas.hexagonal.application.port.out.TarefaRepository;
import com.tesseracode_labs.fundamentos_2semanas.hexagonal.domain.Tarefa;
import com.tesseracode_labs.fundamentos_2semanas.ntp.application.RelogioUtcService;
import com.tesseracode_labs.fundamentos_2semanas.rest.domain.TarefaEntity;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Adapter de saída real: implementa o driven port do domínio hexagonal usando
 * o Spring Data JPA já existente em rest/application/TarefaRepository, que
 * persiste na mesma tabela "tarefas" (ver db/migration/V1__create_table_tarefas.sql).
 *
 * Traduz entre TarefaEntity (detalhe de infraestrutura, anotada com JPA) e
 * Tarefa (domínio puro) — o domínio hexagonal nunca vê a entidade JPA.
 * Ver docs/04-hexagonal-service.md.
 */
@Component
public class TarefaJpaAdapter implements TarefaRepository {

	private final com.tesseracode_labs.fundamentos_2semanas.rest.application.TarefaRepository jpaRepository;
	private final RelogioUtcService relogioUtcService;

	public TarefaJpaAdapter(com.tesseracode_labs.fundamentos_2semanas.rest.application.TarefaRepository jpaRepository,
			RelogioUtcService relogioUtcService) {
		this.jpaRepository = jpaRepository;
		this.relogioUtcService = relogioUtcService;
	}

	@Override
	public Optional<Tarefa> buscarPorId(Long id) {
		return jpaRepository.findById(id).map(this::paraDominio);
	}

	@Override
	public void salvar(Tarefa tarefa) {
		TarefaEntity entity = jpaRepository.findById(tarefa.id())
				.orElseThrow(() -> new IllegalStateException("Tarefa não encontrada para atualização: " + tarefa.id()));
		if (tarefa.concluida() && !entity.isConcluida()) {
			entity.concluir(relogioUtcService.agora());
		}
		jpaRepository.save(entity);
	}

	private Tarefa paraDominio(TarefaEntity entity) {
		return new Tarefa(entity.getId(), entity.getTitulo(), entity.isConcluida());
	}
}
