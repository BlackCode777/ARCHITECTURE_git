# ADR-0001 — Estrutura de Serviços de Arquitetura no Projeto Roadmap

* **Status:** Aceito
* **Data:** 2026-08-03

## Contexto

O projeto `fundamentos-2semanas` é o CRUD de Tarefas da Fase 1 do roadmap de estudo
(ver [FASE-1-FUNDAMENTOS.md](FASE-1-FUNDAMENTOS.md)). O objetivo é usar esse único
CRUD como veículo para praticar 7 conceitos centrais de arquitetura distribuída:
CAP Theorem, REST, EDA, Arquitetura Hexagonal, Sincronização de Tempo (NTP), Load
Balance e Observabilidade/Métricas — representados no diagrama abaixo.

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

Era preciso decidir: (a) cada conceito vira um microsserviço Spring Boot separado
(multi-módulo, pom.xml e porta próprios) ou (b) cada conceito vira um pacote isolado
dentro do módulo Maven único já existente.

## Decisão

Adotar a opção **(b) — pacotes dentro do módulo Maven único**, um por conceito, seguindo
o padrão de nomenclatura e camadas hexagonais definido no catálogo global de skills
(`D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\.claude\CLAUDE.md`):

```text
com.tesseracode_labs.fundamentos_2semanas/
├── task/          ← domínio base do CRUD (Fase 1)
├── cap/            (Serviço 1 — CAP Theorem)
├── rest/           (Serviço 2 — REST)
├── eda/            (Serviço 3 — Event Driven Architecture)
├── hexagonal/       (Serviço 4 — Ports & Adapters, referência canônica)
├── ntp/             (Serviço 5 — Sincronização de Tempo)
├── lb/              (Serviço 6 — Load Balance)
├── metrics/         (Serviço 7 — Observabilidade/Métricas)
├── shared/
└── config/
```

Cada pacote recebeu um esqueleto de código mínimo mas compilável (validado com
`mvnw compile`), e um documento correspondente em `docs/0N-<servico>.md` explicando o
conceito, a estrutura interna e os próximos passos ligados à task do roadmap.

## Alternativas Consideradas

| Opção | Prós | Contras | Motivo da rejeição |
| --- | --- | --- | --- |
| Multi-módulo (7 projetos Maven, portas próprias) | Mais fiel a microsserviços reais; isolamento total de build e deploy | Setup inicial pesado (7 pom.xml, 7 podman-compose, 7 portas); atrito alto para uma fase de fundamentos onde o objetivo é aprender o conceito, não operar infraestrutura de múltiplos serviços | Adiado — pode ser revisitado na Fase 2 (Microservices) do roadmap |
| Somente documentação, sem código | Zero esforço de implementação agora | Não valida se a estrutura compila; conceitos como Hexagonal só fazem sentido quando há código real demonstrando o isolamento de camadas | Rejeitado — o usuário pediu estruturas de arquitetura, não apenas texto |
| Pacotes no módulo único (escolhida) | Baixo atrito, roda em um único processo/porta, permite focar no conceito, ainda assim compilável e testável isoladamente por pacote | Não é "multi-serviço" de verdade (mesmo processo, mesmo deploy) | Aceita conscientemente — adequada ao escopo da Fase 1 |

## Consequências

* Todos os 7 pacotes compartilham o mesmo `pom.xml`, `application.yaml` e processo JVM;
  não há isolamento de deploy nem de porta entre eles.
* Migração futura para multi-módulo (Fase 2 — Microservices) é possível sem reescrever
  o domínio, pois cada pacote já segue Dependency Rule (domain → application →
  infrastructure/web) e pode ser extraído para seu próprio módulo com o mínimo de
  fricção.
* **Regra de evolução:** toda nova classe adicionada a um pacote de serviço exige
  atualização do documento `docs/0N-<servico>.md` correspondente (ver
  [ARQUITETURA-SERVICOS.md](ARQUITETURA-SERVICOS.md), seção "Status de Implementação").
  Decisões que alterem a arquitetura (nova biblioteca, novo padrão de mensageria,
  mudança de multi-módulo) exigem um novo ADR (`ADR-000N`).
* Compilação validada com `mvnw compile` usando JDK 25 — todas as 21 classes novas
  geradas com sucesso.

## Referências

* [ARQUITETURA-SERVICOS.md](ARQUITETURA-SERVICOS.md) — índice geral dos 7 serviços
* [FASE-1-FUNDAMENTOS.md](FASE-1-FUNDAMENTOS.md) — roadmap original
* Skill `adr-architecture-decision-records`, `monolito-modular-vs-microservicos`,
  `hexagonal-ports-adapters` (catálogo global)
