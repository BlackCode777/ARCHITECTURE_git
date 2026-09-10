package com.tesseracodelabs.biblioteca_monolito.editora.domain;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;

/**
 * Editora — entidade de domínio pura (sem JPA, sem Spring).
 *
 * <p>Invariantes (ver {@code doc/01-DOMINIO-MODELO.md} §3):
 * <ul>
 *   <li>{@code nome} obrigatório</li>
 *   <li>{@code cnpj} válido (14 dígitos + DV) — {@link Cnpj}</li>
 * </ul>
 * Unicidade do CNPJ é responsabilidade do use case + constraint no banco.
 */
public class Editora {

    private Long id;
    private String nome;
    private Cnpj cnpj;
    private String cidade;
    private String site;

    public Editora(Long id, String nome, Cnpj cnpj, String cidade, String site) {
        this.id = id;
        aplicarDados(nome, cnpj, cidade, site);
    }

    private Editora(String nome, Cnpj cnpj, String cidade, String site) {
        aplicarDados(nome, cnpj, cidade, site);
    }

    public static Editora nova(String nome, Cnpj cnpj, String cidade, String site) {
        return new Editora(nome, cnpj, cidade, site);
    }

    public void atualizar(String nome, Cnpj cnpj, String cidade, String site) {
        aplicarDados(nome, cnpj, cidade, site);
    }

    private void aplicarDados(String nome, Cnpj cnpj, String cidade, String site) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("nome da editora e obrigatorio");
        }
        if (cnpj == null) {
            throw new RegraNegocioException("cnpj da editora e obrigatorio");
        }
        this.nome = nome.strip();
        this.cnpj = cnpj;
        this.cidade = cidade == null ? null : cidade.strip();
        this.site = site == null ? null : site.strip();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Cnpj getCnpj() {
        return cnpj;
    }

    public String getCidade() {
        return cidade;
    }

    public String getSite() {
        return site;
    }
}
