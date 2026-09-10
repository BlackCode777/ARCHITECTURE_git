package com.tesseracodelabs.biblioteca_monolito.autor.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tesseracodelabs.biblioteca_monolito.autor.application.AtualizarAutorUseCase;
import com.tesseracodelabs.biblioteca_monolito.autor.application.BuscarAutorUseCase;
import com.tesseracodelabs.biblioteca_monolito.autor.application.CriarAutorUseCase;
import com.tesseracodelabs.biblioteca_monolito.autor.application.ListarAutoresUseCase;
import com.tesseracodelabs.biblioteca_monolito.autor.application.RemoverAutorUseCase;
import com.tesseracodelabs.biblioteca_monolito.autor.domain.Autor;
import com.tesseracodelabs.biblioteca_monolito.autor.web.dto.CriarAutorRequest;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RecursoNaoEncontradoException;
import com.tesseracodelabs.biblioteca_monolito.shared.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AutorController.class)
@Import({AutorWebMapperImpl.class, GlobalExceptionHandler.class})
class AutorControllerTest {

    @Autowired
    MockMvc mvc;

    private final ObjectMapper json = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    CriarAutorUseCase criarAutor;
    @MockitoBean
    AtualizarAutorUseCase atualizarAutor;
    @MockitoBean
    BuscarAutorUseCase buscarAutor;
    @MockitoBean
    ListarAutoresUseCase listarAutores;
    @MockitoBean
    RemoverAutorUseCase removerAutor;

    @Test
    void cria_autor_retorna_201_e_location() throws Exception {
        when(criarAutor.executar(any())).thenReturn(
                new Autor(42L, "Cora Coralina", "Brasileira", LocalDate.of(1889, 8, 20), null));

        var body = new CriarAutorRequest("Cora Coralina", "Brasileira", LocalDate.of(1889, 8, 20), null);

        mvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/autores/42"))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.nome").value("Cora Coralina"));
    }

    @Test
    void cria_autor_invalido_retorna_400_problem_detail() throws Exception {
        mvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("nacionalidade", "BR"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Requisicao invalida"))
                .andExpect(jsonPath("$.erros[0].campo").value("nome"));
    }

    @Test
    void buscar_inexistente_retorna_404_problem_detail() throws Exception {
        when(buscarAutor.executar(eq(9L))).thenThrow(RecursoNaoEncontradoException.de("Autor", 9L));

        mvc.perform(get("/api/autores/9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso nao encontrado"))
                .andExpect(jsonPath("$.detail").value("Autor 9 nao encontrado"));
    }

    @Test
    void remover_retorna_204() throws Exception {
        mvc.perform(delete("/api/autores/3"))
                .andExpect(status().isNoContent());
        verify(removerAutor).executar(3L);
    }
}
