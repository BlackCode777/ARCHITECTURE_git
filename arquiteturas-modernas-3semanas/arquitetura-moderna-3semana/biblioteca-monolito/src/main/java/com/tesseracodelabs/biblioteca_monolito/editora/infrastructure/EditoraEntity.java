package com.tesseracodelabs.biblioteca_monolito.editora.infrastructure;

import com.tesseracodelabs.biblioteca_monolito.shared.infrastructure.EntidadeBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * Mapeamento JPA de {@code Editora}. Tabela {@code editora.editoras}.
 * CNPJ armazenado como 14 dígitos, com constraint UNIQUE.
 */
@Entity
@Table(name = "editoras", schema = "editora")
public class EditoraEntity extends EntidadeBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "editoras_seq")
    @SequenceGenerator(
            name = "editoras_seq",
            sequenceName = "editora.editoras_seq",
            allocationSize = EntidadeBase.ALLOCATION_SIZE)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome", nullable = false, length = 200)
    private String nome;

    @Column(name = "cnpj", nullable = false, length = 14, unique = true)
    private String cnpj;

    @Column(name = "cidade", length = 100)
    private String cidade;

    @Column(name = "site", length = 200)
    private String site;

    public EditoraEntity() {
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

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
