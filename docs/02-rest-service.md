# Serviço 2 — REST / HTTP / RPC

> Parte de [ARQUITETURA-SERVICOS.md](ARQUITETURA-SERVICOS.md). Task 03 do roadmap
> ([FASE-1-FUNDAMENTOS.md](FASE-1-FUNDAMENTOS.md)).

## Conceito

* **REST** — estilo arquitetural orientado a recursos, stateless, usando verbos HTTP
  (GET/POST/PUT/PATCH/DELETE) e códigos de status para comunicar semântica.
* **HTTP** — protocolo de transporte subjacente ao REST.
* **RPC** — chamada de procedimento remoto orientada a ação/método (ex.: `criarPedido()`),
  em vez de recurso.
* **gRPC** — implementação moderna de RPC sobre HTTP/2 com Protocol Buffers, usado
  tipicamente em comunicação interna entre microsserviços (baixa latência).

**Quando usar cada um:** REST para APIs públicas/externas (legibilidade, cache HTTP,
ferramentas maduras); gRPC para comunicação interna serviço-a-serviço de alta performance
onde o contrato é fortemente tipado e ambos os lados são controlados pelo mesmo time.

## Estrutura de Pastas

```text
rest/
├── domain/
│   └── StatusResponse.java          ← DTO de resposta (record)
├── application/
│   └── ConsultarStatusUseCase.java
└── web/
    └── RestDemoController.java      ← GET /rest/status
```

## O que foi criado

* [StatusResponse.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/rest/domain/StatusResponse.java)
* [ConsultarStatusUseCase.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/rest/application/ConsultarStatusUseCase.java)
* [RestDemoController.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/rest/web/RestDemoController.java) — endpoint `GET /rest/status`

## Próximos passos (Mini Projeto da Task 03)

* [x] Expandir para o CRUD completo de Tarefas com verbos REST corretos — ver
      [09-tasks-EDA-CAP-REST.md](09-tasks-EDA-CAP-REST.md)
* [x] Implementar OpenAPI/Swagger (`springdoc-openapi-starter-webmvc-ui`) — já
      configurado desde [08-infraestrutura-base.md](08-infraestrutura-base.md),
      endpoints do CRUD anotados com `@Tag`/`@Operation`
* [ ] Criar client HTTP (`WebClient`) para testar chamadas entre serviços — adiado
      para quando houver um segundo serviço real a consumir (Fase 2 — Microservices)
* [x] Documentar o contrato (request/response) de cada endpoint — via `/v3/api-docs`
      (Swagger vivo) e tabela de endpoints em
      [09-tasks-EDA-CAP-REST.md](09-tasks-EDA-CAP-REST.md#endpoints-verbos-e-status-corretos)

**Resumo do que foi implementado (04/08/2026):** CRUD REST completo (`POST/GET/PUT/PATCH/DELETE
/rest/tarefas`) com `TarefaEntity` JPA persistindo em PostgreSQL, DTOs `Request`/`Response`,
validação Bean Validation e erros no formato RFC 9457 (`ProblemDetail`). Detalhes e
resultados dos testes via curl em [09-tasks-EDA-CAP-REST.md](09-tasks-EDA-CAP-REST.md).

## Referências

* Martin Fowler — Richardson Maturity Model, https://martinfowler.com
* Skill `spring-boot-estrutura-projeto`, `tratamento-excecoes-validacao` (catálogo global)
