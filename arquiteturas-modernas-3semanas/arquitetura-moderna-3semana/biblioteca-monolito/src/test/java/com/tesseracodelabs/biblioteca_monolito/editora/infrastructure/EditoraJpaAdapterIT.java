package com.tesseracodelabs.biblioteca_monolito.editora.infrastructure;

import com.tesseracodelabs.biblioteca_monolito.editora.application.port.EditoraConsultaPort;
import com.tesseracodelabs.biblioteca_monolito.editora.domain.Cnpj;
import com.tesseracodelabs.biblioteca_monolito.editora.domain.Editora;
import com.tesseracodelabs.biblioteca_monolito.editora.domain.EditoraRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.AbstractPostgresIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({EditoraJpaAdapter.class, EditoraPersistenceMapperImpl.class})
class EditoraJpaAdapterIT extends AbstractPostgresIT {

    @Autowired
    EditoraRepository repository;

    @Autowired
    EditoraConsultaPort consultaPort;

    @Test
    void salva_e_recarrega_convertendo_cnpj() {
        Editora salva = repository.salvar(Editora.nova(
                "Companhia das Letras", new Cnpj("11222333000181"), "Sao Paulo", null));

        Editora recarregada = repository.buscarPorId(salva.getId()).orElseThrow();
        assertThat(recarregada.getCnpj()).isInstanceOf(Cnpj.class);
        assertThat(recarregada.getCnpj().valor()).isEqualTo("11222333000181");
        assertThat(recarregada.getNome()).isEqualTo("Companhia das Letras");
    }

    @Test
    void existePorCnpj_respeita_ignorarId() {
        Editora salva = repository.salvar(Editora.nova(
                "Aleph", new Cnpj("45723174000110"), "SP", null));

        assertThat(repository.existePorCnpj("45723174000110", null)).isTrue();
        assertThat(repository.existePorCnpj("45723174000110", salva.getId())).isFalse();
        assertThat(repository.existePorCnpj("60746948000112", null)).isFalse();
    }

    @Test
    void consultaPort_devolve_resumo() {
        Editora salva = repository.salvar(Editora.nova(
                "Rocco", new Cnpj("60746948000112"), "Rio de Janeiro", null));

        var resumo = consultaPort.buscarResumo(salva.getId()).orElseThrow();
        assertThat(resumo.nome()).isEqualTo("Rocco");
        assertThat(resumo.cidade()).isEqualTo("Rio de Janeiro");
    }
}
