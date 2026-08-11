# Tasks 02, 03 e 06 — CAP, REST e EDA aplicados ao CRUD real de Tarefas

> Executa os "Próximos passos (Mini Projeto)" descritos em
> [01-cap-service.md](01-cap-service.md) (Task 02), [02-rest-service.md](02-rest-service.md)
> (Task 03) e [03-eda-service.md](03-eda-service.md) (Task 06). Consulte também
> [ARQUITETURA-SERVICOS.md](ARQUITETURA-SERVICOS.md) e [ADR-0002](ADR-0002-cap-503-particao.md).

Até aqui os 7 serviços tinham apenas esqueletos didáticos isolados — nenhum persistia
dados reais. Esta rodada de trabalho criou o **primeiro CRUD real** (`TarefaEntity` +
PostgreSQL) e o usou como base comum para fechar as três tasks pendentes: REST completo,
EDA ligado ao fluxo real, e observação empírica do CAP Theorem sob partição de rede.

## 1. Task 03 — CRUD REST completo (Serviço 2)

### O que foi criado

Todo o código novo vive em `rest/`, seguindo a mesma separação `domain / application / web`
usada nos demais serviços (skill `spring-boot-estrutura-projeto` + `spring-data-jpa-hibernate`
do catálogo global):

```text
rest/
├── domain/
│   └── TarefaEntity.java              ← @Entity, SEQUENCE (allocationSize=50), auditoria UTC
├── application/
│   ├── TarefaRepository.java          ← Spring Data JpaRepository
│   ├── CriarTarefaUseCase.java        ← persiste + publica TarefaCriadaEvent (liga ao EDA)
│   ├── ListarTarefasUseCase.java
│   ├── BuscarTarefaUseCase.java
│   ├── AtualizarTarefaUseCase.java
│   ├── ConcluirTarefaRestUseCase.java
│   └── DeletarTarefaUseCase.java
└── web/
    ├── CriarTarefaRequest.java        ← record com Bean Validation (@NotBlank, @Size)
    ├── AtualizarTarefaRequest.java
    ├── TarefaResponse.java            ← record, nunca expõe TarefaEntity diretamente
    └── TarefaController.java          ← anotado com @Tag/@Operation (Swagger)
```

Também foi criado `shared/exception/`:

* `TarefaNaoEncontradaException.java` — exceção de domínio (404).
* `GlobalExceptionHandler.java` — `@RestControllerAdvice` centralizado, formato RFC 9457
  (`ProblemDetail`), seguindo a skill `tratamento-excecoes-validacao`.

### Endpoints (verbos e status corretos)

| Verbo | Rota | Status sucesso | Descrição |
| --- | --- | --- | --- |
| `POST` | `/rest/tarefas` | `201 Created` + `Location` | Cria tarefa, publica `TarefaCriadaEvent` |
| `GET` | `/rest/tarefas` | `200 OK` | Lista todas |
| `GET` | `/rest/tarefas/{id}` | `200 OK` / `404` | Busca por ID |
| `PUT` | `/rest/tarefas/{id}` | `200 OK` / `404` | Atualiza título (substituição) |
| `PATCH` | `/rest/tarefas/{id}/concluir` | `200 OK` / `404` | Atualização parcial de estado |
| `DELETE` | `/rest/tarefas/{id}` | `204 No Content` / `404` | Remove |

### Validado via curl

```text
POST   /rest/tarefas          → 201 {"id":1,"titulo":"Estudar CAP Theorem",...}
GET    /rest/tarefas          → 200 [...]
GET    /rest/tarefas/1        → 200 {...}
PATCH  /rest/tarefas/1/concluir → 200 {"concluida":true,...}
PUT    /rest/tarefas/1        → 200 {"titulo":"...revisado",...}
GET    /rest/tarefas/999      → 404 ProblemDetail "Tarefa não encontrada"
POST   /rest/tarefas {"titulo":""} → 400 ProblemDetail "erros":[{"campo":"titulo",...}]
DELETE /rest/tarefas/1        → 204
```

### OpenAPI/Swagger

