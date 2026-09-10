package com.tesseracode_labs.fundamentos_2semanas.rest.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * Entidade JPA mapeada para a tabela criada em
 * db/migration/V1__create_table_tarefas.sql (SEQUENCE, allocationSize=50).
 */
@Entity
@Table(name = "tarefas")
public class TarefaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tarefas_seq")
	@SequenceGenerator(name = "tarefas_seq", sequenceName = "tarefas_seq", allocationSize = 50)
	private Long id;

	@Column(name = "titulo", nullable = false)
	private String titulo;

	@Column(name = "concluida", nullable = false)
	private boolean concluida;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected TarefaEntity() {
	}

	/**
	 * O timestamp é recebido como parâmetro (não gerado via Instant.now() aqui)
	 * porque a fonte única de tempo do sistema é RelogioUtcService
	 * (docs/05-ntp-service.md) — entidades JPA não recebem injeção de bean
	 * Spring, então a responsabilidade de obter "agora" fica na camada de
	 * aplicação (UseCase), que injeta o serviço e repassa o valor.
	 */
	public TarefaEntity(String titulo, Instant agora) {
		this.titulo = titulo;
		this.concluida = false;
		this.createdAt = agora;
		this.updatedAt = agora;
	}

	public void concluir(Instant agora) {
		this.concluida = true;
		this.updatedAt = agora;
	}

	public void renomear(String novoTitulo, Instant agora) {
		this.titulo = novoTitulo;
		this.updatedAt = agora;
	}

	public Long getId() {
		return id;
	}

	public String getTitulo() {
		return titulo;
	}

	public boolean isConcluida() {
		return concluida;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}
}
