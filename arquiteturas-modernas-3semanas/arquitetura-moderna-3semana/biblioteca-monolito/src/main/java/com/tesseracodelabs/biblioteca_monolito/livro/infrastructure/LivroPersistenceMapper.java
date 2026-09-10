package com.tesseracodelabs.biblioteca_monolito.livro.infrastructure;

import com.tesseracodelabs.biblioteca_monolito.livro.domain.Livro;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct: {@code Livro} (domínio) ↔ {@code LivroEntity} (JPA).
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LivroPersistenceMapper {

    Livro paraDominio(LivroEntity entity);

    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "nova", ignore = true)
    LivroEntity paraEntity(Livro livro);
}
