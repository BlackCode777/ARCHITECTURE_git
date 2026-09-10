package com.tesseracodelabs.biblioteca_monolito.aluguel.infrastructure;

import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.Aluguel;
import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.AluguelRepository;
import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.StatusAluguel;
import com.tesseracodelabs.biblioteca_monolito.autor.infrastructure.AutorEntity;
import com.tesseracodelabs.biblioteca_monolito.editora.infrastructure.EditoraEntity;
import com.tesseracodelabs.biblioteca_monolito.livro.infrastructure.LivroEntity;
import com.tesseracodelabs.biblioteca_monolito.shared.AbstractPostgresIT;
import com.tesseracodelabs.biblioteca_monolito.shared.CnpjFake;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.GeneroLiterario;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({AluguelJpaAdapter.class, AluguelPersistenceMapperImpl.class})
class AluguelJpaAdapterIT extends AbstractPostgresIT {

    @Autowired
    AluguelRepository repository;
    @Autowired
    EntityManager em;

    private Long livroId;

    @BeforeEach
    void semearLivro() {
        AutorEntity autor = new AutorEntity();
        autor.setNome("Autor X");
        em.persist(autor);

        EditoraEntity editora = new EditoraEntity();
        editora.setNome("Editora Y");
        editora.setCnpj(CnpjFake.proximo());
        em.persist(editora);

        LivroEntity livro = new LivroEntity();
        livro.setTitulo("Livro Z");
        livro.setIsbn("isbn-" + System.nanoTime());
        livro.setNumeroPaginas(100);
        livro.setGenero(GeneroLiterario.ROMANCE);
        livro.setAutorId(autor.getId());
        livro.setEditoraId(editora.getId());
        livro.setExemplaresTotais(3);
        livro.setExemplaresDisponiveis(3);
        em.persist(livro);

        em.flush();
        livroId = livro.getId();
    }

    private Aluguel novoAluguel(String locatario, LocalDate retirada, LocalDate prevista) {
        return Aluguel.abrir(livroId, locatario, retirada, prevista, new BigDecimal("14.00"), "PADRAO");
    }

    @Test
    void salva_e_recarrega_com_enum_e_taxa() {
        Aluguel salvo = repository.salvar(novoAluguel("Ana",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 8)));

        Aluguel recarregado = repository.buscarPorId(salvo.getId()).orElseThrow();
        assertThat(recarregado.getStatus()).isEqualTo(StatusAluguel.ATIVO);
        assertThat(recarregado.getTaxa()).isEqualByComparingTo("14.00");
        assertThat(recarregado.getNomeLocatario()).isEqualTo("Ana");
    }

    @Test
    void lista_por_locatario_ignorando_caixa() {
        repository.salvar(novoAluguel("Ana Silva", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 5)));
        repository.salvar(novoAluguel("ana silva", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 5)));

        assertThat(repository.listarPorLocatario("ANA SILVA")).hasSize(2);
    }

    @Test
    void lista_atrasados_so_traz_em_aberto_e_vencidos() {
        Aluguel vencidoAtivo = novoAluguel("Atrasado", LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 8));
        repository.salvar(vencidoAtivo);

        Aluguel devolvido = novoAluguel("Devolveu", LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 8));
        devolvido.devolver(LocalDate.of(2020, 1, 7));
        repository.salvar(devolvido);

        Aluguel noPrazo = novoAluguel("NoPrazo", LocalDate.now().minusDays(1), LocalDate.now().plusDays(10));
        repository.salvar(noPrazo);

        var atrasados = repository.listarAtrasados(LocalDate.now());
        assertThat(atrasados).extracting(Aluguel::getNomeLocatario).containsExactly("Atrasado");
    }
}
