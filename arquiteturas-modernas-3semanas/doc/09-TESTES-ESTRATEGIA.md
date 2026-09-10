# 09 — Estratégia de Testes

> Pirâmide de testes aplicada aos dois projetos. Ferramentas: JUnit 5, Mockito,
> AssertJ, Spring Boot Test, Testcontainers, WireMock (só microsserviços).

## 1. Pirâmide

```
        ╱╲         ~5%   Ponta a ponta (fluxo completo alugar→devolver)
       ╱──╲
      ╱    ╲       ~25%  Integração (persistência real, web slice, contrato)
     ╱──────╲
    ╱        ╲     ~70%  Unitário (domínio, tarifação, use cases com mock)
   ╱──────────╲
```

## 2. Testes unitários (sem Spring)

### Domínio
| Alvo | Casos |
| --- | --- |
| `Livro` | páginas ≤ 0 rejeitado; `baixarExemplar()` com 0 disponíveis lança; `retornarExemplar()` não passa do total |
| `Autor` | nome vazio rejeitado; nascimento no futuro rejeitado |
| `Editora` | CNPJ inválido rejeitado; CNPJ duplicado (regra no use case) |
| `Aluguel` | `dataDevolucaoPrevista` ≤ retirada rejeitado; `devolver()` grava data e muda status; `marcarAtraso()` só se ATIVO e vencido |

### Tarifação (o coração do OOP)
```java
class PoliticaTarifacaoTest {

    @ParameterizedTest
    @MethodSource("cenarios")
    void calcula_taxa_conforme_politica(PoliticaTarifacao politica,
                                        ContextoTarifacao ctx,
                                        BigDecimal esperado) {
        assertThat(politica.calcular(ctx)).isEqualByComparingTo(esperado);
    }

    static Stream<Arguments> cenarios() {
        var ctx7dias = new ContextoTarifacao(
            LocalDate.of(2026,1,1), LocalDate.of(2026,1,8),
            GeneroLiterario.TECNICO, 400);
        return Stream.of(
            arguments(new TarifacaoPadrao(),      ctx7dias, new BigDecimal("14.00")),
            arguments(new TarifacaoPorGenero(),   ctx7dias, new BigDecimal("28.00")), // TECNICO x2
            arguments(new TarifacaoPromocional(), ctx7dias, new BigDecimal("0.00"))   // 7 dias grátis
        );
    }
}
```

### Use cases (Mockito nos ports)
| Use case | Mock | Casos |
| --- | --- | --- |
| `CriarLivroUseCase` | `AutorConsultaPort`, `EditoraConsultaPort`, `LivroRepository` | autor inexistente → exceção; ok → salva |
| `AlugarUseCase` (mono) | `LivroConsultaPort`, `AluguelRepository` | indisponível → exceção; ok → salva + baixa exemplar |
| `AlugarUseCase` (micro) | `LivroGateway`, `AluguelRepository` | **`baixarExemplar` lança → verifica que `aluguelRepository.remover` foi chamado (compensação)** |

## 3. Testes de integração

### Persistência — `@DataJpaTest` + Testcontainers
```java
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = NONE)
class LivroJpaAdapterIT {

    @Container
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", pg::getJdbcUrl);
        r.add("spring.datasource.username", pg::getUsername);
        r.add("spring.datasource.password", pg::getPassword);
    }

    // salva, busca, aplica filtro por gênero, respeita unicidade de ISBN
}
```

- `autor-service` / `editora-service`: `MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")`
- `aluguel-service`: `@DataMongoTest` + `MongoDBContainer<>("mongo:7")`

### Web slice — `@WebMvcTest`
```java
@WebMvcTest(LivroController.class)
class LivroControllerTest {
    @MockitoBean CriarLivroUseCase criarLivro;
    // POST inválido → 400 ProblemDetail; POST ok → 201 + Location; GET inexistente → 404
}
```

### Contrato Feign — WireMock (só microsserviços)
```java
class AutorFeignAdapterTest {
    // WireMock stub: GET /autores/1/existe → 200  ⇒ adapter.existePorId(1) == true
    // stub 404                                     ⇒ false
    // stub delay > timeout                         ⇒ fallback ("autor indisponível"), sem exceção
}
```

## 4. Testes ponta a ponta

### Monolito — `@SpringBootTest` + Testcontainers
```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
class FluxoAluguelIT {
    // 1. POST /api/autores        → 201
    // 2. POST /api/editoras       → 201
    // 3. POST /api/livros         → 201 (exemplares = 2)
    // 4. POST /api/alugueis       → 201, taxa > 0, livro agora com 1 exemplar
    // 5. POST /api/alugueis/{id}/devolucao → 200, status DEVOLVIDO, livro com 2 exemplares
}
```

### Microsserviços — script contra a stack Podman
`scripts/e2e.sh` (roda com `podman-compose up -d` completo):
```
POST  :8080/api/autores            → capturar autorId
POST  :8080/api/editoras           → capturar editoraId
POST  :8080/api/livros             → capturar livroId
GET   :8080/api/livros/{livroId}   → conferir autorNome resolvido via Feign
POST  :8080/api/alugueis           → conferir taxa
POST  :8080/api/alugueis/{id}/devolucao
# resiliência:
podman stop biblioteca-microservices_autor-service_1
POST  :8080/api/livros             → 201 com autorNome "(indisponível)"  [fallback]
podman stop biblioteca-microservices_livro-service_1
POST  :8080/api/alugueis           → 502; GET no Mongo confirma que NÃO há aluguel órfão [compensação]
```

## 5. Cobertura

- JaCoCo, threshold sugerido: LINE 80%, BRANCH 70%.
- Exclusões: classes `*Application`, `config/**`, DTOs `record` puros, `*FeignClient` (interface).
- `./mvnw verify` roda unitários (Surefire) + integração `*IT` (Failsafe) + JaCoCo.

## 6. Matriz de teste por camada

| Camada | Monolito | Micro | Ferramenta |
| --- | --- | --- | --- |
| Domínio / regras | ✅ | ✅ (reaproveitado) | JUnit + AssertJ |
| Tarifação (OOP) | ✅ | ✅ (reaproveitado) | JUnit `@ParameterizedTest` |
| Use case | ✅ mock ports locais | ✅ mock gateways HTTP | Mockito |
| Persistência | ✅ Postgres | ✅ Postgres / MSSQL / Mongo | Testcontainers |
| Web | ✅ | ✅ | `@WebMvcTest` |
| Contrato entre serviços | — | ✅ | WireMock |
| Ponta a ponta | ✅ `@SpringBootTest` | ✅ script + compose | Testcontainers / Podman |
| Resiliência (fallback/CB) | — | ✅ | WireMock + parar contêiner |
| Compensação (saga) | — | ✅ | Mockito (unit) + e2e |
