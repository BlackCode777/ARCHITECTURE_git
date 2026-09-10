package com.tesseracodelabs.biblioteca_monolito.aluguel.infrastructure;

import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.StatusAluguel;
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

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Mapeamento JPA de {@code Aluguel}. Tabela {@code aluguel.alugueis}.
 * FK para {@code catalogo.livros(id)} é só coluna + constraint no banco.
 */
@Entity
@Table(name = "alugueis", schema = "aluguel")
public class AluguelEntity extends EntidadeBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alugueis_seq")
    @SequenceGenerator(
            name = "alugueis_seq",
            sequenceName = "aluguel.alugueis_seq",
            allocationSize = EntidadeBase.ALLOCATION_SIZE)
    @Column(name = "id")
    private Long id;

    @Column(name = "livro_id", nullable = false)
    private Long livroId;

    @Column(name = "nome_locatario", nullable = false, length = 200)
    private String nomeLocatario;

    @Column(name = "data_retirada", nullable = false)
    private LocalDate dataRetirada;

    @Column(name = "data_devolucao_prevista", nullable = false)
    private LocalDate dataDevolucaoPrevista;

    @Column(name = "data_devolucao_real")
    private LocalDate dataDevolucaoReal;

    @Column(name = "taxa", nullable = false, precision = 10, scale = 2)
    private BigDecimal taxa;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusAluguel status;

    @Column(name = "politica", nullable = false, length = 30)
    private String politica;

    public AluguelEntity() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLivroId() {
        return livroId;
    }

    public void setLivroId(Long livroId) {
        this.livroId = livroId;
    }

    public String getNomeLocatario() {
        return nomeLocatario;
    }

    public void setNomeLocatario(String nomeLocatario) {
        this.nomeLocatario = nomeLocatario;
    }

    public LocalDate getDataRetirada() {
        return dataRetirada;
    }

    public void setDataRetirada(LocalDate dataRetirada) {
        this.dataRetirada = dataRetirada;
    }

    public LocalDate getDataDevolucaoPrevista() {
        return dataDevolucaoPrevista;
    }

    public void setDataDevolucaoPrevista(LocalDate dataDevolucaoPrevista) {
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }

    public LocalDate getDataDevolucaoReal() {
        return dataDevolucaoReal;
    }

    public void setDataDevolucaoReal(LocalDate dataDevolucaoReal) {
        this.dataDevolucaoReal = dataDevolucaoReal;
    }

    public BigDecimal getTaxa() {
        return taxa;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    public StatusAluguel getStatus() {
        return status;
    }

    public void setStatus(StatusAluguel status) {
        this.status = status;
    }

    public String getPolitica() {
        return politica;
    }

    public void setPolitica(String politica) {
        this.politica = politica;
    }
}
