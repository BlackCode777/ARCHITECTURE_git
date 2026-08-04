# Serviço 7 — Observabilidade e Métricas

> Parte de [ARQUITETURA-SERVICOS.md](ARQUITETURA-SERVICOS.md). Entregável de
> observabilidade da Fase 1 ([FASE-1-FUNDAMENTOS.md](FASE-1-FUNDAMENTOS.md)).

## Conceito

* **Actuator** — expõe endpoints operacionais (`/actuator/health`, `/actuator/metrics`,
  `/actuator/prometheus`). Regra inegociável do catálogo global: `health` público,
  o restante exige `ADMIN`.
* **Micrometer** — fachada de métricas usada pelo Spring Boot, com binding para
  Prometheus (`micrometer-registry-prometheus`).
* **Métrica customizada (Counter)** — além das métricas técnicas automáticas (JVM,
  HTTP, datasource), é possível instrumentar métricas de negócio, como
  `tarefas.criadas.total`.
* **Logs estruturados** — logs em formato consumível por ferramentas (JSON via
  `logstash-logback-encoder`), correlacionáveis com trace/span id.

## Estrutura de Pastas

```text
metrics/
├── config/
│   └── MetricsConfig.java        ← common tags do MeterRegistry
└── infrastructure/
    └── TarefaMetrics.java        ← Counter de negócio (tarefas.criadas.total)
```

## O que foi criado

* [MetricsConfig.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/metrics/config/MetricsConfig.java) — adiciona a tag comum `application=fundamentos-2semanas` a todas as métricas exportadas.
* [TarefaMetrics.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/metrics/infrastructure/TarefaMetrics.java) — `Counter` customizado, exposto em `/actuator/prometheus` como `tarefas_criadas_total`.

## Próximos passos (Entregáveis de Observabilidade da Fase 1)

* [ ] Adicionar `micrometer-registry-prometheus` ao `pom.xml` (ainda não presente)
* [ ] Expor `/actuator/health` público e proteger o restante com `ADMIN`
      (skill `spring-security-essencial`)
* [ ] Ligar `TarefaMetrics.incrementarTarefaCriada()` ao fluxo real de criação de tarefa
* [ ] Configurar `logback-classic` + `logstash-logback-encoder` para logs estruturados
* [ ] Subir Prometheus + Grafana via Podman e criar dashboard básico

## Referências

* Martin Fowler — https://martinfowler.com
* Skill `observabilidade-opentelemetry`, `devops-observability-engineer` (catálogo global)
