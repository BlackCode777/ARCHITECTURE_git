package com.tesseracodelabs.biblioteca_monolito.aluguel.infrastructure;

import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.Aluguel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct: {@code Aluguel} (domínio) ↔ {@code AluguelEntity} (JPA).
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AluguelPersistenceMapper {

    Aluguel paraDominio(AluguelEntity entity);

    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    AluguelEntity paraEntity(Aluguel aluguel);
}
