package com.tesseracode_labs.fundamentos_2semanas.hexagonal.domain;

/**
 * Entidade de domínio pura — sem anotações JPA, sem dependência de framework
 * (Dependency Rule). Ver docs/04-hexagonal-service.md.
 */
public class Tarefa {

	private final Long id;
	private String titulo;
	private boolean concluida;

	public Tarefa(Long id, String titulo, boolean concluida) {
		this.id = id;
		this.titulo = titulo;
		this.concluida = concluida;
	}

	public void concluir() {
		this.concluida = true;
	}

	public Long id() {
		return id;
	}

	public String titulo() {
		return titulo;
	}

	public boolean concluida() {
		return concluida;
	}
}
