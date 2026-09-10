package com.tesseracodelabs.biblioteca_monolito.autor.infrastructure;

import com.tesseracodelabs.biblioteca_monolito.autor.application.port.AutorConsultaPort;
import com.tesseracodelabs.biblioteca_monolito.autor.domain.Autor;
import com.tesseracodelabs.biblioteca_monolito.autor.domain.AutorRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.Pagina;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.PedidoPagina;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter de saída do módulo {@code autor}. Implementa tanto o port de
 * persistência ({@code AutorRepository}) quanto a API pública cross-módulo
 * ({@code AutorConsultaPort}).
 */
@Component
class AutorJpaAdapter implements AutorRepository, AutorConsultaPort {

    private final AutorJpaRepository jpa;
    private final AutorPersistenceMapper mapper;

    AutorJpaAdapter(AutorJpaRepository jpa, AutorPersistenceMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    // --- AutorRepository ---------------------------------------------------

    @Override
    public Autor salvar(Autor autor) {
        AutorEntity entity = mapper.paraEntity(autor);
        return mapper.paraDominio(jpa.save(entity));
    }

    @Override
    public Optional<Autor> buscarPorId(Long id) {
        return jpa.findById(id).map(mapper::paraDominio);
    }

    @Override
    public Pagina<Autor> listar(PedidoPagina pedido) {
        var page = jpa.findAll(PageRequest.of(
                pedido.pagina(), pedido.tamanho(), Sort.by("nome").ascending()));
        return new Pagina<>(
                page.getContent().stream().map(mapper::paraDominio).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements());
    }

    @Override
    public void remover(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existePorId(Long id) {
        return id != null && jpa.existsById(id);
    }

    // --- AutorConsultaPort ------------------------------------------------

    @Override
    public Optional<AutorResumo> buscarResumo(Long id) {
        return jpa.findById(id)
                .map(e -> new AutorResumo(e.getId(), e.getNome(), e.getNacionalidade()));
    }
}
