package com.tesseracodelabs.biblioteca_monolito.autor.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório Spring Data para {@code AutorEntity}. Detalhe de infraestrutura —
 * não é exposto fora do módulo {@code autor}.
 */
interface AutorJpaRepository extends JpaRepository<AutorEntity, Long> {
}
