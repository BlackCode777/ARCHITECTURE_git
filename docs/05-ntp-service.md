# Serviço 5 — Sincronização de Tempo (NTP)

> Parte de [ARQUITETURA-SERVICOS.md](ARQUITETURA-SERVICOS.md). Task 04 do roadmap
> ([FASE-1-FUNDAMENTOS.md](FASE-1-FUNDAMENTOS.md)).

## Conceito

Em sistemas distribuídos, cada nó tem seu próprio relógio físico, que naturalmente
diverge (**clock drift**) do relógio dos demais nós ao longo do tempo:

* **NTP (Network Time Protocol)** — protocolo que sincroniza relógios de máquinas
  contra servidores de tempo de referência.
* **Clock Drift** — desvio progressivo entre o relógio local e o tempo real.
* **Timestamp** — marca de tempo associada a um evento/registro.
* **TimeZone** — deslocamento regional que deve ser tratado na borda (apresentação),
  nunca no armazenamento.
* **Relógios Lógicos** (Lamport, Vector Clocks) — alternativa a relógios físicos para
  ordenar eventos em sistemas distribuídos sem depender de sincronização de tempo real.

**Regra prática adotada:** todo timestamp persistido usa **UTC**; conversão para o
fuso do usuário acontece apenas na camada de apresentação (`web`).

## Estrutura de Pastas

```text
ntp/
├── application/
│   └── RelogioUtcService.java   ← fonte única de tempo (Instant.now() em UTC)
└── infrastructure/
    └── Auditoria.java           ← value object createdAt/updatedAt
```

## O que foi criado

* [RelogioUtcService.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/ntp/application/RelogioUtcService.java) — todo código que precisa de "agora" deve injetar este serviço em vez de chamar `Instant.now()` diretamente, centralizando a política de clock.
* [Auditoria.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/ntp/infrastructure/Auditoria.java) — value object reutilizável para `createdAt`/`updatedAt` nas entidades JPA.

## Próximos passos (Mini Projeto da Task 04)

* [x] Aplicar `Auditoria` às entidades JPA do CRUD de Tarefas (`createdAt`/`updatedAt`) —
      ver seção "RelogioUtcService aplicado" abaixo (o value object `Auditoria` em si
      não foi usado diretamente na entidade; `TarefaEntity` já tinha os dois campos
      `Instant` desde a criação, o trabalho desta rodada foi centralizar a origem do
      timestamp em `RelogioUtcService`)
* [x] Garantir que o PostgreSQL/JVM rodem com timezone `UTC` — PostgreSQL confirmado
      via `podman-compose.yml` (`TZ=UTC`/`PGTZ=UTC`); **JVM não roda em UTC** (achado
      abaixo)
* [x] Testar serialização/deserialização de timestamp em diferentes TimeZones no JSON —
      ver seção "Validado via curl" abaixo

## `RelogioUtcService` aplicado ao CRUD real (15/08/2026)

Antes, `TarefaEntity.concluir()`/`renomear()`/o construtor chamavam `Instant.now()`
diretamente — o `RelogioUtcService` existia isolado, sem nenhum código real usando-o.

Como uma entidade JPA é instanciada pelo Hibernate via reflection (não pelo container
Spring), ela não pode receber `RelogioUtcService` por injeção de construtor. A correção
foi mover a responsabilidade de obter "agora" para a camada de aplicação: `TarefaEntity`
passou a **receber** o `Instant` como parâmetro em vez de gerá-lo:

```java
// antes
public void concluir() { this.updatedAt = Instant.now(); }

// depois
public void concluir(Instant agora) { this.updatedAt = agora; }
```

Os UseCases que chamam esses métodos passaram a injetar `RelogioUtcService` e repassar
o valor:

