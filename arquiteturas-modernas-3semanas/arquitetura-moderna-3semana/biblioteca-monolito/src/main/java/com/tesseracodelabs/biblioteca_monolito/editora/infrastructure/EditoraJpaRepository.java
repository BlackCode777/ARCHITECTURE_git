package com.tesseracodelabs.biblioteca_monolito.editora.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface EditoraJpaRepository extends JpaRepository<EditoraEntity, Long> {

    @Query("""
            select count(e) > 0 from EditoraEntity e
            where e.cnpj = :cnpj and (:ignorarId is null or e.id <> :ignorarId)
            """)
    boolean existePorCnpj(@Param("cnpj") String cnpj, @Param("ignorarId") Long ignorarId);
}
