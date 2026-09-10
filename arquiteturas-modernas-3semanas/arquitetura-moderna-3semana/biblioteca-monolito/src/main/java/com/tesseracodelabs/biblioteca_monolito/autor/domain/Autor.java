package com.tesseracodelabs.biblioteca_monolito.autor.domain;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;

import java.time.LocalDate;

/**
 * Autor de livros — entidade de domínio pura (sem JPA, sem Spring).
 *
 * <p>Invariantes (ver {@code doc/01-DOMINIO-MODELO.md} §3):
 * <ul>
 *   <li>{@code nome} obrigatório</li>
 *   <li>{@code nascimento}, quando informado, no passado</li>
 * </ul>
 *
 * <p>O estado só muda por métodos de negócio — não há setter público
 * (encapsulamento).
 */
public class Autor {

    private Long id;
    private String nome;
    private String nacionalidade;
    private LocalDate nascimento;
    private String biografia;

    /**
     * Reconstrói um autor já persistido (uso do adapter de persistência).
     */
    public Autor(Long id, String nome, String nacionalidade, LocalDate nascimento, String biografia) {
        this.id = id;
        aplicarDados(nome, nacionalidade, nascimento, biografia);
    }

    private Autor(String nome, String nacionalidade, LocalDate nascimento, String biografia) {
        aplicarDados(nome, nacionalidade, nascimento, biografia);
    }

    /**
     * Cria um novo autor ainda não persistido (id nulo).
     */
    public static Autor novo(String nome, String nacionalidade, LocalDate nascimento, String biografia) {
        return new Autor(nome, nacionalidade, nascimento, biografia);
    }

    /**
     * Atualiza os dados mutáveis do autor, revalidando as invariantes.
     */
    public void atualizar(String nome, String nacionalidade, LocalDate nascimento, String biografia) {
        aplicarDados(nome, nacionalidade, nascimento, biografia);
    }

    private void aplicarDados(String nome, String nacionalidade, LocalDate nascimento, String biografia) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("nome do autor e obrigatorio");
        }
        if (nascimento != null && !nascimento.isBefore(LocalDate.now())) {
            throw new RegraNegocioException("nascimento do autor deve estar no passado");
        }
        this.nome = nome.strip();
        this.nacionalidade = nacionalidade == null ? null : nacionalidade.strip();
        this.nascimento = nascimento;
        this.biografia = biografia == null ? null : biografia.strip();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public LocalDate getNascimento() {
        return nascimento;
    }

    public String getBiografia() {
        return biografia;
    }
}
