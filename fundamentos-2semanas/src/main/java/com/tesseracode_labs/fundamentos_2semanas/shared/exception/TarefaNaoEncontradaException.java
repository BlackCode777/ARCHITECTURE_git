package com.tesseracode_labs.fundamentos_2semanas.shared.exception;

public class TarefaNaoEncontradaException extends RuntimeException {

	public TarefaNaoEncontradaException(Long id) {
		super("Tarefa com ID %d não foi encontrada".formatted(id));
	}
}
