package com.tesseracode_labs.fundamentos_2semanas.hexagonal.application.port.in;

/** Port de entrada (driving port) — contrato exposto ao adapter web. */
public interface ConcluirTarefaPort {

	void concluir(Long tarefaId);
}
