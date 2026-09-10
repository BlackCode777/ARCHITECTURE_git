package com.tesseracodelabs.biblioteca_monolito.aluguel.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tesseracodelabs.biblioteca_monolito.aluguel.application.AlugarUseCase;
import com.tesseracodelabs.biblioteca_monolito.aluguel.application.BuscarAluguelUseCase;
import com.tesseracodelabs.biblioteca_monolito.aluguel.application.DevolverUseCase;
import com.tesseracodelabs.biblioteca_monolito.aluguel.application.ListarAlugueisPorLocatarioUseCase;
import com.tesseracodelabs.biblioteca_monolito.aluguel.application.ListarAtrasadosUseCase;
import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.Aluguel;
import com.tesseracodelabs.biblioteca_monolito.aluguel.domain.StatusAluguel;
import com.tesseracodelabs.biblioteca_monolito.shared.domain.RegraNegocioException;
import com.tesseracodelabs.biblioteca_monolito.shared.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AluguelController.class)
@Import({AluguelWebMapperImpl.class, GlobalExceptionHandler.class})
class AluguelControllerTest {

    @Autowired
    MockMvc mvc;

    private final ObjectMapper json = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean AlugarUseCase alugar;
    @MockitoBean DevolverUseCase devolver;
    @MockitoBean BuscarAluguelUseCase buscar;
    @MockitoBean ListarAlugueisPorLocatarioUseCase listarPorLocatario;
    @MockitoBean ListarAtrasadosUseCase listarAtrasados;

    private Aluguel exemplo(StatusAluguel status) {
        return new Aluguel(1L, 10L, "Ana",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 8),
                status == StatusAluguel.DEVOLVIDO ? LocalDate.of(2026, 1, 6) : null,
                new BigDecimal("14.00"), status, "PADRAO");
    }

    @Test
    void aluga_retorna_201_com_taxa_calculada() throws Exception {
        when(alugar.executar(any())).thenReturn(exemplo(StatusAluguel.ATIVO));

        mvc.perform(post("/api/alugueis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"livroId\":10,\"nomeLocatario\":\"Ana\",\"dias\":7}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taxa").value(14.00))
                .andExpect(jsonPath("$.status").value("ATIVO"))
                .andExpect(jsonPath("$.politica").value("PADRAO"));
    }

    @Test
    void aluga_livro_indisponivel_retorna_422() throws Exception {
        when(alugar.executar(any())).thenThrow(new RegraNegocioException("livro 10 nao tem exemplares disponiveis"));

        mvc.perform(post("/api/alugueis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"livroId\":10,\"nomeLocatario\":\"Ana\",\"dias\":7}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void aluga_payload_invalido_retorna_400() throws Exception {
        mvc.perform(post("/api/alugueis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeLocatario\":\"\",\"dias\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void devolucao_retorna_200_com_status_devolvido() throws Exception {
        when(devolver.executar(eq(1L), any())).thenReturn(exemplo(StatusAluguel.DEVOLVIDO));

        mvc.perform(post("/api/alugueis/1/devolucao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DEVOLVIDO"))
                .andExpect(jsonPath("$.dataDevolucaoReal").value("2026-01-06"));
    }
}
