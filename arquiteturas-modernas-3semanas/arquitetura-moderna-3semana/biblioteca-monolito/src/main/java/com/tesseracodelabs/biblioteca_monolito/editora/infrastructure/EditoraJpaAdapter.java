package com.tesseracodelabs.biblioteca_monolito.editora.infrastructure;

import com.tesseracodelabs.biblioteca_monolito.editora.application.port.EditoraConsultaPort;
import com.tesseracodelabs.biblioteca_monolito.editora.domain.Editora;
import com.tesseracodelabs.biblioteca_monolito.editora.domain.EditoraRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.Pagina;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.PedidoPagina;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter de saída do módulo {@code editora}: implementa o port de persistência
 * e a API pública cross-módulo.
 */
@Component
class EditoraJpaAdapter implements EditoraRepository, EditoraConsultaPort {

    private final EditoraJpaRepository jpa;
    private final EditoraPersistenceMapper mapper;

    EditoraJpaAdapter(EditoraJpaRepository jpa, EditoraPersistenceMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Editora salvar(Editora editora) {
        return mapper.paraDominio(jpa.save(mapper.paraEntity(editora)));
    }

    @Override
    public Optional<Editora> buscarPorId(Long id) {
        return jpa.findById(id).map(mapper::paraDominio);
    }

    @Override
    public Pagina<Editora> listar(PedidoPagina pedido) {
        var page = jpa.findAll(PageRequest.of(
                pedido.pagina(), pedido.tamanho(), Sort.by("nome").ascending()));
        return new Pagina<>(
                page.getContent().stream().map(mapper::paraDominio).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Override
    public void remover(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existePorId(Long id) {
        return id != null && jpa.existsById(id);
    }

    @Override
    public boolean existePorCnpj(String cnpj, Long ignorarId) {
        return jpa.existePorCnpj(cnpj, ignorarId);
    }

    @Override
    public Optional<EditoraResumo> buscarResumo(Long id) {
        return jpa.findById(id)
                .map(e -> new EditoraResumo(e.getId(), e.getNome(), e.getCidade()));
    }
}
