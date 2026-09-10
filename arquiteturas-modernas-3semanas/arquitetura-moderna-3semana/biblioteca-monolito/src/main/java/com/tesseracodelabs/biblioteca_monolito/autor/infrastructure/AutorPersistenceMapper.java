package com.tesseracodelabs.biblioteca_monolito.autor.infrastructure;

import com.tesseracodelabs.biblioteca_monolito.autor.domain.Autor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct: {@code Autor} (domínio) ↔ {@code AutorEntity} (JPA).
 * Compile-time — sem reflexão em runtime, sem Lombok.
 *
 * <p>Domínio → entity: os carimbos de auditoria ({@code criadoEm} /
 * {@code atualizadoEm}) são geridos pelo Hibernate, então ficam ignorados aqui.
 * Entity → domínio: MapStruct usa o construtor de reidratação de {@code Autor}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AutorPersistenceMapper {

    Autor paraDominio(AutorEntity entity);

    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "nova", ignore = true)
    AutorEntity paraEntity(Autor autor);
}
