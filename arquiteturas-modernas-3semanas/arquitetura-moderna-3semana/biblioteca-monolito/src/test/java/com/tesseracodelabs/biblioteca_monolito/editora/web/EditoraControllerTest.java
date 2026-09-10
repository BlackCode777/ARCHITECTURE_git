package com.tesseracodelabs.biblioteca_monolito.editora.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesseracodelabs.biblioteca_monolito.editora.application.AtualizarEditoraUseCase;
import com.tesseracodelabs.biblioteca_monolito.editora.application.BuscarEditoraUseCase;
import com.tesseracodelabs.biblioteca_monolito.editora.application.CriarEditoraUseCase;
import com.tesseracodelabs.biblioteca_monolito.editora.application.ListarEditorasUseCase;
import com.tesseracodelabs.biblioteca_monolito.editora.application.RemoverEditoraUseCase;
import com.tesseracodelabs.biblioteca_monolito.editora.domain.Cnpj;
import com.tesseracodelabs.biblioteca_monolito.editora.domain.Editora;
import com.tesseracodelabs.biblioteca_monolito.editora.web.dto.CriarEditoraRequest;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.ConflitoException;
import com.tesseracodelabs.biblioteca_monolito.shared.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EditoraController.class)
@Import({EditoraWebMapperImpl.class, GlobalExceptionHandler.class})
class EditoraControllerTest {

    @Autowired
    MockMvc mvc;

    private final ObjectMapper json = new ObjectMapper();

    @MockitoBean
    CriarEditoraUseCase criarEditora;
    @MockitoBean
    AtualizarEditoraUseCase atualizarEditora;
    @MockitoBean
    BuscarEditoraUseCase buscarEditora;
    @MockitoBean
    ListarEditorasUseCase listarEditoras;
    @MockitoBean
    RemoverEditoraUseCase removerEditora;

    @Test
    void cria_editora_retorna_201_com_cnpj_formatado() throws Exception {
        when(criarEditora.executar(any())).thenReturn(new Editora(
                1L, "Companhia das Letras", new Cnpj("11222333000181"), "Sao Paulo", null));

        var body = new CriarEditoraRequest("Companhia das Letras", "11222333000181", "Sao Paulo", null);

        mvc.perform(post("/api/editoras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cnpj").value("11.222.333/0001-81"));
    }

    @Test
    void cnpj_duplicado_retorna_409() throws Exception {
        when(criarEditora.executar(any())).thenThrow(new ConflitoException("cnpj duplicado"));

        var body = new CriarEditoraRequest("X", "11222333000181", null, null);

        mvc.perform(post("/api/editoras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito de estado"));
    }

    @Test
    void payload_sem_nome_retorna_400() throws Exception {
        mvc.perform(post("/api/editoras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cnpj\":\"11222333000181\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros[0].campo").value("nome"));
    }
}
