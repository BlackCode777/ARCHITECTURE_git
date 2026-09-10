package com.tesseracodelabs.biblioteca_monolito.shared.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * Superclasse mapeada compartilhada pelas entidades JPA (camada
 * {@code infrastructure}) do monólito. O domínio não conhece esta classe.
 *
 * <p>Traz só os carimbos de auditoria. O ID fica em cada entidade concreta,
 * que declara seu próprio {@code @Id} + {@code @SequenceGenerator} apontando
 * para a sequence do seu schema, com {@code allocationSize = }{@link #ALLOCATION_SIZE}
 * (nunca {@code IDENTITY}).
 */
@MappedSuperclass
public abstract class EntidadeBase {

    /**
     * Tamanho do lote de alocação de IDs. Deve bater com o {@code INCREMENT}
     * da sequence no banco (ver migrations {@code V*__schema_*.sql}).
     */
    public static final int ALLOCATION_SIZE = 50;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    protected OffsetDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    protected OffsetDateTime atualizadoEm;

    protected EntidadeBase() {
    }

    /**
     * Cada entidade concreta expõe o próprio ID.
     */
    public abstract Long getId();

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public boolean isNova() {
        return getId() == null;
    }

    /**
     * Igualdade por identidade persistente: mesmo tipo concreto e mesmo ID não
     * nulo. Entidades ainda não persistidas só são iguais a si mesmas.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntidadeBase outra = (EntidadeBase) o;
        return getId() != null && getId().equals(outra.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
