package com.tesseracodelabs.biblioteca_monolito.autor.infrastructure;

import com.tesseracodelabs.biblioteca_monolito.shared.infrastructure.EntidadeBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Mapeamento JPA de {@code Autor}. Tabela {@code autor.autores}.
 * ID por {@code SEQUENCE} {@code autor.autores_seq} (allocationSize = 50).
 *
 * <p>Objeto de infraestrutura: tem setters para o MapStruct e o Hibernate.
 * O encapsulamento das invariantes fica na entidade de domínio {@code Autor}.
 */
@Entity
@Table(name = "autores", schema = "autor")
public class AutorEntity extends EntidadeBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "autores_seq")
    @SequenceGenerator(
            name = "autores_seq",
            sequenceName = "autor.autores_seq",
            allocationSize = EntidadeBase.ALLOCATION_SIZE)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Column(name = "nacionalidade", length = 100)
    private String nacionalidade;

    @Column(name = "nascimento")
    private LocalDate nascimento;

    @Column(name = "biografia", length = 4000)
    private String biografia;

    public AutorEntity() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public LocalDate getNascimento() {
        return nascimento;
    }

    public void setNascimento(LocalDate nascimento) {
        this.nascimento = nascimento;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }
}
