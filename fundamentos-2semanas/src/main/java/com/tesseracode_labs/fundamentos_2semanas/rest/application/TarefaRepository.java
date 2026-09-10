package com.tesseracode_labs.fundamentos_2semanas.rest.application;

import com.tesseracode_labs.fundamentos_2semanas.rest.domain.TarefaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarefaRepository extends JpaRepository<TarefaEntity, Long> {
}