`TarefaController` foi anotado com `@Tag` e `@Operation` (springdoc, já configurado em
[08-infraestrutura-base.md](08-infraestrutura-base.md)). Confirmado `GET /v3/api-docs` → `200`,
com o schema completo do CRUD de Tarefas.

### Pendências conscientemente adiadas

* **Client HTTP (`WebClient`)** — não foi criado porque ainda não há um segundo serviço
  real para consumir; ficará natural quando o roadmap (Fase 2 — Microservices) separar
  os serviços em módulos independentes (ver [ADR-0001](ADR-0001-arquitetura-servicos.md)).
* **Contrato formal documentado por endpoint** (além do Swagger vivo) — o próprio
  `/v3/api-docs` já cumpre esse papel; não foi duplicado em Markdown para evitar
  divergência entre documentação e código.

## 2. Task 06 — EDA ligado ao fluxo real (Serviço 3)

### O que mudou

Antes, `PublicarTarefaCriadaUseCase` e `TarefaCriadaEventListener` existiam isolados,
sem nenhum código real disparando o evento. Agora:

* `CriarTarefaUseCase.executar()` (rest/application) chama
  `PublicarTarefaCriadaUseCase.executar(tarefa.getId())` **depois** de persistir a
  tarefa — o evento só é publicado se o `save()` no PostgreSQL for bem-sucedido.
* Um **segundo listener** foi criado —
  [TarefaCriadaMetricsListener.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/eda/infrastructure/listener/TarefaCriadaMetricsListener.java) —
  demonstrando **Publish/Subscribe** de verdade: dois consumers reagem ao mesmo
  `TarefaCriadaEvent` de forma independente, sem se conhecerem:
  1. `TarefaCriadaEventListener` — loga o evento (auditoria).
  2. `TarefaCriadaMetricsListener` — incrementa `TarefaMetrics` (conecta ao
     [Serviço 7 — Metrics](07-metrics-service.md), antes órfão).

```text
CriarTarefaUseCase.executar()
        │ save(TarefaEntity)               ← PostgreSQL
        │ publishEvent(TarefaCriadaEvent)  ← ApplicationEventPublisher (síncrono)
        ├──> TarefaCriadaEventListener        (log estruturado)
        └──> TarefaCriadaMetricsListener      (Counter tarefas.criadas.total)
```

### Validado via curl + log + Actuator

```text
POST /rest/tarefas {"titulo":"Estudar CAP Theorem"} → 201

# log da aplicação:
TarefaCriadaEventListener : Evento recebido: tarefaId=1 ocorridoEm=...

# GET /actuator/metrics/tarefas.criadas.total
{"measurements":[{"statistic":"COUNT","value":1.0}], "availableTags":[{"tag":"application","values":["fundamentos-2semanas"]}]}
```

Os dois listeners dispararam a partir da mesma publicação — confirma Pub/Sub.

### Pendência conscientemente adiada

* **Migração para Kafka/RabbitMQ** — mantida como trabalho de fase futura (skills
  `mensageria-kafka` / `mensageria-rabbitmq` no catálogo global), pois o mecanismo em
  memória (`ApplicationEventPublisher`) ainda cumpre o objetivo didático da Fase 1.

## 3. Task 02 — CAP Theorem observado empiricamente (Serviço 1)

### Experimento realizado

Com a aplicação rodando e uma tarefa já persistida:

```powershell
podman stop fdmts-2smn-postgres     # simula partição de rede / queda do nó de dados
curl http://localhost:8080/rest/tarefas
curl http://localhost:8080/actuator/health
podman start fdmts-2smn-postgres    # restaura a partição
```

### Comportamento observado (registrado, não hipotético)

| Chamada | Resultado real | Tempo |
| --- | --- | --- |
| `GET /rest/tarefas` (banco fora) | **500** genérico (antes da correção) | ~30s |
| `POST /rest/tarefas` (banco fora) | **500** genérico (antes da correção) | ~30s |
| `GET /actuator/health` (banco fora) | **503** `{"status":"DOWN"}` | imediato |
| `GET /rest/tarefas` (banco fora, após correção) | **503** ProblemDetail | ~30s |
| Qualquer chamada, banco de volta | **200**, sem dado perdido/corrompido | imediato |

