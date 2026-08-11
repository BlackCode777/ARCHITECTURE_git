# Serviço 1 — CAP Theorem

> Parte de [ARQUITETURA-SERVICOS.md](ARQUITETURA-SERVICOS.md). Task 02 do roadmap
> ([FASE-1-FUNDAMENTOS.md](FASE-1-FUNDAMENTOS.md)).

## Conceito

O **CAP Theorem** (Brewer, 2000) afirma que um sistema distribuído não pode garantir
simultaneamente as três propriedades a seguir quando ocorre uma partição de rede —
apenas duas por vez:

* **Consistency (C)** — toda leitura recebe a escrita mais recente ou um erro.
* **Availability (A)** — toda requisição recebe uma resposta (sem garantia de ser a
  mais recente).
* **Partition Tolerance (P)** — o sistema continua operando mesmo com falha de
  comunicação entre nós.

Como partições de rede são inevitáveis em sistemas distribuídos reais, a escolha
prática é entre **CP** (consistência forte, sacrificando disponibilidade durante a
partição) e **AP** (disponibilidade, aceitando consistência eventual). PostgreSQL em
instância única, por padrão, tende a CP; réplicas assíncronas tendem a AP para leitura.

## Estrutura de Pastas

```text
cap/
├── domain/
│   └── ConsistencyMode.java        ← enum STRONG / EVENTUAL
├── application/
│   └── SimularParticaoUseCase.java ← caso de uso que expõe o modo ativo
└── web/
    └── CapDemoController.java      ← GET /cap/simular-particao
```

## O que foi criado

* [ConsistencyMode.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/cap/domain/ConsistencyMode.java) — enum que representa o trade-off C x A.
* [SimularParticaoUseCase.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/cap/application/SimularParticaoUseCase.java) — caso de uso didático.
* [CapDemoController.java](../fundamentos-2semanas/src/main/java/com/tesseracode_labs/fundamentos_2semanas/cap/web/CapDemoController.java) — endpoint `GET /cap/simular-particao`.

## Próximos passos (Mini Projeto da Task 02)

* [x] Simular queda do PostgreSQL (parar o container via Podman) e observar o
      comportamento real da API (timeout, erro 503, etc.) — ver
      [09-tasks-EDA-CAP-REST.md](09-tasks-EDA-CAP-REST.md)
* [x] Registrar o impacto observado neste documento — detalhes completos em
      [09-tasks-EDA-CAP-REST.md](09-tasks-EDA-CAP-REST.md#3-task-02--cap-theorem-observado-empiricamente-serviço-1)
* [x] Criar ADR comparando os cenários CP x AP testados — ver
      [ADR-0002](ADR-0002-cap-503-particao.md)

**Resumo do experimento (04/08/2026):** com o CRUD real de Tarefas rodando sobre
PostgreSQL, derrubar o container fez os endpoints de negócio travarem ~30s (timeout do
HikariCP) antes de retornar 503, enquanto `/actuator/health` respondeu `DOWN` de forma
imediata. Nenhuma escrita parcial ocorreu — a API confirmou o comportamento **CP**
(Consistency + Partition Tolerance, sacrificando Availability). Detalhes completos e a
correção do `GlobalExceptionHandler` em [09-tasks-EDA-CAP-REST.md](09-tasks-EDA-CAP-REST.md)
e [ADR-0002](ADR-0002-cap-503-particao.md).

## Referências

* Skill `dados-sistemas-distribuidos` (catálogo global) — CAP theorem, replicação, isolamento
* Martin Fowler — https://martinfowler.com
