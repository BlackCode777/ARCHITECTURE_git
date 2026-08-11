# Serviço 3 — Event Driven Architecture (EDA)

> Parte de [ARQUITETURA-SERVICOS.md](ARQUITETURA-SERVICOS.md). Task 06 do roadmap
> ([FASE-1-FUNDAMENTOS.md](FASE-1-FUNDAMENTOS.md)).

## Conceito

**Event Driven Architecture** desacopla produtores e consumidores através de eventos:

* **Evento** — fato imutável que já aconteceu (ex.: `TarefaCriadaEvent`).
* **Producer** — quem publica o evento, sem saber quem (ou quantos) vão consumi-lo.
* **Consumer** — quem reage ao evento, de forma assíncrona.
* **Publish/Subscribe** — múltiplos consumers podem reagir ao mesmo evento
  independentemente uns dos outros.
* **Event Notification** — o evento carrega só o essencial (ex.: ID), e quem consome
  busca mais detalhes se precisar, evitando acoplamento de payload.

Nesta fase o mecanismo usado é o `ApplicationEventPublisher` do próprio Spring
(em memória, síncrono por padrão) — sem broker externo. Kafka/RabbitMQ entram em
fases futuras do roadmap (ver skills `mensageria-kafka` e `mensageria-rabbitmq` no
catálogo global).

## Estrutura de Pastas

```text
eda/
├── domain/
│   └── event/
│       └── TarefaCriadaEvent.java          ← Domain Event (record)
├── application/
│   └── PublicarTarefaCriadaUseCase.java    ← Producer
└── infrastructure/
    └── listener/
        └── TarefaCriadaEventListener.java  ← Consumer (@EventListener)
```

## O que foi criado

* [TarefaCriadaEvent.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/eda/domain/event/TarefaCriadaEvent.java)
* [PublicarTarefaCriadaUseCase.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/eda/application/PublicarTarefaCriadaUseCase.java) — publica via `ApplicationEventPublisher`
* [TarefaCriadaEventListener.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/eda/infrastructure/listener/TarefaCriadaEventListener.java) — consome e loga

## Próximos passos (Mini Projeto da Task 06)

* [x] Ligar `PublicarTarefaCriadaUseCase` ao fluxo real de criação de tarefa do CRUD —
      ver [09-tasks-EDA-CAP-REST.md](09-tasks-EDA-CAP-REST.md)
* [x] Adicionar mais um listener demonstrando Pub/Sub com múltiplos consumers —
      `TarefaCriadaMetricsListener`, que também conecta ao
      [Serviço 7 — Metrics](07-metrics-service.md)
* [ ] (Fase futura) Migrar producer/consumer para Kafka ou RabbitMQ — ver skills
      `mensageria-kafka` / `mensageria-rabbitmq`

**Resumo do que foi implementado (04/08/2026):** `CriarTarefaUseCase` (rest/application)
agora publica `TarefaCriadaEvent` após persistir a tarefa com sucesso. Dois listeners
independentes reagem ao mesmo evento — `TarefaCriadaEventListener` (log) e
`TarefaCriadaMetricsListener` (incrementa `tarefas.criadas.total` no Actuator/Prometheus).
Validado via curl + log da aplicação + `/actuator/metrics`. Detalhes em
[09-tasks-EDA-CAP-REST.md](09-tasks-EDA-CAP-REST.md).

## Referências

* Martin Fowler — "What do you mean by Event-Driven", https://martinfowler.com
* Skill `mensageria-kafka`, `mensageria-rabbitmq`, `ddd-domain-driven-design` (catálogo global)
