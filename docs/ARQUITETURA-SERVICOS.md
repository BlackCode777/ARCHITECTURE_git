# Arquitetura de Serviços — Projeto Roadmap

> Consulta obrigatória: [CLAUDE.md](../CLAUDE.md) → catálogo global de skills em
> `D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\.claude\CLAUDE.md`. Convenções de
> nomenclatura (`UseCase`, `Port`, `Repository`, `Entity`, `Request`, `Response`,
> `Mapper`), regras inegociáveis (SEQUENCE, Testcontainers, Podman, `open-in-view: false`)
> e a tabela de gatilhos de skills valem para todos os serviços descritos aqui.

## Visão Geral

```text
                +---------------------+
                |  Projeto Roadmap    |
                |---------------------|
                | CRUD de Tarefas     |
                | PostgreSQL          |
                | Spring Boot         |
                | Podman              |
                +----------+----------+
                           |
         -----------------------------------------
         |      |      |      |      |      |     |
       CAP    REST   EDA   Hexagonal NTP  LB  Metrics
```

O projeto `fundamentos-2semanas` é o CRUD de Tarefas base (Fase 1 do roadmap —
ver [FASE-1-FUNDAMENTOS.md](FASE-1-FUNDAMENTOS.md)). Cada ramo do diagrama acima
corresponde a um **pacote de serviço** dentro do mesmo módulo Maven, isolado em
`com.tesseracode_labs.fundamentos_2semanas.<servico>`, demonstrando um conceito de
arquitetura distribuída específico. A intenção é que cada pacote seja um exemplo
didático autocontido do padrão que representa, mesmo compartilhando o mesmo processo
JVM e `pom.xml`.

## Mapa de Serviços

| # | Serviço | Pacote | Conceito | Documentação |
| --- | --- | --- | --- | --- |
| 1 | `cap-service` | `cap` | CAP Theorem — Consistency, Availability, Partition Tolerance | [01-cap-service.md](01-cap-service.md) |
| 2 | `rest-service` | `rest` | REST, HTTP, RPC/gRPC | [02-rest-service.md](02-rest-service.md) |
| 3 | `eda-service` | `eda` | Event Driven Architecture (Producer/Consumer/Pub-Sub) | [03-eda-service.md](03-eda-service.md) |
| 4 | `hexagonal-service` | `hexagonal` | Hexagonal Architecture (Ports & Adapters) | [04-hexagonal-service.md](04-hexagonal-service.md) |
| 5 | `ntp-service` | `ntp` | Sincronização de tempo (NTP, Clock Drift, UTC) | [05-ntp-service.md](05-ntp-service.md) |
| 6 | `lb-service` | `lb` | Load Balance (Round Robin, Health Check, Reverse Proxy) | [06-lb-service.md](06-lb-service.md) |
| 7 | `metrics-service` | `metrics` | Observabilidade e Métricas (Actuator, Prometheus) | [07-metrics-service.md](07-metrics-service.md) |

## Estrutura de Pastas — Padrão Comum

Cada pacote de serviço segue a mesma filosofia hexagonal definida no catálogo global
(`hexagonal-ports-adapters`), adaptada ao escopo específico do conceito que demonstra:

```text
src/main/java/com/tesseracode_labs/fundamentos_2semanas/
├── task/                    ← domínio base do CRUD de Tarefas (Fase 1)
│   ├── domain/
│   ├── application/
│   ├── infrastructure/
│   └── web/
├── cap/                     ← Serviço 1: CAP Theorem
├── rest/                    ← Serviço 2: REST
├── eda/                     ← Serviço 3: Event Driven Architecture
├── hexagonal/               ← Serviço 4: Hexagonal Architecture (referência canônica)
├── ntp/                     ← Serviço 5: Sincronização de Tempo
├── lb/                      ← Serviço 6: Load Balance
├── metrics/                 ← Serviço 7: Observabilidade e Métricas
├── shared/                  ← código transversal (exceções, paginação, auditoria)
└── config/                  ← @Configuration, @Bean
```

Cada `<servico>/` interno replica, quando fizer sentido para o conceito, as camadas
`domain/ application/ infrastructure/ web/` — mas a estrutura interna exata está
detalhada em cada documento individual, pois nem todo conceito exige as 4 camadas
completas (ex.: `ntp` é majoritariamente utilitário/config).

## Status de Implementação

| Serviço | Estrutura de pastas | Código mínimo | Testes | ADR |
| --- | --- | --- | --- | --- |
| cap-service | ✅ | ✅ (esqueleto) | ⬜ | ver [ADR-0001](ADR-0001-arquitetura-servicos.md) |
| rest-service | ✅ | ✅ (esqueleto) | ⬜ | ver ADR-0001 |
| eda-service | ✅ | ✅ (esqueleto) | ⬜ | ver ADR-0001 |
| hexagonal-service | ✅ | ✅ (esqueleto) | ⬜ | ver ADR-0001 |
| ntp-service | ✅ | ✅ (esqueleto) | ⬜ | ver ADR-0001 |
| lb-service | ✅ | ✅ (esqueleto) | ⬜ | ver ADR-0001 |
| metrics-service | ✅ | ✅ (esqueleto) | ⬜ | ver ADR-0001 |

> Regra de evolução: **toda nova criação de código dentro de um pacote de serviço
> exige atualização do documento correspondente** (`0N-<servico>.md`) e, se a decisão
> for arquitetural (ex.: trocar de biblioteca, mudar padrão de mensageria), exige
> também um novo ADR em `docs/ADR-000N-*.md`.

## Referências

* Martin Fowler — https://martinfowler.com (fonte única indicada na Fase 1)
* [FASE-1-FUNDAMENTOS.md](FASE-1-FUNDAMENTOS.md) — roadmap de estudo original
* Skill `hexagonal-ports-adapters`, `dados-sistemas-distribuidos`,
  `escalabilidade-sistemas-distribuidos`, `mensageria-kafka`, `mensageria-rabbitmq`,
  `observabilidade-opentelemetry` — catálogo global (CLAUDE.md)
