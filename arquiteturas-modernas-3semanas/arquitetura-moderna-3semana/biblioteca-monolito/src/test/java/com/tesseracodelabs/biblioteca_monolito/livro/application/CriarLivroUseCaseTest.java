package com.tesseracodelabs.biblioteca_monolito.livro.application;

import com.tesseracodelabs.biblioteca_monolito.autor.application.port.AutorConsultaPort;
import com.tesseracodelabs.biblioteca_monolito.editora.application.port.EditoraConsultaPort;
import com.tesseracodelabs.biblioteca_monolito.livro.domain.Livro;
import com.tesseracodelabs.biblioteca_monolito.livro.domain.LivroRepository;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.ConflitoException;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.GeneroLiterario;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarLivroUseCaseTest {

    @Mock
    LivroRepository livroRepository;
    @Mock
    AutorConsultaPort autorConsulta;
    @Mock
    EditoraConsultaPort editoraConsulta;

    @InjectMocks
    CriarLivroUseCase useCase;

    private CriarLivroUseCase.Comando comando() {
        return new CriarLivroUseCase.Comando("Fundacao", "9788535902772", 244, 1951,
                GeneroLiterario.FICCAO_CIENTIFICA, "sinopse", 1L, 2L, 3);
    }

    @Test
    void cria_livro_quando_autor_e_editora_existem() {
        when(autorConsulta.existePorId(1L)).thenReturn(true);
        when(editoraConsulta.existePorId(2L)).thenReturn(true);
        when(livroRepository.existePorIsbn("9788535902772", null)).thenReturn(false);
        when(livroRepository.salvar(any(Livro.class))).thenAnswer(inv -> inv.getArgument(0));

        Livro livro = useCase.executar(comando());

        assertThat(livro.getTitulo()).isEqualTo("Fundacao");
        assertThat(livro.getExemplaresDisponiveis()).isEqualTo(3);
        verify(livroRepository).salvar(any(Livro.class));
    }

    @Test
    void falha_quando_autor_nao_existe() {
        when(autorConsulta.existePorId(1L)).thenReturn(false);

        assertThatThrownBy(() -> useCase.executar(comando()))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("autor");

        verify(livroRepository, never()).salvar(any());
    }

    @Test
    void falha_quando_editora_nao_existe() {
        when(autorConsulta.existePorId(1L)).thenReturn(true);
        when(editoraConsulta.existePorId(2L)).thenReturn(false);

        assertThatThrownBy(() -> useCase.executar(comando()))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("editora");

        verify(livroRepository, never()).salvar(any());
    }

    @Test
    void falha_quando_isbn_ja_existe() {
        when(autorConsulta.existePorId(1L)).thenReturn(true);
        when(editoraConsulta.existePorId(2L)).thenReturn(true);
        when(livroRepository.existePorIsbn("9788535902772", null)).thenReturn(true);

        assertThatThrownBy(() -> useCase.executar(comando()))
                .isInstanceOf(ConflitoException.class);

        verify(livroRepository, never()).salvar(any());
    }
}
