package com.tesseracodelabs.biblioteca_monolito.autor.infrastructure;

import com.tesseracodelabs.biblioteca_monolito.autor.application.port.AutorConsultaPort;
import com.tesseracodelabs.biblioteca_monolito.autor.domain.Autor;
import com.tesseracodelabs.biblioteca_monolito.autor.domain.AutorRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.AbstractPostgresIT;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.Pagina;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.PedidoPagina;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({AutorJpaAdapter.class, AutorPersistenceMapperImpl.class})
class AutorJpaAdapterIT extends AbstractPostgresIT {

    @Autowired
    AutorRepository repository;

    @Autowired
    AutorConsultaPort consultaPort;

    @Test
    void salva_e_recarrega_com_id_e_auditoria() {
        Autor salvo = repository.salvar(Autor.novo(
                "Machado de Assis", "Brasileiro", LocalDate.of(1839, 6, 21), "Realista."));

        assertThat(salvo.getId()).isNotNull();

        Autor recarregado = repository.buscarPorId(salvo.getId()).orElseThrow();
        assertThat(recarregado.getNome()).isEqualTo("Machado de Assis");
        assertThat(recarregado.getNascimento()).isEqualTo(LocalDate.of(1839, 6, 21));
    }

    @Test
    void lista_paginado_ordenado_por_nome() {
        repository.salvar(Autor.novo("Zzz Ultimo", "BR", null, null));
        repository.salvar(Autor.novo("Aaa Primeiro", "BR", null, null));

        Pagina<Autor> pagina = repository.listar(new PedidoPagina(0, 50));

        assertThat(pagina.itens()).extracting(Autor::getNome)
                .contains("Aaa Primeiro", "Zzz Ultimo");
        assertThat(pagina.itens())
                .extracting(Autor::getNome)
                .isSortedAccordingTo(String::compareTo);
        assertThat(pagina.totalItens()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void existePorId_e_remover() {
        Autor salvo = repository.salvar(Autor.novo("Temp", null, null, null));
        Long id = salvo.getId();

        assertThat(repository.existePorId(id)).isTrue();

        repository.remover(id);

        assertThat(repository.existePorId(id)).isFalse();
        assertThat(repository.buscarPorId(id)).isEmpty();
    }

    @Test
    void consultaPort_devolve_resumo() {
        Autor salvo = repository.salvar(Autor.novo("Clarice Lispector", "Brasileira", null, null));

        var resumo = consultaPort.buscarResumo(salvo.getId()).orElseThrow();
        assertThat(resumo.nome()).isEqualTo("Clarice Lispector");
        assertThat(resumo.nacionalidade()).isEqualTo("Brasileira");
        assertThat(consultaPort.existePorId(salvo.getId())).isTrue();
    }
}
