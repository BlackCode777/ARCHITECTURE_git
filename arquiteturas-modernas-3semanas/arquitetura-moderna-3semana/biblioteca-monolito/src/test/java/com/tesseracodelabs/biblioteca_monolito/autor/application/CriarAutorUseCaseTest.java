package com.tesseracodelabs.biblioteca_monolito.autor.application;

import com.tesseracodelabs.biblioteca_monolito.autor.domain.Autor;
import com.tesseracodelabs.biblioteca_monolito.autor.domain.AutorRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarAutorUseCaseTest {

    @Mock
    AutorRepository autorRepository;

    @InjectMocks
    CriarAutorUseCase useCase;

    @Test
    void cria_e_persiste_o_autor() {
        var comando = new CriarAutorUseCase.Comando(
                "Ursula K. Le Guin", "Americana", LocalDate.of(1929, 10, 21), "bio");
        when(autorRepository.salvar(any(Autor.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Autor criado = useCase.executar(comando);

        ArgumentCaptor<Autor> captor = ArgumentCaptor.forClass(Autor.class);
        verify(autorRepository).salvar(captor.capture());
        assertThat(captor.getValue().getNome()).isEqualTo("Ursula K. Le Guin");
        assertThat(criado.getNacionalidade()).isEqualTo("Americana");
    }

    @Test
    void nao_persiste_quando_dominio_rejeita() {
        var comando = new CriarAutorUseCase.Comando("  ", null, null, null);

        assertThatThrownBy(() -> useCase.executar(comando))
                .isInstanceOf(RegraNegocioException.class);

        verify(autorRepository, never()).salvar(any());
    }
}
