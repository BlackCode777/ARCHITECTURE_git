package com.tesseracodelabs.biblioteca_monolito.autor.application;

import com.tesseracodelabs.biblioteca_monolito.autor.domain.AutorRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RecursoNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverAutorUseCaseTest {

    @Mock
    AutorRepository autorRepository;

    @InjectMocks
    RemoverAutorUseCase useCase;

    @Test
    void remove_autor_existente() {
        when(autorRepository.existePorId(5L)).thenReturn(true);

        useCase.executar(5L);

        verify(autorRepository).remover(5L);
    }

    @Test
    void falha_quando_autor_nao_existe() {
        when(autorRepository.existePorId(5L)).thenReturn(false);

        assertThatThrownBy(() -> useCase.executar(5L))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(autorRepository, never()).remover(5L);
    }
}
