package com.tesseracodelabs.biblioteca_monolito.livro.application;

import com.tesseracodelabs.biblioteca_monolito.autor.application.port.AutorConsultaPort;
import com.tesseracodelabs.biblioteca_monolito.editora.application.port.EditoraConsultaPort;
import com.tesseracodelabs.biblioteca_monolito.livro.domain.Livro;
import com.tesseracodelabs.biblioteca_monolito.livro.domain.LivroRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.ConflitoException;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.GeneroLiterario;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RecursoNaoEncontradoException;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Atualiza um livro existente. Revalida autor/editora e ISBN.
 */
@Service
public class AtualizarLivroUseCase {

    private final LivroRepository livroRepository;
    private final AutorConsultaPort autorConsulta;
    private final EditoraConsultaPort editoraConsulta;

    public AtualizarLivroUseCase(LivroRepository livroRepository,
                                 AutorConsultaPort autorConsulta,
                                 EditoraConsultaPort editoraConsulta) {
        this.livroRepository = livroRepository;
        this.autorConsulta = autorConsulta;
        this.editoraConsulta = editoraConsulta;
    }

    @Transactional
    public Livro executar(Long id, Comando c) {
        Livro livro = livroRepository.buscarPorId(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Livro", id));

        if (!autorConsulta.existePorId(c.autorId())) {
            throw new RegraNegocioException("autor %d nao existe".formatted(c.autorId()));
        }
        if (!editoraConsulta.existePorId(c.editoraId())) {
            throw new RegraNegocioException("editora %d nao existe".formatted(c.editoraId()));
        }

        livro.atualizar(c.titulo(), c.isbn(), c.numeroPaginas(), c.anoPublicacao(),
                c.genero(), c.sinopse(), c.autorId(), c.editoraId(), c.exemplaresTotais());

        if (livroRepository.existePorIsbn(livro.getIsbn(), id)) {
            throw new ConflitoException("ja existe outro livro com o ISBN " + livro.getIsbn());
        }
        return livroRepository.salvar(livro);
    }

    public record Comando(String titulo, String isbn, int numeroPaginas, Integer anoPublicacao,
                          GeneroLiterario genero, String sinopse, Long autorId, Long editoraId,
                          int exemplaresTotais) {
    }
}
