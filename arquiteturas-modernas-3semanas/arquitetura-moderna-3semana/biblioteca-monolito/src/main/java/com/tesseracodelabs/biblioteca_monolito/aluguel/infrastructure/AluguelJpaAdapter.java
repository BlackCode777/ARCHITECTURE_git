package com.tesseracodelabs.biblioteca_monolito.aluguel.infrastructure;

import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.Aluguel;
import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.AluguelRepository;
import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.StatusAluguel;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Adapter de saída do módulo {@code aluguel}. O aluguel não expõe API pública
 * para outros módulos — só implementa seu port de persistência.
 */
@Component
class AluguelJpaAdapter implements AluguelRepository {

    private final AluguelJpaRepository jpa;
    private final AluguelPersistenceMapper mapper;

    AluguelJpaAdapter(AluguelJpaRepository jpa, AluguelPersistenceMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Aluguel salvar(Aluguel aluguel) {
        return mapper.paraDominio(jpa.save(mapper.paraEntity(aluguel)));
    }

    @Override
    public Optional<Aluguel> buscarPorId(Long id) {
        return jpa.findById(id).map(mapper::paraDominio);
    }

    @Override
    public List<Aluguel> listarPorLocatario(String nomeLocatario) {
        return jpa.findByNomeLocatarioIgnoreCaseOrderByDataRetiradaDesc(nomeLocatario)
                .stream().map(mapper::paraDominio).toList();
    }

    @Override
    public List<Aluguel> listarAtrasados(LocalDate referencia) {
        return jpa.buscarAtrasados(List.of(StatusAluguel.ATIVO, StatusAluguel.ATRASADO), referencia)
                .stream().map(mapper::paraDominio).toList();
    }
}
