# Serviço 4 — Arquitetura Hexagonal (Ports & Adapters)

> Parte de [ARQUITETURA-SERVICOS.md](ARQUITETURA-SERVICOS.md). Task 05 do roadmap
> ([FASE-1-FUNDAMENTOS.md](FASE-1-FUNDAMENTOS.md)). Referência canônica para os demais
> serviços — ver skill `hexagonal-ports-adapters` no catálogo global.

## Conceito

A **Arquitetura Hexagonal** (Alistair Cockburn) isola o domínio de qualquer detalhe de
infraestrutura (framework, banco, web) através de **portas** (interfaces) e
**adaptadores** (implementações):

* **Domain Isolation** — o domínio (`Tarefa.java`) não conhece Spring, JPA ou HTTP.
* **Port** — contrato. Port de entrada (*driving*) é chamado de fora para dentro
  (ex.: `ConcluirTarefaPort`); port de saída (*driven*) é implementado por um adapter
  de infraestrutura (ex.: `TarefaRepository`).
* **Adapter** — implementação concreta de um port. Adapter de entrada = controller;
  adapter de saída = persistência, mensageria, API externa.
* **Dependency Rule** — as dependências sempre apontam para dentro (`web`/`infrastructure`
  dependem de `application`, que depende de `domain`; nunca o contrário).

## Estrutura de Pastas

```text
hexagonal/
├── domain/
│   └── Tarefa.java                              ← entidade pura, sem anotações
├── application/
│   ├── port/
│   │   ├── in/
│   │   │   └── ConcluirTarefaPort.java          ← driving port
│   │   └── out/
│   │       └── TarefaRepository.java            ← driven port
│   └── ConcluirTarefaUseCase.java               ← implementa o driving port
├── infrastructure/
│   └── persistence/
│       └── InMemoryTarefaRepositoryAdapter.java ← implementa o driven port
└── web/
    └── ConcluirTarefaController.java            ← adapter de entrada (depende só do Port)
```

## O que foi criado

* [Tarefa.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/hexagonal/domain/Tarefa.java) — entidade de domínio pura
* [ConcluirTarefaPort.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/hexagonal/application/port/in/ConcluirTarefaPort.java)
* [TarefaRepository.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/hexagonal/application/port/out/TarefaRepository.java)
* [ConcluirTarefaUseCase.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/hexagonal/application/ConcluirTarefaUseCase.java)
* [TarefaJpaAdapter.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/hexagonal/infrastructure/persistence/TarefaJpaAdapter.java) — adapter real, persiste no PostgreSQL (ver seção abaixo)
* [ConcluirTarefaController.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/hexagonal/web/ConcluirTarefaController.java) — `PATCH /hexagonal/tarefas/{id}/concluir`

## `TarefaJpaAdapter` — do placeholder ao PostgreSQL real (15/08/2026)

O `InMemoryTarefaRepositoryAdapter` foi **removido** e substituído por
`TarefaJpaAdapter`, que implementa o mesmo driven port (`hexagonal.application.port.out.TarefaRepository`)
sem que `ConcluirTarefaUseCase` ou o domínio `Tarefa` precisassem mudar uma linha —
prova prática da Dependency Rule.

```text
hexagonal.web.ConcluirTarefaController
        │  depende de
        ▼
hexagonal.application.port.in.ConcluirTarefaPort
        │  implementado por
        ▼
hexagonal.application.ConcluirTarefaUseCase
        │  depende de
        ▼
hexagonal.application.port.out.TarefaRepository   ← driven port, inalterado
        │  implementado por
        ▼
hexagonal.infrastructure.persistence.TarefaJpaAdapter
        │  delega para
        ▼
rest.application.TarefaRepository (Spring Data JPA)  ← reaproveitado do CRUD REST
        │  persiste
        ▼
PostgreSQL — tabela "tarefas" (mesma do db/migration/V1__create_table_tarefas.sql)
```

O adapter **traduz** entre `TarefaEntity` (JPA, pacote `rest.domain`) e `Tarefa`
(domínio puro, pacote `hexagonal.domain`) — o domínio hexagonal nunca importa uma
classe anotada com `@Entity`. Reaproveitar o `rest.application.TarefaRepository`
(Spring Data) em vez de escrever um `EntityManager`/JDBC próprio evitou duplicar
configuração de conexão e manteve uma única fonte de verdade para a tabela `tarefas`.

### Validado via curl (mesma tabela, dois caminhos de código)

```text
POST /rest/tarefas {"titulo":"Testar adapter hexagonal JPA"} → 201 {"id":2,...}

# usa o caminho 100% hexagonal (port → use case → adapter):
PATCH /hexagonal/tarefas/2/concluir → 200

# confirma a persistência via o caminho REST comum, provando que é a mesma linha:
GET /rest/tarefas/2 → 200 {"id":2,...,"concluida":true,"updatedAt":"...atualizado..."}
```

### Gap encontrado (não corrigido nesta rodada)

Ao testar com um ID inexistente, `TarefaJpaAdapter.salvar()` lança
`IllegalStateException` (não a `TarefaNaoEncontradaException` usada pelo CRUD REST),
e o `GlobalExceptionHandler` não a captura — o cliente recebe `500` genérico em vez de
`404`. Diferente do achado do CAP (ver [ADR-0002](ADR-0002-cap-503-particao.md)), aqui
o domínio hexagonal tem sua própria exceção de "não encontrado" implícita
(`orElseThrow` em `ConcluirTarefaUseCase`, hoje um `IllegalArgumentException`) que
diverge da exceção usada pelo `TarefaJpaAdapter.salvar()`. Fica registrado como
pendência: unificar as exceções de "recurso não encontrado" entre os pacotes
`hexagonal` e `rest`, ou aceitar que cada serviço didático tenha sua própria hierarquia
de erro (a decidir em um ADR futuro se o projeto migrar para multi-módulo).

## Próximos passos (Mini Projeto da Task 05)

* [x] Substituir `InMemoryTarefaRepositoryAdapter` por um `TarefaJpaAdapter` real
      ligado ao PostgreSQL, mantendo o mesmo port `TarefaRepository`
* [ ] Aplicar o mesmo padrão ao restante do CRUD de Tarefas (`task/` no pacote base)
* [ ] Escrever teste unitário do `ConcluirTarefaUseCase` mockando `TarefaRepository`
      (skill `mockito-mocks-stubs`)
* [ ] Unificar tratamento de "tarefa não encontrada" entre `hexagonal` e `rest` (gap
      descrito acima)

## Referências

* Alistair Cockburn — Hexagonal Architecture (Ports & Adapters)
* Martin Fowler — https://martinfowler.com
* Skill `hexagonal-ports-adapters`, `solid-clean-architecture`,
  `principios-componentes-arquitetura` (catálogo global)
