package com.tesseracodelabs.biblioteca_monolito.livro.infrastructure;

import com.tesseracodelabs.biblioteca_monolito.autor.infrastructure.AutorEntity;
import com.tesseracodelabs.biblioteca_monolito.editora.infrastructure.EditoraEntity;
import com.tesseracodelabs.biblioteca_monolito.livro.application.port.LivroConsultaPort;
import com.tesseracodelabs.biblioteca_monolito.livro.domain.FiltroLivro;
import com.tesseracodelabs.biblioteca_monolito.livro.domain.Livro;
import com.tesseracodelabs.biblioteca_monolito.livro.domain.LivroRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.AbstractPostgresIT;
import com.tesseracodelabs.biblioteca_monolito.shared.CnpjFake;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.GeneroLiterario;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.PedidoPagina;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({LivroJpaAdapter.class, LivroPersistenceMapperImpl.class})
class LivroJpaAdapterIT extends AbstractPostgresIT {

    @Autowired
    LivroRepository repository;
    @Autowired
    LivroConsultaPort consultaPort;
    @Autowired
    EntityManager em;

    private Long autorId;
    private Long editoraId;

    @BeforeEach
    void semearAutorEEditora() {
        AutorEntity autor = new AutorEntity();
        autor.setNome("Isaac Asimov");
        em.persist(autor);

        EditoraEntity editora = new EditoraEntity();
        editora.setNome("Aleph");
        editora.setCnpj(CnpjFake.proximo());
        em.persist(editora);

        em.flush();
        autorId = autor.getId();
        editoraId = editora.getId();
    }

    // sem hifens/espacos: Livro.novo normaliza o ISBN removendo-os
    private String isbn(String sufixo) {
        return "itlivro%d%s".formatted(autorId, sufixo);
    }

    private Livro livroNovo(String titulo, String isbn, GeneroLiterario genero, int totais) {
        return Livro.novo(titulo, isbn, 200, 2000, genero, "s", autorId, editoraId, totais);
    }

    @Test
    void salva_recarrega_e_persiste_enum_como_string() {
        Livro salvo = repository.salvar(livroNovo("Fundacao", isbn("a"),
                GeneroLiterario.FICCAO_CIENTIFICA, 3));

        Livro recarregado = repository.buscarPorId(salvo.getId()).orElseThrow();
        assertThat(recarregado.getGenero()).isEqualTo(GeneroLiterario.FICCAO_CIENTIFICA);
        assertThat(recarregado.getExemplaresDisponiveis()).isEqualTo(3);
    }

    @Test
    void filtra_por_genero_e_por_autor() {
        repository.salvar(livroNovo("Fic 1", isbn("b"), GeneroLiterario.FICCAO_CIENTIFICA, 1));
        repository.salvar(livroNovo("Terror 1", isbn("c"), GeneroLiterario.TERROR, 1));

        // filtra pelo autor recem-criado para isolar de dados de outros testes
        var doAutorFiccao = repository.listar(
                new FiltroLivro(GeneroLiterario.FICCAO_CIENTIFICA, autorId), new PedidoPagina(0, 50));
        assertThat(doAutorFiccao.itens()).extracting(Livro::getGenero)
                .containsOnly(GeneroLiterario.FICCAO_CIENTIFICA);
        assertThat(doAutorFiccao.itens()).extracting(Livro::getTitulo).containsExactly("Fic 1");

        var doAutor = repository.listar(new FiltroLivro(null, autorId), new PedidoPagina(0, 50));
        assertThat(doAutor.itens()).extracting(Livro::getAutorId).containsOnly(autorId);
        assertThat(doAutor.itens()).hasSize(2);
    }

    @Test
    void existePorIsbn_respeita_ignorarId() {
        String isbn = isbn("d");
        Livro salvo = repository.salvar(livroNovo("Unico", isbn, GeneroLiterario.ACAO, 1));
        assertThat(repository.existePorIsbn(isbn, null)).isTrue();
        assertThat(repository.existePorIsbn(isbn, salvo.getId())).isFalse();
    }

    @Test
    void consultaPort_baixa_e_retorna_exemplar() {
        Livro salvo = repository.salvar(livroNovo("Emprestavel", isbn("e"), GeneroLiterario.AVENTURA, 2));
        Long id = salvo.getId();

        var disp = consultaPort.disponibilidade(id).orElseThrow();
        assertThat(disp.disponivel()).isTrue();
        assertThat(disp.exemplaresDisponiveis()).isEqualTo(2);
        assertThat(disp.genero()).isEqualTo(GeneroLiterario.AVENTURA);

        consultaPort.baixarExemplar(id);
        assertThat(consultaPort.disponibilidade(id).orElseThrow().exemplaresDisponiveis()).isEqualTo(1);

        consultaPort.retornarExemplar(id);
        assertThat(consultaPort.disponibilidade(id).orElseThrow().exemplaresDisponiveis()).isEqualTo(2);
    }
}
