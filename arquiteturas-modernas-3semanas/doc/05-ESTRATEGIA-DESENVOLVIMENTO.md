# 05 — Estratégia de Desenvolvimento

> Ordem de construção dos dois projetos e o raciocínio por trás dela. As tarefas
> executáveis estão em `06-TASKS-MONOLITO.md` e `07-TASKS-MICROSERVICES.md`.

## 1. Visão em 3 fases

Os dois projetos vivem em `arquitetura-moderna-3semana/`:
`biblioteca-monolito/` (Fase 1) e `biblioteca-microservices/` (Fase 2), ambos
gerados no start.spring.io.

```text
FASE 1 ─ Monolito modular          FASE 2 ─ Extração p/ microsserviços     FASE 3 ─ Análise
────────────────────────           ──────────────────────────────────     ──────────────
1. Gerar no Initializr + Podman    1. Gerar 5 projetos no Initializr        Rodar os dois,
2. shared/ + config                2. Copiar domínio/use cases do monolito   medir, comparar,
3. Módulo autor (vertical)         3. Trocar chamada de método por Feign     escrever conclusões
4. Módulo editora (vertical)       4. Um banco por serviço                   no doc 04
5. Módulo livro (usa autor+edit)   5. API Gateway
6. Módulo aluguel (+ tarifação)    6. Resiliência (timeout/retry/fallback)
7. Testes + Swagger + seed         7. Compensação no fluxo de aluguel
                                   8. podman-compose completo + testes de contrato
```

**Regra:** a Fase 2 só começa quando a Fase 1 estiver **verde** (build + testes + Swagger navegável).

## 2. Por que monolito primeiro

1. Descobrimos o domínio escrevendo-o. As fronteiras entre `livro` e `aluguel`
   ficam óbvias só depois de implementar as regras.
2. Se o monolito já nasce **modular** (import cruzado proibido, comunicação por
   porta), a extração vira mecânica: trocar `port` local por `FeignClient`.
3. Todo o modelo de domínio, os use cases, os testes de regra de negócio e os
   DTOs são **reaproveitados** na Fase 2 quase sem mudança.

## 3. Princípio de construção: fatia vertical (walking skeleton)

Cada módulo é entregue **inteiro** antes de passar para o próximo:
`domain → application → infrastructure → web → teste → endpoint no Swagger`.

Nada de "fazer todas as entidades, depois todos os repositórios". Ao terminar o
módulo `autor`, dá para criar e listar autor pela API. Isso mantém o projeto
sempre demonstrável.

Ordem dos módulos (dependência crescente):
```
autor   (não depende de ninguém)
editora (não depende de ninguém)
livro   (depende de autor + editora)
aluguel (depende de livro + tem a lógica de tarifação/OOP)
```

## 4. Arquitetura interna (idêntica nos dois projetos)

Hexagonal + DDD tático, por feature:

| Camada | Papel | Não pode |
| --- | --- | --- |
| `domain` | entidades, VOs, enums, regras, **ports** (interfaces) | conhecer Spring, JPA, HTTP |
| `application` | use cases, orquestração, `@Transactional` | conhecer JPA/HTTP diretamente (só ports) |
| `infrastructure` | adapters: JPA, Feign, mensageria | conter regra de negócio |
| `web` | controllers, DTOs (records), mappers de entrada | conter regra de negócio |

- **Sem Lombok.** DTOs são `record`. Entidades de domínio têm construtor + métodos
  de negócio + getters; estado muda só por método (encapsulamento).
- **MapStruct** para todo mapeamento entidade ↔ DTO e domínio ↔ entidade JPA.
- **OOP explícito**: `PoliticaTarifacao` abstrata + 3 subclasses (herança,
  polimorfismo, template method). Ver doc 01, seção 4.

## 5. Estratégia de dados

### Monolito
- 1 PostgreSQL, 4 schemas (`autor`, `editora`, `catalogo`, `aluguel`).
- Flyway com migrations numeradas `V1__…` a `V5__dados_exemplo.sql`.
- FKs reais entre `catalogo.livros` → `autor.autores` / `editora.editoras`.

