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

* [ ] Simular queda do PostgreSQL (parar o container via Podman) e observar o
      comportamento real da API (timeout, erro 503, etc.)
* [ ] Registrar o impacto observado neste documento
* [ ] Criar ADR comparando os cenários CP x AP testados (ver
      [ADR-0001](ADR-0001-arquitetura-servicos.md))

## Referências

* Skill `dados-sistemas-distribuidos` (catálogo global) — CAP theorem, replicação, isolamento
* Martin Fowler — https://martinfowler.com
