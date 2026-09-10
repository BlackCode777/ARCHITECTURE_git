package com.tesseracodelabs.biblioteca_monolito.aluguel.domain;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Aluguel de um livro por um locatário. Entidade de domínio pura.
 *
 * <p>Invariantes (ver {@code doc/01-DOMINIO-MODELO.md} §3):
 * <ul>
 *   <li>{@code dataDevolucaoPrevista} &gt; {@code dataRetirada}</li>
 *   <li>{@code taxa} &gt;= 0 (calculada por uma política — nunca informada pelo cliente)</li>
 * </ul>
 *
 * <p>Estado encapsulado: {@link #devolver(LocalDate)} e {@link #marcarAtraso(LocalDate)}
 * são as únicas formas de mudar {@code status} / {@code dataDevolucaoReal}.
 */
public class Aluguel {

    private Long id;
    private final Long livroId;
    private final String nomeLocatario;
    private final LocalDate dataRetirada;
    private final LocalDate dataDevolucaoPrevista;
    private LocalDate dataDevolucaoReal;
    private final BigDecimal taxa;
    private StatusAluguel status;
    private final String politica;

    /**
     * Reidratação (uso do adapter de persistência).
     */
    public Aluguel(Long id, Long livroId, String nomeLocatario, LocalDate dataRetirada,
                   LocalDate dataDevolucaoPrevista, LocalDate dataDevolucaoReal,
                   BigDecimal taxa, StatusAluguel status, String politica) {
        this.id = id;
        this.livroId = livroId;
        this.nomeLocatario = nomeLocatario;
        this.dataRetirada = dataRetirada;
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
        this.dataDevolucaoReal = dataDevolucaoReal;
        this.taxa = taxa;
        this.status = status;
        this.politica = politica;
    }

    private Aluguel(Long livroId, String nomeLocatario, LocalDate dataRetirada,
                    LocalDate dataDevolucaoPrevista, BigDecimal taxa, String politica) {
        if (livroId == null) {
            throw new RegraNegocioException("livroId e obrigatorio");
        }
        if (nomeLocatario == null || nomeLocatario.isBlank()) {
            throw new RegraNegocioException("nome do locatario e obrigatorio");
        }
        if (dataRetirada == null || dataDevolucaoPrevista == null
                || !dataDevolucaoPrevista.isAfter(dataRetirada)) {
            throw new RegraNegocioException("devolucao prevista deve ser posterior a retirada");
        }
        if (taxa == null || taxa.signum() < 0) {
            throw new RegraNegocioException("taxa nao pode ser negativa");
        }
        this.livroId = livroId;
        this.nomeLocatario = nomeLocatario.strip();
        this.dataRetirada = dataRetirada;
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
        this.taxa = taxa;
        this.status = StatusAluguel.ATIVO;
        this.politica = politica;
    }

    /**
     * Abre um novo aluguel (status {@code ATIVO}).
     */
    public static Aluguel abrir(Long livroId, String nomeLocatario, LocalDate dataRetirada,
                                LocalDate dataDevolucaoPrevista, BigDecimal taxa, String politica) {
        return new Aluguel(livroId, nomeLocatario, dataRetirada, dataDevolucaoPrevista, taxa, politica);
    }

    /**
     * Registra a devolução do livro.
     *
     * @param quando data efetiva da devolução (não pode ser anterior à retirada)
     */
    public void devolver(LocalDate quando) {
        if (status == StatusAluguel.DEVOLVIDO) {
            throw new RegraNegocioException("aluguel ja foi devolvido");
        }
        if (quando == null || quando.isBefore(dataRetirada)) {
            throw new RegraNegocioException("data de devolucao invalida");
        }
        this.dataDevolucaoReal = quando;
        this.status = StatusAluguel.DEVOLVIDO;
    }

    /**
     * Marca o aluguel como {@code ATRASADO} se ainda estiver ativo e a data de
     * referência já passou da devolução prevista.
     *
     * @return true se o status mudou
     */
    public boolean marcarAtraso(LocalDate referencia) {
        if (status == StatusAluguel.ATIVO && referencia.isAfter(dataDevolucaoPrevista)) {
            this.status = StatusAluguel.ATRASADO;
            return true;
        }
        return false;
    }

    public boolean estaEmAberto() {
        return status == StatusAluguel.ATIVO || status == StatusAluguel.ATRASADO;
    }

    public Long getId() {
        return id;
    }

    public Long getLivroId() {
        return livroId;
    }

    public String getNomeLocatario() {
        return nomeLocatario;
    }

    public LocalDate getDataRetirada() {
        return dataRetirada;
    }

    public LocalDate getDataDevolucaoPrevista() {
        return dataDevolucaoPrevista;
    }

    public LocalDate getDataDevolucaoReal() {
        return dataDevolucaoReal;
    }

    public BigDecimal getTaxa() {
        return taxa;
    }

    public StatusAluguel getStatus() {
        return status;
    }

    public String getPolitica() {
        return politica;
    }
}
