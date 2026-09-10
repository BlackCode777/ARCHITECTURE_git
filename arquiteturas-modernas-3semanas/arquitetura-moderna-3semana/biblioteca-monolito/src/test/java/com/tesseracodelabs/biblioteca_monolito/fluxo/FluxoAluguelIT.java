package com.tesseracodelabs.biblioteca_monolito.fluxo;

import com.tesseracodelabs.biblioteca_monolito.shared.AbstractPostgresIT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Fluxo completo do monólito exercitando as fronteiras entre os 4 módulos numa
 * única transação por request: cadastra autor e editora, cadastra um livro que
 * os referencia, aluga (taxa calculada pela política), confere a baixa do
 * exemplar e devolve (exemplar volta à circulação).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FluxoAluguelIT extends AbstractPostgresIT {

    @LocalServerPort
    int porta;

    private RestClient rest;

    @BeforeEach
    void configurarCliente() {
        rest = RestClient.create("http://localhost:" + porta);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> post(String path, Map<String, ?> body) {
        return rest.post().uri(path).body(body).retrieve().body(Map.class);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> get(String path) {
        return rest.get().uri(path).retrieve().body(Map.class);
    }

    @Test
    void cadastra_aluga_e_devolve() {
        Object autorId = post("/api/autores",
                Map.of("nome", "Isaac Asimov", "nacionalidade", "Russo-americano")).get("id");

        Object editoraId = post("/api/editoras",
                Map.of("nome", "Aleph", "cnpj", "11.222.333/0001-81", "cidade", "Sao Paulo")).get("id");

        Map<String, Object> livro = post("/api/livros", Map.of(
                "titulo", "Fundacao", "isbn", "9788535902772", "numeroPaginas", 244,
                "anoPublicacao", 1951, "genero", "FICCAO_CIENTIFICA",
                "autorId", autorId, "editoraId", editoraId, "exemplaresTotais", 2));
        Object livroId = livro.get("id");
        assertThat(livro.get("autorNome")).isEqualTo("Isaac Asimov");
        assertThat(livro.get("editoraNome")).isEqualTo("Aleph");
        assertThat(livro.get("exemplaresDisponiveis")).isEqualTo(2);

        Map<String, Object> aluguel = post("/api/alugueis",
                Map.of("livroId", livroId, "nomeLocatario", "Ana", "dias", 7));
        Object aluguelId = aluguel.get("id");
        assertThat(aluguel.get("status")).isEqualTo("ATIVO");
        assertThat(((Number) aluguel.get("taxa")).doubleValue()).isEqualTo(14.0);

        assertThat(get("/api/livros/" + livroId).get("exemplaresDisponiveis")).isEqualTo(1);

        Map<String, Object> devolvido = post("/api/alugueis/" + aluguelId + "/devolucao", Map.of());
        assertThat(devolvido.get("status")).isEqualTo("DEVOLVIDO");

        assertThat(get("/api/livros/" + livroId).get("exemplaresDisponiveis")).isEqualTo(2);
    }

    @Test
    void alugar_livro_sem_exemplar_devolve_422_e_nao_grava_aluguel() {
        Object autorId = post("/api/autores", Map.of("nome", "Autor Unico")).get("id");
        Object editoraId = post("/api/editoras",
                Map.of("nome", "Editora Unica", "cnpj", "45.723.174/0001-10")).get("id");
        Object livroId = post("/api/livros", Map.of(
                "titulo", "So um", "isbn", "isbn-unico-1", "numeroPaginas", 10,
                "genero", "ACAO", "autorId", autorId, "editoraId", editoraId,
                "exemplaresTotais", 1)).get("id");

        post("/api/alugueis", Map.of("livroId", livroId, "nomeLocatario", "AnaU", "dias", 3));

        HttpStatusCode status = rest.post().uri("/api/alugueis")
                .body(Map.of("livroId", livroId, "nomeLocatario", "BeaU", "dias", 3))
                .exchange((req, res) -> res.getStatusCode());
        assertThat(status.value()).isEqualTo(422);

        List<?> alugueisAna = rest.get().uri("/api/alugueis?locatario=AnaU").retrieve().body(List.class);
        assertThat(alugueisAna).hasSize(1);
    }
}
