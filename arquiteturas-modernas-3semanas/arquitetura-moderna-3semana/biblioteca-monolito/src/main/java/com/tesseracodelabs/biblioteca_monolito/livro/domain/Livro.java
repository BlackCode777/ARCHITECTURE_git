package com.tesseracodelabs.biblioteca_monolito.livro.domain;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.GeneroLiterario;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;

import java.time.Year;

/**
 * Livro do catálogo — entidade de domínio pura.
 *
 * <p>Invariantes (ver {@code doc/01-DOMINIO-MODELO.md} §3):
 * <ul>
 *   <li>{@code numeroPaginas} &gt; 0</li>
 *   <li>{@code exemplaresDisponiveis} entre 0 e {@code exemplaresTotais}</li>
 *   <li>{@code isbn} obrigatório (unicidade é do use case + banco)</li>
 *   <li>{@code genero} obrigatório</li>
 *   <li>{@code autorId} e {@code editoraId} obrigatórios (existência é do use case)</li>
 * </ul>
 *
 * <p>Os exemplares só mudam por {@link #baixarExemplar()} / {@link #retornarExemplar()}.
 */
public class Livro {

    private Long id;
    private String titulo;
    private String isbn;
    private int numeroPaginas;
    private Integer anoPublicacao;
    private GeneroLiterario genero;
    private String sinopse;
    private Long autorId;
    private Long editoraId;
    private int exemplaresTotais;
    private int exemplaresDisponiveis;

    /**
     * Reidratação (uso do adapter de persistência) — recebe o estado completo.
     */
    public Livro(Long id, String titulo, String isbn, int numeroPaginas, Integer anoPublicacao,
                 GeneroLiterario genero, String sinopse, Long autorId, Long editoraId,
                 int exemplaresTotais, int exemplaresDisponiveis) {
        this.id = id;
        this.exemplaresDisponiveis = exemplaresDisponiveis;
        aplicarDados(titulo, isbn, numeroPaginas, anoPublicacao, genero, sinopse,
                autorId, editoraId, exemplaresTotais);
        validarExemplares();
    }

    private Livro(String titulo, String isbn, int numeroPaginas, Integer anoPublicacao,
                  GeneroLiterario genero, String sinopse, Long autorId, Long editoraId,
                  int exemplaresTotais) {
        aplicarDados(titulo, isbn, numeroPaginas, anoPublicacao, genero, sinopse,
                autorId, editoraId, exemplaresTotais);
        this.exemplaresDisponiveis = exemplaresTotais;
    }

    /**
     * Cria um novo livro com todos os exemplares disponíveis.
     */
    public static Livro novo(String titulo, String isbn, int numeroPaginas, Integer anoPublicacao,
                             GeneroLiterario genero, String sinopse, Long autorId, Long editoraId,
                             int exemplaresTotais) {
        return new Livro(titulo, isbn, numeroPaginas, anoPublicacao, genero, sinopse,
                autorId, editoraId, exemplaresTotais);
    }

    /**
     * Atualiza os dados cadastrais. Não mexe nos exemplares em circulação:
     * {@code exemplaresTotais} só pode diminuir até o número de exemplares
     * atualmente emprestados.
     */
    public void atualizar(String titulo, String isbn, int numeroPaginas, Integer anoPublicacao,
                          GeneroLiterario genero, String sinopse, Long autorId, Long editoraId,
                          int exemplaresTotais) {
        int emprestados = this.exemplaresTotais - this.exemplaresDisponiveis;
        if (exemplaresTotais < emprestados) {
            throw new RegraNegocioException(
                    "exemplaresTotais (%d) menor que os %d exemplares emprestados".formatted(exemplaresTotais, emprestados));
        }
        aplicarDados(titulo, isbn, numeroPaginas, anoPublicacao, genero, sinopse,
                autorId, editoraId, exemplaresTotais);
        this.exemplaresDisponiveis = exemplaresTotais - emprestados;
    }

    /**
     * Retira um exemplar de circulação (chamado ao criar um aluguel).
     */
    public void baixarExemplar() {
        if (exemplaresDisponiveis <= 0) {
            throw new RegraNegocioException("nao ha exemplares disponiveis do livro " + titulo);
        }
        exemplaresDisponiveis--;
    }

    /**
     * Devolve um exemplar à circulação (chamado ao devolver um aluguel).
     */
    public void retornarExemplar() {
        if (exemplaresDisponiveis >= exemplaresTotais) {
            throw new RegraNegocioException("todos os exemplares do livro " + titulo + " ja estao disponiveis");
        }
        exemplaresDisponiveis++;
    }

    public boolean temExemplarDisponivel() {
        return exemplaresDisponiveis > 0;
    }

    private void aplicarDados(String titulo, String isbn, int numeroPaginas, Integer anoPublicacao,
                              GeneroLiterario genero, String sinopse, Long autorId, Long editoraId,
                              int exemplaresTotais) {
        if (titulo == null || titulo.isBlank()) {
            throw new RegraNegocioException("titulo do livro e obrigatorio");
        }
        if (isbn == null || isbn.isBlank()) {
            throw new RegraNegocioException("isbn do livro e obrigatorio");
        }
        if (numeroPaginas <= 0) {
            throw new RegraNegocioException("numeroPaginas deve ser maior que zero");
        }
        if (genero == null) {
            throw new RegraNegocioException("genero do livro e obrigatorio");
        }
        if (autorId == null) {
            throw new RegraNegocioException("autorId e obrigatorio");
        }
        if (editoraId == null) {
            throw new RegraNegocioException("editoraId e obrigatorio");
        }
        if (exemplaresTotais < 0) {
            throw new RegraNegocioException("exemplaresTotais nao pode ser negativo");
        }
        if (anoPublicacao != null && anoPublicacao > Year.now().getValue()) {
            throw new RegraNegocioException("anoPublicacao nao pode estar no futuro");
        }
        this.titulo = titulo.strip();
        this.isbn = isbn.replaceAll("[\\s-]", "");
        this.numeroPaginas = numeroPaginas;
        this.anoPublicacao = anoPublicacao;
        this.genero = genero;
        this.sinopse = sinopse == null ? null : sinopse.strip();
        this.autorId = autorId;
        this.editoraId = editoraId;
        this.exemplaresTotais = exemplaresTotais;
    }

    private void validarExemplares() {
        if (exemplaresDisponiveis < 0 || exemplaresDisponiveis > exemplaresTotais) {
            throw new RegraNegocioException(
                    "exemplaresDisponiveis (%d) fora do intervalo [0, %d]".formatted(exemplaresDisponiveis, exemplaresTotais));
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getNumeroPaginas() {
        return numeroPaginas;
    }

    public Integer getAnoPublicacao() {
        return anoPublicacao;
    }

    public GeneroLiterario getGenero() {
        return genero;
    }

    public String getSinopse() {
        return sinopse;
    }

    public Long getAutorId() {
        return autorId;
    }

    public Long getEditoraId() {
        return editoraId;
    }

    public int getExemplaresTotais() {
        return exemplaresTotais;
    }

    public int getExemplaresDisponiveis() {
        return exemplaresDisponiveis;
    }
}
