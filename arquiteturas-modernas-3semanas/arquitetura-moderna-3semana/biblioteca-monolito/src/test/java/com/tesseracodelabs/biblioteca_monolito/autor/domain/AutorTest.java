package com.tesseracodelabs.biblioteca_monolito.autor.domain;

import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AutorTest {

    @Test
    void cria_autor_valido() {
        Autor autor = Autor.novo("Isaac Asimov", "Russo-americano",
                LocalDate.of(1920, 1, 2), "Autor de ficcao cientifica.");

        assertThat(autor.getId()).isNull();
        assertThat(autor.getNome()).isEqualTo("Isaac Asimov");
        assertThat(autor.getNacionalidade()).isEqualTo("Russo-americano");
    }

    @Test
    void rejeita_nome_em_branco() {
        assertThatThrownBy(() -> Autor.novo("  ", null, null, null))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("nome");
    }

    @Test
    void rejeita_nascimento_no_futuro() {
        LocalDate amanha = LocalDate.now().plusDays(1);
        assertThatThrownBy(() -> Autor.novo("Fulano", null, amanha, null))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("nascimento");
    }

    @Test
    void aceita_nascimento_nulo() {
        Autor autor = Autor.novo("Anonimo", null, null, null);
        assertThat(autor.getNascimento()).isNull();
    }

    @Test
    void atualizar_revalida_invariantes() {
        Autor autor = Autor.novo("Nome Ok", null, null, null);
        assertThatThrownBy(() -> autor.atualizar("", null, null, null))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void atualizar_troca_os_dados() {
        Autor autor = new Autor(1L, "Antigo", "BR", LocalDate.of(1900, 1, 1), "bio");
        autor.atualizar("Novo Nome", "PT", LocalDate.of(1950, 5, 5), "nova bio");

        assertThat(autor.getId()).isEqualTo(1L);
        assertThat(autor.getNome()).isEqualTo("Novo Nome");
        assertThat(autor.getNacionalidade()).isEqualTo("PT");
        assertThat(autor.getNascimento()).isEqualTo(LocalDate.of(1950, 5, 5));
    }

    @Test
    void normaliza_espacos_das_bordas() {
        Autor autor = Autor.novo("  Jorge Amado  ", "  Brasileiro  ", null, "  bio  ");
        assertThat(autor.getNome()).isEqualTo("Jorge Amado");
        assertThat(autor.getNacionalidade()).isEqualTo("Brasileiro");
        assertThat(autor.getBiografia()).isEqualTo("bio");
    }
}
