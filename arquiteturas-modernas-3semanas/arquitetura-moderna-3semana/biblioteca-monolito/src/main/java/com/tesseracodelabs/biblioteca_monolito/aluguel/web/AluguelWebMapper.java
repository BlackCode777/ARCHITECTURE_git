package com.tesseracodelabs.biblioteca_monolito.aluguel.web;

import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.Aluguel;
import com.tesseracodelabs.biblioteca_monolito.aluguel.web.dto.AluguelResponse;
import org.mapstruct.Mapper;

/**
 * MapStruct: domínio {@code Aluguel} → {@code AluguelResponse}.
 */
@Mapper(componentModel = "spring")
public interface AluguelWebMapper {

    AluguelResponse paraResponse(Aluguel aluguel);
}
