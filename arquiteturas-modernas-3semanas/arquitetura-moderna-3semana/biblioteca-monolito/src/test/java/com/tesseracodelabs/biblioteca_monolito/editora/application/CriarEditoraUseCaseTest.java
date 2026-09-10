package com.tesseracodelabs.biblioteca_monolito.editora.application;

import com.tesseracodelabs.biblioteca_monolito.editora.domain.Editora;
import com.tesseracodelabs.biblioteca_monolito.editora.domain.EditoraRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.ConflitoException;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarEditoraUseCaseTest {

    @Mock
    EditoraRepository editoraRepository;

    @InjectMocks
    CriarEditoraUseCase useCase;

    @Test
    void cria_editora_com_cnpj_inedito() {
        when(editoraRepository.existePorCnpj(eq("11222333000181"), isNull())).thenReturn(false);
        when(editoraRepository.salvar(any(Editora.class))).thenAnswer(inv -> inv.getArgument(0));

        Editora criada = useCase.executar(new CriarEditoraUseCase.Comando(
                "Companhia das Letras", "11.222.333/0001-81", "Sao Paulo", null));

        assertThat(criada.getCnpj().valor()).isEqualTo("11222333000181");
        verify(editoraRepository).salvar(any(Editora.class));
    }

    @Test
    void recusa_cnpj_duplicado() {
        when(editoraRepository.existePorCnpj(eq("11222333000181"), isNull())).thenReturn(true);

        assertThatThrownBy(() -> useCase.executar(new CriarEditoraUseCase.Comando(
                "Outra", "11222333000181", null, null)))
                .isInstanceOf(ConflitoException.class);

        verify(editoraRepository, never()).salvar(any());
    }

    @Test
    void recusa_cnpj_invalido_antes_de_tocar_o_repositorio() {
        assertThatThrownBy(() -> useCase.executar(new CriarEditoraUseCase.Comando(
                "X", "00000000000000", null, null)))
                .isInstanceOf(RegraNegocioException.class);

        verify(editoraRepository, never()).existePorCnpj(any(), any());
        verify(editoraRepository, never()).salvar(any());
    }
}
