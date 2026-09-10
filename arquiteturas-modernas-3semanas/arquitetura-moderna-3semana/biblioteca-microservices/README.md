# biblioteca-microservices

> **Fase 2** do estudo Monolito × Microsserviços. O mesmo sistema de reserva/aluguel
> de livros, quebrado em **4 serviços independentes + 1 API Gateway**, cada serviço
> com **seu próprio banco**.
>
> Projetos **gerados no start.spring.io** e descompactados nesta pasta, agrupados
> por um `pom.xml` pai (`packaging=pom`).
>
> Só iniciar a implementação com a Fase 1 (`../biblioteca-monolito/`) **verde**.
> Reaproveitar domínio, use cases e DTOs do monolito.

## Documentação

Em [`../../doc/`](../../doc/README.md):

- **Arquitetura deste projeto**: [`../../doc/03-ARQUITETURA-MICROSERVICES.md`](../../doc/03-ARQUITETURA-MICROSERVICES.md)
- **Checklist de implementação**: [`../../doc/07-TASKS-MICROSERVICES.md`](../../doc/07-TASKS-MICROSERVICES.md)
- Comparativo com o monolito: [`../../doc/04-COMPARATIVO.md`](../../doc/04-COMPARATIVO.md)
- Contêineres: [`../../doc/08-PODMAN-COMPOSE.md`](../../doc/08-PODMAN-COMPOSE.md)
- Testes: [`../../doc/09-TESTES-ESTRATEGIA.md`](../../doc/09-TESTES-ESTRATEGIA.md)

## Os serviços

| Serviço | Porta | Banco | Depende de |
| --- | --- | --- | --- |
| **api-gateway** | 8080 | — | todos |
| **livro-service** | 8081 | PostgreSQL `:5432` / `livro_db` | autor-service, editora-service |
| **autor-service** | 8082 | SQL Server inst. A `:1433` / `autor_db` | — |
| **editora-service** | 8083 | SQL Server inst. B `:1434` / `editora_db` | — |
| **aluguel-service** | 8084 | MongoDB `:27017` / `aluguel_db` | livro-service |

```
       clientes ──► api-gateway (8080) ──► [ livro | autor | editora | aluguel ]
                                                │        │         │         │
                                            PostgreSQL  SQLSrv-A  SQLSrv-B  MongoDB

  serviço → serviço: REST síncrono via OpenFeign + Resilience4j (timeout/retry/CB/fallback)
```

## Stack

| Item | Escolha |
| --- | --- |
| Java / Spring Boot | 25 / 4.1.1 |
| Gateway | Spring Cloud Gateway |
| Cliente HTTP entre serviços | OpenFeign + Resilience4j |
| Arquitetura interna | Hexagonal (Ports & Adapters) — igual ao monolito |
| Persistência | JPA + Flyway (livro/autor/editora); Spring Data MongoDB (aluguel) |
| DTOs / Mapeamento | `record` + MapStruct — sem Lombok |
| API docs | springdoc-openapi por serviço |
| Testes | JUnit 5, Mockito, Testcontainers, **WireMock** (contrato) |
| Contêineres | Podman (`podman-compose`) |

## Estrutura da pasta

```
biblioteca-microservices/
├── pom.xml              (POM pai, packaging=pom, <modules>)
├── podman-compose.yml   (4 bancos + 4 serviços + gateway)
├── api-gateway/
├── livro-service/
├── autor-service/
├── editora-service/
├── aluguel-service/
└── scripts/e2e.sh       (fluxo ponta a ponta via gateway)
```

## Como rodar

```powershell
podman machine start

# só os bancos (dev pela IDE):
podman-compose up -d postgres sqlserver-autor sqlserver-editora mongo

# stack completa:
podman-compose up -d

# logs / reset:
podman-compose logs -f
podman-compose down -v
```

Swagger de cada serviço: `http://localhost:808{1..4}/swagger-ui.html`
Entrada única: `http://localhost:8080/api/...`

## O que este projeto demonstra

- **Database-per-service**: nenhum serviço toca o banco do outro; dado alheio se pede pela API.
- **Perda do `JOIN`**: "livro com nome do autor" vira chamada Feign (+ fallback se o autor-service cair).
- **Perda da transação ACID**: alugar = salvar aluguel (Mongo) + baixar exemplar (livro-service) →
  **compensação manual** se o segundo passo falhar.
- **Isolamento de falha**: derrubar o aluguel-service não impede cadastrar autor.
- **Heterogeneidade**: relacional (Postgres/SQL Server) e documento (Mongo) convivendo.

## Fluxo de exemplo (via gateway)

```
POST :8080/api/autores    → autorId
POST :8080/api/editoras   → editoraId
POST :8080/api/livros     { autorId, editoraId, genero, ... }  → livroId
GET  :8080/api/livros/{livroId}   → autorNome resolvido via Feign
POST :8080/api/alugueis   { livroId, nomeLocatario, dias }     → taxa
POST :8080/api/alugueis/{id}/devolucao
```
