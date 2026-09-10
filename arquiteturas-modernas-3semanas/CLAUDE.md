# Projeto Arranjo Atlântica — Sistema de Reserva de Passagens

Sistema para controle de viagens, reservas, lista de espera e embarque da
Congregação Atlântica: Angular (frontend) + Spring Boot (API REST) + SQL Server
+ Podman.

> Catálogo global de skills e agentes: `D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\.claude`
> Antes de gerar código ou tomar decisões de arquitetura, leia as skills e/ou
> invoque os agentes indicados nas tabelas abaixo.
>
> **Reestruturação em andamento (desde 2026-09-01)**: o modelo de domínio está
> migrando para o "UML Corrigido v2". Fonte da verdade:
> `aoa-service/docs/estrutura-json-.../004-arquitetura-corrigida-parte-02-01 - UML Corrigido v2.json`.
> Plano e convenções: `aoa-service/docs/11`, `12`, `13` + `aoa-service/docs/adr/`.
> O "Modelo de Domínio (resumo)" no fim deste arquivo é o modelo **antigo** e
> será atualizado ao fim da reestruturação — até lá, seguir o doc `11`.

## Iniciando container podman
- podman-compose up -d sqlserver sqlserver-init

---

## Documentação obrigatória de implementações

> A partir de 2026-07-23, **toda implementação** (backend `aoa-service` ou
> frontend `aoa-web`) deve ser documentada em
> `aoa-service\docs\`, mesmo quando a mudança é só no frontend.

- **Nomenclatura**: `NN-NOME-DO-DOCUMENTO.md` — dois dígitos, zero à esquerda,
  seguidos de hífen e do título em maiúsculas separado por hífens (ex.:
  `01-AUTENTICACAO-JWT-LOGIN.md`). Ver `docs/01-AUTENTICACAO-JWT-LOGIN.md`
  como referência de formato.
- **Numeração**: sempre o próximo inteiro livre — conferir o maior prefixo
  existente em `aoa-service\docs\` antes de criar o arquivo.
- **Conteúdo mínimo**: resumo do que foi implementado, tabela/lista de
  arquivos alterados por camada (backend/frontend), decisões e desvios do
  padrão, e uma seção "Pendências / próximos passos" quando aplicável.
- Isso vale mesmo para tarefas pequenas — não pular a documentação por a
  mudança parecer trivial.

---

## Como este projeto usa o catálogo `.claude`

- **Skills** (conhecimento técnico, ler antes de codar): `D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\.claude\skills\`
  - Índice completo: [CLAUDE.md](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\.claude\skills\CLAUDE.md)
  - Guia de uso / fluxos por tipo de projeto: [COMO-USAR.md](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\.claude\skills\COMO-USAR.md)
- **Agentes** (subagentes especializados, invocar via `Agent` tool): `D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\.claude\agents\`
  - Cada agente espelha uma skill (mesmo nome) e referencia a skill canônica no próprio arquivo.
  - Agentes adicionais (sem skill 1:1): performance (Big O), DevOps/DevSecOps, segurança Spring, mapeamento/logística.

---

## Stack do Projeto

| Camada | Tecnologia |
| --- | --- |
| Linguagem | Java 21 LTS (mínimo) → Java 25 (alvo) |
| Framework Backend | Spring Boot 4.x / Spring Framework 7 |
| Frontend | Angular 22 (standalone components, zoneless, Vitest) |
| Banco Principal | SQL Server 2022 — `arranjo-atlantica-db` |
| Container Runtime | Podman (rootless, Windows) — nunca Docker Desktop |
| Build | Maven (primário) |
| Arquitetura | Monólito Modular · Hexagonal (Ports & Adapters) + DDD tático |

---

## Regras Inegociáveis

- **ID de entidades JPA**: PK numérica → sempre `SEQUENCE` com `allocationSize=50`,
  nunca `IDENTITY`. PK `UUID` → gerada pela aplicação (`GenerationType.UUID`),
  coluna `UNIQUEIDENTIFIER`. Este projeto usa **`UUID`** em todas as entidades
  (`shared/domain/EntidadeBase`) — ver ADR-0001.
- **Testes de integração**: sempre Testcontainers com banco real — nunca H2 em memória
- **Container runtime**: sempre `podman` — nunca `docker`
- **JUnit**: sempre JUnit 5
- **Frontend testes**: Vitest para Angular
- **open-in-view**: sempre `false`
- **ddl-auto em produção**: sempre `validate`
- **Credenciais**: sempre variáveis de ambiente em prod — nunca hardcode
- **Actuator**: `health` público, resto exige `ADMIN`
- **Reserva vs. Passagem**: `Reserva` é o cabeçalho da solicitação; `ReservaPassageiro`
  é a passagem individual de cada pessoa (uma reserva pode conter vários passageiros)
- **Assento único por ônibus ativo**: índice exclusivo filtrado em
  `reserva_passageiro (viagem_veiculo_id, assento_id)` para status `SOLICITADA/CONFIRMADA/EMBARCADA`
- **Pessoa vs. Usuário**: entidades separadas — nem todo passageiro acessa o sistema
- **Mapeamento entidade ↔ DTO**: sempre **MapStruct** (compile-time) — nunca
  ModelMapper/Dozer/Orika, nunca conversão manual nova em service/controller.
  Ver ADR-0002 e `aoa-service/docs/12`.

---

## Convenção de nomenclatura (backend e frontend)

> Detalhe completo + inventário de rename: `aoa-service/docs/12-CONVENCAO-NOMENCLATURA-E-MAPPERS.md`.

Toda classe carrega **sufixo de tipo** e vive num **pacote/pasta de feature**.
O nome é **idêntico** dos dois lados (Java e TypeScript) quando representa o
mesmo conceito.

| Papel | Sufixo | Exemplo |
| --- | --- | --- |
| Entidade JPA | `Entity` | `PessoaEntity` |
| DTO de entrada | `RequestDto` | `PessoaRequestDto` |
| DTO de saída | `ResponseDto` | `PessoaResponseDto` |
| DTO interno / resumo | `Dto` | `AssentoMapaDto` |
| Enum de domínio | *(sem sufixo)* | `StatusReserva` |
| Repositório | `Repository` | `PessoaRepository` |
| Serviço de aplicação | `Service` | `PessoaService` |
| Controller REST | `Controller` | `PessoaController` |
| Mapper MapStruct | `Mapper` | `PessoaMapper` |
| Exceção | `Exception` | `PassageiroDuplicadoException` |

- Exceção: `EntidadeBase` (`@MappedSuperclass`) fica **sem** sufixo.
- Nome de **tabela/coluna SQL não muda** por causa do sufixo —
  `@Table(name = "pessoas")` em `PessoaEntity`.
- Frontend: `<feature>/models/<nome>-request.dto.ts` exporta
  `interface <Nome>RequestDto` com o **mesmo shape** do record Java.
  `<Nome>Model` só quando a tela precisa de estado que não vem do backend.
- Pacote backend: `<modulo>/{domain,application,infrastructure,web}` — módulos
  `cadastro`, `seguranca`, `evento`, `viagem`, `transporte`, `reserva`,
  `financeiro`, `operacao`. Mapper mora em `web`, **nunca injeta `Repository`**.

---

## Decisões arquiteturais (ADRs)

Registradas em `aoa-service/docs/adr/` (índice: `LEIAME.md`). ADR aceito não
se edita — cria-se um novo que o substitui.

| # | Decisão |
| --- | --- |
| ADR-0001 | Monólito Modular mantido (não microsserviços) + PK `UUID` gerada pela aplicação |
| ADR-0002 | MapStruct como padrão de mapeamento entidade ↔ DTO |

---

## Skills a consultar por área

| Área | Skill (arquivo canônico) |
| --- | --- |
| Arquitetura hexagonal / ports & adapters | [hexagonal-ports-adapters](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeArchitecture\hexagonal-ports-adapters\SKILL.md) |
| DDD (Reserva/ReservaPassageiro como agregados) | [ddd-domain-driven-design](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeArchitecture\ddd-domain-driven-design\SKILL.md) |
| SOLID / Clean Architecture | [solid-clean-architecture](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeArchitecture\solid-clean-architecture\SKILL.md) |
| Monólito modular vs. microsserviços | [monolito-modular-vs-microservicos](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeArchitecture\monolito-modular-vs-microservicos\SKILL.md) |
| ADRs (decisões como "por que monólito modular") | [adr-architecture-decision-records](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeArchitecture\adr-architecture-decision-records\SKILL.md) |
| Estrutura do projeto Spring Boot | [spring-boot-estrutura-projeto](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeCode\spring-boot-estrutura-projeto\SKILL.md) |
| Java moderno (Records para DTOs, Virtual Threads) | [java-moderno-25](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeCode\java-moderno-25\SKILL.md) |
| JPA/Hibernate (entidades Pessoa, Viagem, Reserva...) | [spring-data-jpa-hibernate](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeCode\spring-data-jpa-hibernate\SKILL.md) |
| Autenticação/perfis (Administrador/Secretário/Passageiro) | [spring-security-essencial](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeCode\spring-security-essencial\SKILL.md) |
| Erros e validação (ProblemDetail) | [tratamento-excecoes-validacao](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeCode\tratamento-excecoes-validacao\SKILL.md) |
| SQL Server (modelagem, índices únicos filtrados) | [sql-server-modelagem-jpa](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeDB\sql-server-modelagem-jpa\SKILL.md) |
| Migrations (`arranjo-atlantica-db`) | [flyway-liquibase-versionamento](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeDB\flyway-liquibase-versionamento\SKILL.md) |
| Ambiente local (Podman + SQL Server) | [containers-bancos-dev](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeDB\containers-bancos-dev\SKILL.md) |
| Testes unitários | [junit5-fundamentos](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeTests\junit5-fundamentos\SKILL.md) |
| Mocks | [mockito-mocks-stubs](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeTests\mockito-mocks-stubs\SKILL.md) |
| Testes de integração com banco real | [testcontainers-integracao](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeTests\testcontainers-integracao\SKILL.md) |
| `@SpringBootTest` / `@WebMvcTest` | [spring-boot-test-integracao](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeTests\spring-boot-test-integracao\SKILL.md) |
| Cobertura de testes | [jacoco-cobertura](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeTests\jacoco-cobertura\SKILL.md) |
| Estrutura Angular (dashboard, standalone) | [angular-estrutura-projeto](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeDesign\angular-estrutura-projeto\SKILL.md) |
| Signals (estado do mapa de assentos) | [angular-signals-reatividade](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeDesign\angular-signals-reatividade\SKILL.md) |
| Consumo da API REST | [angular-services-http](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeDesign\angular-services-http\SKILL.md) |
| Design responsivo (mapa de assentos, dashboard) | [angular-design-visual-responsivo](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeDesign\angular-design-visual-responsivo\SKILL.md) |
| Testes Angular | [angular-testes](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeDesign\angular-testes\SKILL.md) |
| Fundamentos Podman (Windows) | [podman-fundamentos-windows](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeInfra\podman-fundamentos-windows\SKILL.md) |
| `podman-compose` (frontend + backend + sqlserver) | [podman-compose-bancos-dev](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeInfra\podman-compose-bancos-dev\SKILL.md) |
| Dockerfile/Containerfile multistage da API | [dockerfile-spring-boot-multistage](D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\ClaudeInfra\dockerfile-spring-boot-multistage\SKILL.md) |

---

## Agentes a invocar por tarefa

Agentes ficam em `D:\PROGRAMACAO\03_JAVA\02-AI-claude-code\.claude\agents\` (nome do
arquivo = nome do agente). Use o `Agent` tool para tarefas amplas ou multi-arquivo;
para consulta pontual, prefira ler a skill correspondente diretamente.

| Tarefa | Agente |
| --- | --- |
| Desenhar/isolar camadas domain/application/infrastructure/web | `hexagonal-ports-adapters` |
| Modelar agregados (Reserva, ReservaPassageiro, ListaEspera) | `ddd-domain-driven-design` |
| Revisar SecurityConfig, JWT, CORS, Actuator antes de produção | `spring-boot-security-agent` |
| Entidades JPA / SQL Server (SEQUENCE, índices únicos filtrados) | `spring-data-jpa-hibernate`, `sql-server-modelagem-jpa` |
| Testes de integração com Testcontainers + Podman | `testcontainers-integracao` |
| Componentes Angular (dashboard, mapa de assentos) | `angular-estrutura-projeto`, `angular-signals-reatividade` |
| Diagnosticar endpoint lento (ex.: listagem de reservas) | `api-performance-agent` |
| Analisar/otimizar complexidade (N+1 em Viagem→Reserva→Passageiro) | `big-o-analyzer-agent`, `big-o-optimizer-agent` |
| Code review focado em performance antes de merge | `code-reviewer-big-o-agent` |
| Pipeline CI/CD (build, test, deploy) | `cicd-pipeline-agent`, `devops-engineer-agent` |
| Auditoria de segurança/dependências antes de release | `security-audit-agent`, `dependency-security-agent`, `devsecops-engineer-agent` |
| Observabilidade (logs, métricas, health checks) | `observability-agent` |
| Estratégia de release/rollback/migração de banco | `deployment-release-agent` |
| Documentar decisão arquitetural (ex.: monólito modular) | `adr-architecture-decision-records` |

---

## Modelo de Domínio (resumo)

> ⚠️ **Modelo antigo (v1).** Está sendo substituído pelo "UML Corrigido v2"
> (novas entidades `Evento`, `EventoDia`, `ReservaDia`; módulo `financeiro`;
> assento/ponto de embarque por dia; `Viagem` → `EventoDia`). Enquanto a
> reestruturação não termina, seguir `aoa-service/docs/11` e o JSON fonte.
> Esta seção será reescrita ao fim da Etapa B do doc `13`.

Entidades principais e suas relações — ver diagramas ER/UML completos na conversa de
especificação do projeto:

```
Congregacao 1──N Familia 1──N Pessoa
Pessoa 1──0..1 Usuario N──N Perfil
Pessoa 1──0..1 Motorista

Viagem 1──N ViagemVeiculo N──1 Veiculo 1──N Assento
Viagem 1──N Reserva 1──N ReservaPassageiro
ReservaPassageiro N──1 Pessoa
ReservaPassageiro 1──0..1 ListaEspera
ReservaPassageiro 1──0..1 Embarque
```

Decisões de modelagem fixadas (não renegociar sem ADR):

1. `Pessoa` ≠ `Usuario` — nem todo passageiro acessa o sistema.
2. `Reserva` ≠ `ReservaPassageiro` — uma reserva pode conter vários passageiros (família).
3. `Assento` pertence ao `Veiculo` (numerado 1–46 no ônibus atual).
4. `Viagem` e `Veiculo` se associam via `ViagemVeiculo` (permite múltiplos ônibus por viagem).
5. `Embarque` e `ListaEspera` pertencem à `ReservaPassageiro` (passagem individual), não à `Reserva`.
6. Restrição de assento único ativo e de passageiro único ativo por viagem via índice único filtrado no SQL Server.

---

## Comandos Frequentes

```powershell
# Backend
.\mvnw test
.\mvnw verify

# Frontend
ng serve
ng test

# Infraestrutura local
podman machine start
podman-compose -f podman-compose.yml up -d
podman-compose down
```
