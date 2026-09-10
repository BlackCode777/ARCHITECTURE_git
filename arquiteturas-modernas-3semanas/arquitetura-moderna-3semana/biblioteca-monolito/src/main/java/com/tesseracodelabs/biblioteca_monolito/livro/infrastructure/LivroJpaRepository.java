package com.tesseracodelabs.biblioteca_monolito.livro.infrastructure;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.GeneroLiterario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface LivroJpaRepository extends JpaRepository<LivroEntity, Long> {

    @Query("""
            select l from LivroEntity l
            where (:genero is null or l.genero = :genero)
              and (:autorId is null or l.autorId = :autorId)
            """)
    Page<LivroEntity> buscarComFiltro(@Param("genero") GeneroLiterario genero,
                                      @Param("autorId") Long autorId,
                                      Pageable pageable);

    @Query("""
            select count(l) > 0 from LivroEntity l
            where l.isbn = :isbn and (:ignorarId is null or l.id <> :ignorarId)
            """)
    boolean existePorIsbn(@Param("isbn") String isbn, @Param("ignorarId") Long ignorarId);
}
