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

* [ ] Aplicar `Auditoria` às entidades JPA do CRUD de Tarefas (`createdAt`/`updatedAt`)
* [ ] Garantir que o PostgreSQL/JVM rodem com timezone `UTC` (Podman: `TZ=UTC`)
* [ ] Testar serialização/deserialização de timestamp em diferentes TimeZones no JSON

## Referências

* Martin Fowler — https://martinfowler.com
* Skill `sql-server-modelagem-jpa` (adaptar para tipos PostgreSQL), `dados-sistemas-distribuidos` (catálogo global)
