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
* [InMemoryTarefaRepositoryAdapter.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/hexagonal/infrastructure/persistence/InMemoryTarefaRepositoryAdapter.java) — placeholder em memória
* [ConcluirTarefaController.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/hexagonal/web/ConcluirTarefaController.java) — `PATCH /hexagonal/tarefas/{id}/concluir`

## Próximos passos (Mini Projeto da Task 05)

* [ ] Substituir `InMemoryTarefaRepositoryAdapter` por um `TarefaJpaAdapter` real
      ligado ao PostgreSQL, mantendo o mesmo port `TarefaRepository`
* [ ] Aplicar o mesmo padrão ao restante do CRUD de Tarefas (`task/` no pacote base)
* [ ] Escrever teste unitário do `ConcluirTarefaUseCase` mockando `TarefaRepository`
      (skill `mockito-mocks-stubs`)

## Referências

* Alistair Cockburn — Hexagonal Architecture (Ports & Adapters)
* Martin Fowler — https://martinfowler.com
* Skill `hexagonal-ports-adapters`, `solid-clean-architecture`,
  `principios-componentes-arquitetura` (catálogo global)
