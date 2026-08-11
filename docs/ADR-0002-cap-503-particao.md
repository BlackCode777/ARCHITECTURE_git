# ADR-0002 — Resposta 503 sob partição de dados (CAP: escolha CP)

* **Status:** Aceito
* **Data:** 2026-08-04

## Contexto

Durante a execução do experimento da Task 02 do roadmap (ver
[09-tasks-EDA-CAP-REST.md](09-tasks-EDA-CAP-REST.md)), o PostgreSQL foi derrubado via
`podman stop fdmts-2smn-postgres` com a aplicação em execução e uma tarefa já
persistida. O objetivo era observar o comportamento real da API sob partição — não
hipotético — e decidir como a API deveria se comportar nesse cenário.

Comportamento observado antes de qualquer correção:

* Chamadas de negócio (`GET`/`POST /rest/tarefas`) travavam por **30 segundos**
  (timeout padrão do `HikariCP`) antes de retornar **HTTP 500 genérico**, sem corpo
  estruturado — porque a exceção real lançada é
  `org.springframework.transaction.CannotCreateTransactionException`, não
  `DataAccessResourceFailureException` (única capturada até então no
  `GlobalExceptionHandler`).
* `GET /actuator/health` respondia **imediatamente** com `503` / `{"status":"DOWN"}`,
  pois o `DataSourceHealthIndicator` faz um `isValid()` curto, sem esperar o
  `connection-timeout` do pool de aplicação.
* Ao religar o banco, o sistema voltou a `200 OK` sem qualquer dado corrompido ou
  escrita parcial — a transação nunca chegou a abrir durante a partição.

## Decisão

1. **Assumir CP explicitamente** para o CRUD de Tarefas: durante uma partição, a API
   fica indisponível (ou lenta, até o timeout do pool) em vez de servir dado
   potencialmente desatualizado. Não há réplica de leitura nem cache configurados
   nesta fase — logo, não existe um caminho "AP" real disponível sem adicionar essa
   infraestrutura (fora do escopo da Fase 1).
2. **Capturar `CannotCreateTransactionException` no `GlobalExceptionHandler`**, junto
   com `DataAccessResourceFailureException`, retornando `503 Service Unavailable` no
   formato RFC 9457 (`ProblemDetail`) em vez do `500` genérico do Spring Boot.
3. **Não alterar o `connection-timeout` do HikariCP** (mantido no padrão de 30s) nesta
   rodada — documentar o comportamento observado é suficiente para o objetivo didático
   da Task 02; reduzir o timeout é uma decisão de tuning que pertence a uma fase
   posterior, quando houver requisito real de SLA.

## Alternativas Consideradas

| Opção | Prós | Contras | Motivo da rejeição |
| --- | --- | --- | --- |
| Reduzir `connection-timeout` do Hikari para falhar rápido (ex.: 2s) | UX melhor sob partição — cliente não espera 30s | Sob picos de latência normais (não partição), aumentaria falsos negativos de "banco indisponível" | Adiado — ajuste fino de infraestrutura sem requisito de SLA definido ainda |
| Adicionar cache local (Caffeine, já no roadmap de dependências) para servir leitura stale durante partição (rota AP) | Disponibilidade alta mesmo sob partição | Contradiz a garantia de consistência que o CRUD de Tarefas precisa (usuário não pode ver tarefa como "pendente" quando já foi concluída em outra sessão) | Rejeitado para este CRUD; ficará como exercício futuro comparativo CP x AP |
| Manter apenas `DataAccessResourceFailureException` no handler (não corrigir) | Nenhum esforço adicional | Cliente recebe `500` sem corpo estruturado — informação nenhuma sobre a causa real (partição de dados) | Rejeitado — corrigir é trivial e melhora a observabilidade sem custo arquitetural |

## Consequências

* Clientes da API agora recebem `503` com `ProblemDetail` (`title`, `detail`, `status`,
  `instance`) sempre que o banco estiver inacessível, em vez de um `500` opaco.
* O tempo de resposta sob partição continua em até ~30s para os endpoints de negócio —
  isso é uma limitação conhecida e aceita, não um bug: reflete a escolha CP.
* `/actuator/health` continua sendo o sinal mais rápido e confiável de indisponibilidade
  de banco — deve ser o endpoint usado por um futuro load balancer/orquestrador para
  decisões de failover (ver [Serviço 6 — Load Balance](06-lb-service.md)), não os
  endpoints de negócio.
* Se uma fase futura exigir alta disponibilidade de leitura durante partição, a decisão
  precisará ser revisitada com um novo ADR, avaliando cache/réplica de leitura (rota AP).

## Referências

* [09-tasks-EDA-CAP-REST.md](09-tasks-EDA-CAP-REST.md) — experimento completo e achados
* [01-cap-service.md](01-cap-service.md) — conceito de CAP Theorem
* Skill `dados-sistemas-distribuidos`, `tratamento-excecoes-validacao` (catálogo global)