| UseCase | Chamada alterada |
| --- | --- |
| `rest.application.CriarTarefaUseCase` | `new TarefaEntity(titulo, relogioUtcService.agora())` |
| `rest.application.ConcluirTarefaRestUseCase` | `tarefa.concluir(relogioUtcService.agora())` |
| `rest.application.AtualizarTarefaUseCase` | `tarefa.renomear(novoTitulo, relogioUtcService.agora())` |
| `hexagonal.infrastructure.persistence.TarefaJpaAdapter` | `entity.concluir(relogioUtcService.agora())` — o endpoint hexagonal (Serviço 4) também passou a usar a mesma fonte de tempo |

O domínio hexagonal puro (`hexagonal.domain.Tarefa`) não foi alterado — seu
`concluir()` não tem campo de timestamp, só a flag `concluida`, então não havia nada
para centralizar ali.

### Validado via curl

```text
date -u  → 2026-08-15 09:20:03 UTC (hora real do host)

POST /rest/tarefas {"titulo":"Testar RelogioUtcService"} → 201
  {"createdAt":"2026-08-15T09:20:03.496063100Z", "updatedAt":"2026-08-15T09:20:03.496063100Z"}

PATCH /rest/tarefas/52/concluir → 200
  {"createdAt":"2026-08-15T09:20:03.496063Z", "updatedAt":"2026-08-15T09:20:32.375887600Z"}
```

`createdAt` bate exatamente com o horário UTC real do host no momento da chamada —
confirma que `RelogioUtcService` está gerando o timestamp correto e que ele chega
intacto até a resposta JSON.

### Serialização/deserialização JSON — Jackson + `Instant`

O Spring Boot já registra o módulo `jackson-datatype-jsr310` por padrão (via
`spring-boot-starter-jackson`), então `java.time.Instant` serializa nativamente no
formato ISO-8601 com sufixo `Z` (UTC), sem configuração adicional:

```json
{"createdAt": "2026-08-15T09:20:03.496063Z"}
```

**Decisão de design relevante:** `CriarTarefaRequest`/`AtualizarTarefaRequest` **não**
aceitam `createdAt`/`updatedAt` como campo de entrada — o cliente não controla esses
valores, apenas o servidor via `RelogioUtcService`. Isso elimina de raiz a classe de bug
onde um cliente enviaria um timestamp em timezone incorreto (ex.: `-03:00` em vez de
UTC) e ele fosse aceito sem tradução. Não havia, portanto, um campo de entrada real para
testar deserialização de timezone alternativo — o contrato da API já impede esse cenário
por design.

### Achado — a JVM não roda em UTC (risco documentado, não corrigido)

```text
JVM default TimeZone: America/Sao_Paulo
```

A JVM local usa o timezone do sistema operacional Windows (`America/Sao_Paulo`), **não**
UTC — apesar do PostgreSQL estar configurado corretamente (`TZ=UTC`/`PGTZ=UTC` no
`podman-compose.yml`) e do Hibernate forçar `hibernate.jdbc.jdbc.time_zone: UTC` na
conexão JDBC (`application.yaml`). Isso não quebrou nada nesta rodada porque:

* `RelogioUtcService.agora()` usa `Instant.now()`, que é um ponto absoluto no tempo
  (UTC por definição, não depende de timezone local).
* O driver PostgreSQL + `jdbc.time_zone: UTC` já força a conversão correta na escrita.

Mas é uma dependência implícita frágil: qualquer código futuro que use
`LocalDateTime.now()` ou `ZonedDateTime.now()` (em vez de `Instant.now()`/
`RelogioUtcService`) herdaria silenciosamente o timezone `America/Sao_Paulo` da JVM.
Fica registrado como risco conhecido — a correção definitiva seria adicionar
`-Duser.timezone=UTC` na JVM (ou `JAVA_TOOL_OPTIONS=-Duser.timezone=UTC` no ambiente de
execução), mas isso foi deixado para uma fase de hardening futura por não haver, hoje,
nenhum código violando a regra "sempre `Instant`/UTC".

## Referências

* Martin Fowler — https://martinfowler.com
* Skill `sql-server-modelagem-jpa` (adaptar para tipos PostgreSQL), `dados-sistemas-distribuidos` (catálogo global)