**Achado 1 — a API trava, não falha rápido.** As chamadas de negócio (`/rest/tarefas`)
não retornam erro imediatamente: o HikariCP tenta abrir uma nova conexão e só desiste
após o `connection-timeout` padrão de **30 segundos**, gerando
`CannotCreateTransactionException`. Isso é uma escolha implícita de **CP**
(Consistency + Partition Tolerance): a API prefere ficar indisponível/lenta a
responder com dado potencialmente desatualizado — não existe cache local nem réplica
de leitura configurada, então não há como ser AP aqui sem adicionar essa infraestrutura.

**Achado 2 — Actuator não sofre o mesmo travamento.** `/actuator/health` usa o
`DataSourceHealthIndicator`, que executa um `isValid()`/ping curto e falha rápido —
não fica preso ao `connection-timeout` do pool de aplicação. Isso o torna confiável
como sinal de liveness/readiness para um load balancer (ver
[Serviço 6 — Load Balance](06-lb-service.md)), mesmo quando os endpoints de negócio
já estão lentos.

**Achado 3 — a exceção real não era a que o código esperava.** O handler original em
`GlobalExceptionHandler` capturava apenas `DataAccessResourceFailureException`, mas a
exceção efetivamente lançada é `org.springframework.transaction.CannotCreateTransactionException`
(o Spring nem chega a abrir a transação JPA). Sem capturá-la, o cliente recebia um
`500 Internal Server Error` sem corpo estruturado — comportamento corrigido nesta
rodada (ver [ADR-0002](ADR-0002-cap-503-particao.md)).

**Achado 4 — nenhuma escrita parcial ocorreu.** Ao tentar `POST` durante a partição, a
transação nunca abriu — logo nenhuma linha foi inserida. Ao religar o banco, `GET
/rest/tarefas` retornou lista vazia (a tarefa de teste da partição nunca existiu),
confirmando que o sistema não ficou em estado inconsistente.

### Documento de comparação de cenários

| Cenário | Consistency | Availability | Observação |
| --- | --- | --- | --- |
| Banco disponível | Forte (read-your-writes via `ddl-auto: validate` + transação síncrona) | Alta | Comportamento padrão |
| Banco indisponível (partição) | Mantida (sem escrita parcial, sem dado stale servido) | **Sacrificada** — 503 após até 30s | Escolha CP, coerente com PostgreSQL single-node sem réplica |
| Banco volta | Forte, imediata | Alta, imediata | Sem necessidade de reconciliação — não houve dado divergente |

## Mudanças de código resumidas

| Arquivo | Ação |
| --- | --- |
| `rest/domain/TarefaEntity.java` | Criado — entidade JPA real |
| `rest/application/TarefaRepository.java` | Criado — `JpaRepository` |
| `rest/application/{Criar,Listar,Buscar,Atualizar,ConcluirTarefaRest,Deletar}UseCase.java` | Criados |
| `rest/web/{Criar,Atualizar}TarefaRequest.java`, `TarefaResponse.java`, `TarefaController.java` | Criados |
| `rest/web/RestDemoController.java`, `rest/application/ConsultarStatusUseCase.java`, `rest/domain/StatusResponse.java` | **Removidos** — substituídos pelo CRUD real |
| `shared/exception/TarefaNaoEncontradaException.java`, `GlobalExceptionHandler.java` | Criados |
| `eda/infrastructure/listener/TarefaCriadaMetricsListener.java` | Criado — segundo consumer Pub/Sub |
| `hexagonal/application/ConcluirTarefaUseCase.java` | Sem alteração — mantido isolado; o novo `rest/application/ConcluirTarefaRestUseCase.java` evita colisão de nome de bean Spring |

## Referências

* Skill `spring-data-jpa-hibernate`, `tratamento-excecoes-validacao`,
  `spring-boot-estrutura-projeto`, `dados-sistemas-distribuidos` (catálogo global)
* [ADR-0002 — Resposta 503 sob partição de dados](ADR-0002-cap-503-particao.md)
* [01-cap-service.md](01-cap-service.md), [02-rest-service.md](02-rest-service.md),
  [03-eda-service.md](03-eda-service.md), [07-metrics-service.md](07-metrics-service.md)
