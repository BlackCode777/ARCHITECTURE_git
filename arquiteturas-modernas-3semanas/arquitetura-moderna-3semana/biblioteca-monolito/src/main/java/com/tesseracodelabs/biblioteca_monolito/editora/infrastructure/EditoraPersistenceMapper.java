package com.tesseracodelabs.biblioteca_monolito.editora.infrastructure;

import com.tesseracodelabs.biblioteca_monolito.editora.domain.Cnpj;
import com.tesseracodelabs.biblioteca_monolito.editora.domain.Editora;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct: {@code Editora} (domínio) ↔ {@code EditoraEntity} (JPA).
 * O CNPJ é value object no domínio e {@code String} na entity.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EditoraPersistenceMapper {

    @Mapping(target = "cnpj", expression = "java(new com.tesseracodelabs.biblioteca_monolito.editora.domain.Cnpj(entity.getCnpj()))")
    Editora paraDominio(EditoraEntity entity);

    @Mapping(target = "cnpj", expression = "java(editora.getCnpj().valor())")
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "nova", ignore = true)
    EditoraEntity paraEntity(Editora editora);

    default String map(Cnpj cnpj) {
        return cnpj == null ? null : cnpj.valor();
    }
}
