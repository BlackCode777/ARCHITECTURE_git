package com.tesseracodelabs.biblioteca_monolito.aluguel.infrastructure;

import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.StatusAluguel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

interface AluguelJpaRepository extends JpaRepository<AluguelEntity, Long> {

    List<AluguelEntity> findByNomeLocatarioIgnoreCaseOrderByDataRetiradaDesc(String nomeLocatario);

    @Query("""
            select a from AluguelEntity a
            where a.status in :status and a.dataDevolucaoPrevista < :referencia
            order by a.dataDevolucaoPrevista asc
            """)
    List<AluguelEntity> buscarAtrasados(@Param("status") List<StatusAluguel> status,
                                        @Param("referencia") LocalDate referencia);
}
