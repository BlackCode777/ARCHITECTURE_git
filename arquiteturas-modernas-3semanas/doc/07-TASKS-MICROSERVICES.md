# 07 — TASKS: `biblioteca-microservices`

> Fase 2. Só começar com o monolito (doc 06) **verde**. Reaproveitar domínio,
> use cases e DTOs do monolito onde couber.

## Estrutura da Fase 2

Fica em `arquitetura-moderna-3semana/biblioteca-microservices/`:

```text
arquitetura-moderna-3semana/biblioteca-microservices/
├── pom.xml                 (POM pai — <packaging>pom</packaging>, <modules>)
├── podman-compose.yml      (4 bancos + 4 serviços + gateway)
├── api-gateway/
├── livro-service/
├── autor-service/
├── editora-service/
├── aluguel-service/
├── scripts/e2e.sh
└── README.md
```

Cada `*-service` é um projeto Spring Boot independente (módulo Maven do POM pai).

---

## Épico 0 — Scaffolding

> Tudo dentro de `arquitetura-moderna-3semana/biblioteca-microservices/`.

- [ ] **0.1** Gerar os **5 projetos no start.spring.io** (Maven · Java 25 · Spring Boot 4.1.1 ·
  Group `com.tesseracodelabs`): `api-gateway`, `livro-service`, `autor-service`,
  `editora-service`, `aluguel-service`. Descompactar cada `.zip` na pasta.
- [ ] **0.2** Criar à mão o `pom.xml` **pai** (`packaging=pom`, `<modules>` com os 5,
  `<parent>` = `spring-boot-starter-parent` 4.1.1, `<dependencyManagement>` com o BOM
  `spring-cloud-dependencies`). Apontar cada módulo `<parent>` para esse pom pai.
- [ ] **0.3** Dependências por serviço (marcar no Initializr onde possível, completar no pom):
  comuns: `webmvc`, `validation`, `actuator`, `springdoc-openapi`, `mapstruct`
  - relacionais (`livro`, `autor`, `editora`): `data-jpa`, `flyway-core`, driver
  - `aluguel-service`: `spring-boot-starter-data-mongodb`
  - serviços que chamam outros (`livro`, `aluguel`): `spring-cloud-starter-openfeign`, `resilience4j-spring-boot3`
  - `api-gateway`: `spring-cloud-starter-gateway`
  - `<dependencyManagement>` com `spring-cloud-dependencies` BOM no POM pai
- [ ] **0.4** `podman-compose.yml` completo (ver doc 08 §2): `postgres`, `sqlserver-autor`, `sqlserver-editora`, `mongo`, + 5 apps, rede `biblioteca-net`, healthchecks
- [ ] **0.5** Subir só os bancos: `podman-compose up -d postgres sqlserver-autor sqlserver-editora mongo`; validar cada um

---

## Épico 1 — `autor-service` (folha, sem dependências)

- [ ] **1.1** Copiar `autor/domain` + `autor/application` (use cases) do monolito
- [ ] **1.2** `infrastructure/persistence` — `AutorEntity` para **SQL Server** (`@Table(name="autores")`, sem schema; tipos `datetime2`, `nvarchar`); IDs `SEQUENCE` `allocationSize=50`
- [ ] **1.3** `application.yaml` — datasource SQL Server instância A (`jdbc:sqlserver://sqlserver-autor:1433;databaseName=autor_db;encrypt=false`), `open-in-view: false`, `ddl-auto: validate`
- [ ] **1.4** Flyway: `V1__create_autores.sql` (dialeto SQL Server)
- [ ] **1.5** `web/` — `AutorController` (porta 8082, rotas `/autores`), DTOs, WebMapper.
  Endpoints extra p/ contrato: `GET /autores/{id}/existe`, `GET /autores/{id}` (resumo)
