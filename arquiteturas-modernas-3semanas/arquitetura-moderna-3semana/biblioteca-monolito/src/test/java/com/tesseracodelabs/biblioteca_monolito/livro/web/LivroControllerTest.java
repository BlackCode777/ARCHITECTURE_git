package com.tesseracodelabs.biblioteca_monolito.livro.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesseracodelabs.biblioteca_monolito.autor.application.port.AutorConsultaPort;
import com.tesseracodelabs.biblioteca_monolito.editora.application.port.EditoraConsultaPort;
import com.tesseracodelabs.biblioteca_monolito.livro.application.AtualizarLivroUseCase;
import com.tesseracodelabs.biblioteca_monolito.livro.application.BuscarLivroUseCase;
import com.tesseracodelabs.biblioteca_monolito.livro.application.CriarLivroUseCase;
import com.tesseracodelabs.biblioteca_monolito.livro.application.ListarLivrosUseCase;
import com.tesseracodelabs.biblioteca_monolito.livro.application.RemoverLivroUseCase;
import com.tesseracodelabs.biblioteca_monolito.livro.domain.Livro;
import com.tesseracodelabs.biblioteca_monolito.livro.web.dto.CriarLivroRequest;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.GeneroLiterario;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;
import com.tesseracodelabs.biblioteca_monolito.shared.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LivroController.class)
@Import({LivroWebMapperImpl.class, GlobalExceptionHandler.class})
class LivroControllerTest {

    @Autowired
    MockMvc mvc;

    private final ObjectMapper json = new ObjectMapper();

    @MockitoBean CriarLivroUseCase criarLivro;
    @MockitoBean AtualizarLivroUseCase atualizarLivro;
    @MockitoBean BuscarLivroUseCase buscarLivro;
    @MockitoBean ListarLivrosUseCase listarLivros;
    @MockitoBean RemoverLivroUseCase removerLivro;
    @MockitoBean AutorConsultaPort autorConsulta;
    @MockitoBean EditoraConsultaPort editoraConsulta;

    private Livro livroExemplo() {
        return new Livro(10L, "Fundacao", "9788535902772", 244, 1951,
                GeneroLiterario.FICCAO_CIENTIFICA, "s", 1L, 2L, 3, 3);
    }

    @Test
    void cria_livro_resolve_nome_do_autor_e_editora() throws Exception {
        when(criarLivro.executar(any())).thenReturn(livroExemplo());
        when(autorConsulta.buscarResumo(1L)).thenReturn(
                Optional.of(new AutorConsultaPort.AutorResumo(1L, "Isaac Asimov", "Russo")));
        when(editoraConsulta.buscarResumo(2L)).thenReturn(
                Optional.of(new EditoraConsultaPort.EditoraResumo(2L, "Aleph", "SP")));

        var body = new CriarLivroRequest("Fundacao", "9788535902772", 244, 1951,
                GeneroLiterario.FICCAO_CIENTIFICA, "s", 1L, 2L, 3);

        mvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.autorNome").value("Isaac Asimov"))
                .andExpect(jsonPath("$.editoraNome").value("Aleph"))
                .andExpect(jsonPath("$.exemplaresDisponiveis").value(3));
    }

    @Test
    void autor_inexistente_retorna_422() throws Exception {
        when(criarLivro.executar(any())).thenThrow(new RegraNegocioException("autor 9 nao existe"));

        var body = new CriarLivroRequest("X", "123", 10, null,
                GeneroLiterario.ACAO, null, 9L, 2L, 1);

        mvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(body)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Regra de negocio violada"));
    }

    @Test
    void payload_invalido_retorna_400() throws Exception {
        mvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"\",\"isbn\":\"\",\"numeroPaginas\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void busca_livro_por_id() throws Exception {
        when(buscarLivro.executar(10L)).thenReturn(livroExemplo());
        when(autorConsulta.buscarResumo(1L)).thenReturn(
                Optional.of(new AutorConsultaPort.AutorResumo(1L, "Isaac Asimov", null)));
        when(editoraConsulta.buscarResumo(2L)).thenReturn(Optional.empty());

        mvc.perform(get("/api/livros/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Fundacao"))
                .andExpect(jsonPath("$.autorNome").value("Isaac Asimov"))
                .andExpect(jsonPath("$.editoraNome").doesNotExist());
    }
}
