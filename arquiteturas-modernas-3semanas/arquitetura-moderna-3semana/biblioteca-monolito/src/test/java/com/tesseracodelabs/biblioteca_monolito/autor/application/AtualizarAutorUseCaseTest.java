package com.tesseracodelabs.biblioteca_monolito.autor.application;

import com.tesseracodelabs.biblioteca_monolito.autor.domain.Autor;
import com.tesseracodelabs.biblioteca_monolito.autor.domain.AutorRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RecursoNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarAutorUseCaseTest {

    @Mock
    AutorRepository autorRepository;

    @InjectMocks
    AtualizarAutorUseCase useCase;

    @Test
    void atualiza_autor_existente() {
        Autor existente = new Autor(7L, "Antigo", "BR", LocalDate.of(1900, 1, 1), null);
        when(autorRepository.buscarPorId(7L)).thenReturn(Optional.of(existente));
        when(autorRepository.salvar(any(Autor.class))).thenAnswer(inv -> inv.getArgument(0));

        var comando = new AtualizarAutorUseCase.Comando("Novo", "PT", null, "bio");
        Autor atualizado = useCase.executar(7L, comando);

        assertThat(atualizado.getNome()).isEqualTo("Novo");
        assertThat(atualizado.getId()).isEqualTo(7L);
        verify(autorRepository).salvar(existente);
    }

    @Test
    void falha_quando_autor_nao_existe() {
        when(autorRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar(99L,
                new AtualizarAutorUseCase.Comando("X", null, null, null)))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(autorRepository, never()).salvar(any());
    }
}