- [ ] **1.6** `OpenApiConfig` — "Autor Service API"
- [ ] **1.7** Testes: domínio (reaproveitado), use case, `AutorRepositoryIT` (Testcontainers **mssql**), controller (`@WebMvcTest`)
- [ ] **1.8** `Containerfile` multi-stage
- [ ] **1.9** `podman-compose up -d autor-service`; testar via `http://localhost:8082/swagger-ui.html`

## Épico 2 — `editora-service` (folha)

- [ ] **2.1–2.9** Igual ao Épico 1, para `Editora`, **SQL Server instância B** (`sqlserver-editora:1433` mapeado p/ host `:1434`), `editora_db`, porta 8083.

## Épico 3 — `livro-service` (depende de autor + editora)

- [ ] **3.1** Copiar `livro/domain` + `livro/application` do monolito
- [ ] **3.2** `domain/GeneroLiterario.java` — **cópia local** (contrato)
- [ ] **3.3** `application/port/AutorGateway.java`, `EditoraGateway.java` — ports de saída (o use case usa isso, não sabe que é HTTP)
- [ ] **3.4** `infrastructure/persistence` — `LivroEntity` para **PostgreSQL** (`livro_db`); **sem FK** para autor/editora, só colunas `autor_id`, `editora_id` (Long)
- [ ] **3.5** `infrastructure/client`:
  - `AutorFeignClient` (`@FeignClient(name="autor-service", url="${clients.autor.url}")`)
  - `AutorFeignAdapter` implements `AutorGateway` — com `@CircuitBreaker` + fallback ("autor indisponível")
  - idem `EditoraFeignClient` / `EditoraFeignAdapter`
- [ ] **3.6** `application.yaml` — Postgres `livro_db`; `clients.autor.url=http://autor-service:8082`, `clients.editora.url=http://editora-service:8083`; Resilience4j (timeout 2s, retry 2, CB)
- [ ] **3.7** Flyway `V1__create_livros.sql` (Postgres) + índice `genero`, `autor_id`
- [ ] **3.8** `web/` — `LivroController` porta 8081, rotas `/livros`.
  Endpoints de contrato p/ aluguel-service:
  `GET /livros/{id}/disponibilidade`, `PATCH /livros/{id}/baixa-exemplar`, `PATCH /livros/{id}/retorno-exemplar`
- [ ] **3.9** Testes:
  - domínio + use case (mock `AutorGateway`/`EditoraGateway`)
  - `LivroRepositoryIT` (Testcontainers Postgres)
  - **contrato**: `AutorFeignAdapterTest` contra **WireMock** (200, 404, timeout → fallback)
  - controller `@WebMvcTest`
- [ ] **3.10** `Containerfile`; `podman-compose up -d livro-service`
- [ ] **3.11** Teste manual: criar livro via gateway; derrubar `autor-service` e ver o fallback

## Épico 4 — `aluguel-service` (depende de livro; MongoDB)

- [ ] **4.1** Copiar `aluguel/domain` do monolito **inteiro** (Aluguel, PoliticaTarifacao + subclasses, ContextoTarifacao, SeletorPolitica, StatusAluguel)
- [ ] **4.2** `domain/GeneroLiterario.java` — cópia local
- [ ] **4.3** `infrastructure/persistence` — `AluguelDocument` (`@Document(collection="alugueis")`), `AluguelMongoRepository` (`MongoRepository`), `AluguelMongoAdapter`, mapper.
  Índices: `locatario`, `status`, `dataDevolucaoPrevista` (criados via `@Indexed` ou `MongoTemplate` no startup)
- [ ] **4.4** `application/port/LivroGateway.java` — `disponibilidade(id)`, `baixarExemplar(id)`, `retornarExemplar(id)`
- [ ] **4.5** `infrastructure/client` — `LivroFeignClient` + `LivroFeignAdapter` (CB + fallback)
- [ ] **4.6** `AlugarUseCase` — **sem `@Transactional` distribuída**:
  1. `livroGateway.disponibilidade(livroId)` → se indisponível, 409
  2. `politica = seletor.escolher(genero)`; `taxa = politica.calcular(ctx)`
  3. `aluguelRepository.salvar(aluguel)` (Mongo)
  4. `try { livroGateway.baixarExemplar(livroId) } catch { aluguelRepository.remover(aluguel.id); throw }`  ← **compensação**
