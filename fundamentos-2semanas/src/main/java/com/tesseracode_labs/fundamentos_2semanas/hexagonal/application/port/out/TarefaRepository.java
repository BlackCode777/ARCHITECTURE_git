package com.tesseracode_labs.fundamentos_2semanas.hexagonal.application.port.out;

import com.tesseracode_labs.fundamentos_2semanas.hexagonal.domain.Tarefa;
import java.util.Optional;

/** Port de saída (driven port) — contrato implementado pelo adapter de persistência. */
public interface TarefaRepository {

	Optional<Tarefa> buscarPorId(Long id);

	void salvar(Tarefa tarefa);
}