### Microsserviços
- 4 bancos isolados. **Sem FK entre bancos** — só referência por ID.
- `livro-service` → PostgreSQL + Flyway.
- `autor-service` → SQL Server (instância A) + Flyway.
- `editora-service` → SQL Server (instância B) + Flyway.
- `aluguel-service` → MongoDB (sem migrations; índices criados no startup).
- Enum `GeneroLiterario` é **copiado** em cada serviço que precisa — é contrato, não biblioteca.

## 6. Estratégia de comunicação (só Fase 2)

1. Começar com **URLs fixas** (hostnames do `podman-compose`). Sem Eureka/Consul.
2. `OpenFeign` para cada dependência, encapsulado num **adapter** que implementa
   uma **port** do `application` — o use case não sabe que é HTTP.
3. `Resilience4j`: timeout, retry (2×, backoff), circuit breaker, fallback.
4. Fluxo de aluguel usa **compensação manual** (salvar aluguel → baixar exemplar →
   se falhar, remover aluguel). Documentar como "saga simplificada".

## 7. Estratégia de testes (detalhe no doc 09)

Pirâmide: ~70% unitário, ~25% integração, ~5% ponta a ponta.

| Tipo | Monolito | Microsserviços |
| --- | --- | --- |
| Unitário (domínio, tarifação, use cases com mock) | JUnit 5 + Mockito + AssertJ | idem, por serviço |
| Integração de persistência | `@DataJpaTest` + Testcontainers (Postgres) | Testcontainers do banco de cada serviço |
| Integração web | `@WebMvcTest` (mock do use case) | idem |
| Integração completa | `@SpringBootTest` + Testcontainers | por serviço |
| Contrato entre serviços | — | teste do `FeignClient` contra WireMock / serviço stub |
| Ponta a ponta | fluxo alugar→devolver via `TestRestTemplate` | fluxo via gateway com todos os contêineres de pé |

## 8. Estratégia de contêineres (detalhe no doc 08)

- **Podman** + `podman-compose`, nunca Docker.
- Monolito: `podman-compose.yml` sobe **PostgreSQL** (+ a app opcionalmente).
- Microsserviços: `podman-compose.yml` sobe **Postgres + SQL Server A + SQL Server B
  + MongoDB + os 4 serviços + o gateway**, numa rede interna.
- Healthchecks em todos os bancos; serviços com `depends_on: condition: service_healthy`.
- `Containerfile` multi-stage por serviço (build layered JAR → runtime JRE, usuário não-root).

## 9. Definição de "pronto" (Definition of Done)

Um módulo/serviço está pronto quando:
- [ ] Build passa (`./mvnw verify`)
- [ ] Testes unitários e de integração verdes
- [ ] Endpoints aparecem e funcionam no Swagger UI
- [ ] Migrations aplicam do zero (`podman-compose down -v` → `up`)
- [ ] `GlobalExceptionHandler` devolve `ProblemDetail` para erro de validação e "não encontrado"
- [ ] Documentado: qualquer desvio do padrão anotado no doc do projeto
- [ ] Arquivos novos/alterados **indexados no graphify**

## 10. Sequência recomendada de sessões de trabalho

| Sessão | Entrega |
| --- | --- |
| 1 | Fase 1 tasks 0–1: `biblioteca-monolito/` gerado no Initializr, `shared/`, `config/`, Podman + Postgres de pé |
| 2 | Módulo `autor` completo (vertical) + testes |
| 3 | Módulo `editora` completo + testes |
| 4 | Módulo `livro` completo (integra autor/editora) + testes |
| 5 | Módulo `aluguel` + `PoliticaTarifacao` + testes; monolito **fechado** |
| 6 | Fase 2: scaffolding dos 4 serviços + gateway, `podman-compose` completo |
| 7 | `autor-service` e `editora-service` (folhas) rodando com seus bancos |
| 8 | `livro-service` + Feign para autor/editora + resiliência |
| 9 | `aluguel-service` + Mongo + Feign para livro + compensação |
| 10 | Testes de contrato/integração, ponta a ponta via gateway, doc 04 finalizado |