- [ ] **4.7** `DevolverUseCase` — atualiza documento; `livroGateway.retornarExemplar(livroId)` (compensação inversa se falhar)
- [ ] **4.8** `application.yaml` — Mongo `mongodb://mongo:27017/aluguel_db`; `clients.livro.url=http://livro-service:8081`; Resilience4j
- [ ] **4.9** `web/` — `AluguelController` porta 8084, rotas `/alugueis`
- [ ] **4.10** Testes:
  - `PoliticaTarifacaoTest` (reaproveitado, `@ParameterizedTest`)
  - `AlugarUseCaseTest` — mock `LivroGateway`; **caso de compensação** (baixaExemplar lança → aluguel é removido)
  - `AluguelRepositoryIT` (Testcontainers **MongoDB**)
  - contrato `LivroFeignAdapterTest` (WireMock)
- [ ] **4.11** `Containerfile`; `podman-compose up -d aluguel-service`

## Épico 5 — `api-gateway`

- [ ] **5.1** `application.yaml` com as 4 rotas (`/api/livros/**`, `/api/autores/**`, `/api/editoras/**`, `/api/alugueis/**`), `StripPrefix=1`, porta 8080 (ver doc 03 §6)
- [ ] **5.2** `CORS` liberado (estudo, sem front — mas deixa pronto)
- [ ] **5.3** Rota agregadora opcional: `/swagger-ui` listando os 4 specs (`springdoc` com `urls`)
- [ ] **5.4** `Containerfile`; `podman-compose up -d api-gateway`
- [ ] **5.5** Teste: `GET http://localhost:8080/api/autores` chega no autor-service

## Épico 6 — Integração e fechamento

- [ ] **6.1** `podman-compose down -v && podman-compose up -d` → **stack inteira** de pé, healthchecks verdes
- [ ] **6.2** Teste ponta a ponta via gateway (script `scripts/e2e.sh` ou coleção):
  criar autor → criar editora → criar livro → consultar livro (nome do autor resolvido via Feign) →
  alugar → ver taxa → devolver → listar atrasados
- [ ] **6.3** Teste de resiliência: parar `autor-service`, criar livro → fallback; parar `livro-service`, alugar → 502 + compensação (aluguel não fica órfão no Mongo)
- [ ] **6.4** `seeds/` — scripts de carga inicial por serviço (chamando as APIs)
- [ ] **6.5** `README.md` do projeto (já existe em `arquitetura-moderna-3semana/biblioteca-microservices/README.md` — atualizar se preciso)
- [ ] **6.6** Preencher a seção "cenários concretos" do doc 04 com números observados (latência do fluxo de aluguel mono × micro, nº de queries/chamadas)
- [ ] **6.7** Atualizar **graphify** com todos os arquivos dos microsserviços
- [ ] **6.8** Commit: `feat(microservices): sistema de reserva de livros — 4 serviços + gateway`

## Riscos e pontos de atenção

| Risco | Mitigação |
| --- | --- |
| SQL Server em contêiner é pesado (2 instâncias) | `MSSQL_MEMORY_LIMIT_MB`; subir sob demanda; máquina Podman com RAM suficiente |
| Testcontainers + Podman no Windows | `DOCKER_HOST=npipe:////./pipe/podman-machine-default`, `TESTCONTAINERS_RYUK_DISABLED=true` |
| Feign + porta errada entre contêiner e host | URLs de cliente usam **hostname do compose** (`autor-service:8082`), não `localhost` |
| Enum `GeneroLiterario` divergir entre serviços | tratar como contrato; se mudar, mudar em todos + versionar |
| Compensação não cobre todos os casos de falha | documentar limites; citar Outbox/Saga como evolução (fora do escopo) |
