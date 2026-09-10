package com.tesseracodelabs.biblioteca_monolito.livro.infrastructure;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.GeneroLiterario;
import com.tesseracodelabs.biblioteca_monolito.shared.infrastructure.EntidadeBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * Mapeamento JPA de {@code Livro}. Tabela {@code catalogo.livros}.
 * FKs para {@code autor.autores(id)} e {@code editora.editoras(id)} são apenas
 * colunas + constraint no banco — a entity não referencia as outras entities
 * (regra de fronteira de módulo).
 */
@Entity
@Table(name = "livros", schema = "catalogo")
public class LivroEntity extends EntidadeBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "livros_seq")
    @SequenceGenerator(
            name = "livros_seq",
            sequenceName = "catalogo.livros_seq",
            allocationSize = EntidadeBase.ALLOCATION_SIZE)
    @Column(name = "id")
    private Long id;

    @Column(name = "titulo", nullable = false, length = 300)
    private String titulo;

    @Column(name = "isbn", nullable = false, length = 20, unique = true)
    private String isbn;

    @Column(name = "numero_paginas", nullable = false)
    private int numeroPaginas;

    @Column(name = "ano_publicacao")
    private Integer anoPublicacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "genero", nullable = false, length = 30)
    private GeneroLiterario genero;

    @Column(name = "sinopse", length = 4000)
    private String sinopse;

    @Column(name = "autor_id", nullable = false)
    private Long autorId;

    @Column(name = "editora_id", nullable = false)
    private Long editoraId;

    @Column(name = "exemplares_totais", nullable = false)
    private int exemplaresTotais;

    @Column(name = "exemplares_disponiveis", nullable = false)
    private int exemplaresDisponiveis;

    public LivroEntity() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getNumeroPaginas() {
        return numeroPaginas;
    }

    public void setNumeroPaginas(int numeroPaginas) {
        this.numeroPaginas = numeroPaginas;
    }

    public Integer getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(Integer anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }

    public GeneroLiterario getGenero() {
        return genero;
    }

    public void setGenero(GeneroLiterario genero) {
        this.genero = genero;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public Long getAutorId() {
        return autorId;
    }

    public void setAutorId(Long autorId) {
        this.autorId = autorId;
    }

    public Long getEditoraId() {
        return editoraId;
    }

    public void setEditoraId(Long editoraId) {
        this.editoraId = editoraId;
    }

    public int getExemplaresTotais() {
        return exemplaresTotais;
    }

    public void setExemplaresTotais(int exemplaresTotais) {
        this.exemplaresTotais = exemplaresTotais;
    }

    public int getExemplaresDisponiveis() {
        return exemplaresDisponiveis;
    }

    public void setExemplaresDisponiveis(int exemplaresDisponiveis) {
        this.exemplaresDisponiveis = exemplaresDisponiveis;
    }
}
