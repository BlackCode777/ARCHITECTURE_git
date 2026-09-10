package com.tesseracodelabs.biblioteca_monolito.livro.infrastructure;

import com.tesseracodelabs.biblioteca_monolito.livro.application.port.LivroConsultaPort;
import com.tesseracodelabs.biblioteca_monolito.livro.domain.FiltroLivro;
import com.tesseracodelabs.biblioteca_monolito.livro.domain.Livro;
import com.tesseracodelabs.biblioteca_monolito.livro.domain.LivroRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.Pagina;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.PedidoPagina;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RecursoNaoEncontradoException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Adapter de saída do módulo {@code livro}: persistência ({@code LivroRepository})
 * e API pública para o módulo {@code aluguel} ({@code LivroConsultaPort}).
 *
 * <p>{@code baixarExemplar}/{@code retornarExemplar} são transacionais aqui
 * porque, no monólito, participam da mesma transação do use case de aluguel.
 */
@Component
class LivroJpaAdapter implements LivroRepository, LivroConsultaPort {

    private final LivroJpaRepository jpa;
    private final LivroPersistenceMapper mapper;

    LivroJpaAdapter(LivroJpaRepository jpa, LivroPersistenceMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    // --- LivroRepository -------------------------------------------------

    @Override
    public Livro salvar(Livro livro) {
        return mapper.paraDominio(jpa.save(mapper.paraEntity(livro)));
    }

    @Override
    public Optional<Livro> buscarPorId(Long id) {
        return jpa.findById(id).map(mapper::paraDominio);
    }

    @Override
    public Pagina<Livro> listar(FiltroLivro filtro, PedidoPagina pedido) {
        var page = jpa.buscarComFiltro(
                filtro.genero(), filtro.autorId(),
                PageRequest.of(pedido.pagina(), pedido.tamanho(), Sort.by("titulo").ascending()));
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
    public boolean existePorIsbn(String isbn, Long ignorarId) {
        return jpa.existePorIsbn(isbn, ignorarId);
    }

    // --- LivroConsultaPort ---------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Optional<LivroDisponibilidade> disponibilidade(Long livroId) {
        return jpa.findById(livroId).map(e -> new LivroDisponibilidade(
                e.getId(),
                e.getExemplaresDisponiveis() > 0,
                e.getExemplaresDisponiveis(),
                e.getGenero(),
                e.getNumeroPaginas()));
    }

    @Override
    @Transactional
    public void baixarExemplar(Long livroId) {
        Livro livro = jpa.findById(livroId).map(mapper::paraDominio)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Livro", livroId));
        livro.baixarExemplar();
        jpa.save(mapper.paraEntity(livro));
    }

    @Override
    @Transactional
    public void retornarExemplar(Long livroId) {
        Livro livro = jpa.findById(livroId).map(mapper::paraDominio)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Livro", livroId));
        livro.retornarExemplar();
        jpa.save(mapper.paraEntity(livro));
    }
}
