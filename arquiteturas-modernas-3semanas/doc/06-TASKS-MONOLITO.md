# 06 — TASKS: `biblioteca-monolito`

> Checklist executável da Fase 1. Marcar `[x]` ao concluir. Cada bloco termina
> com o projeto compilando e demonstrável.
>
> **STATUS (2026-09-10): Épicos 0–6 concluídos.** `./mvnw clean verify` →
> BUILD SUCCESS (84 unitários + 16 de integração). Registro do processo,
> decisões e desvios em [`10-IMPLEMENTACAO-MONOLITO.md`](10-IMPLEMENTACAO-MONOLITO.md).
> Única pendência: plugin JaCoCo com threshold (item 6.3 estendido).

## Épico 0 — Preparação

> O projeto mora em `arquitetura-moderna-3semana/biblioteca-monolito/`.

- [x] **0.1** Gerar o projeto no **start.spring.io**:
  - Project **Maven** · Language **Java** · Spring Boot **4.1.1** · Packaging **Jar** · Java **25**
  - Group `com.tesseracodelabs` · Artifact `biblioteca-monolito` (pacote gerado: `com.tesseracodelabs.biblioteca_monolito`)
  - Dependencies: **Spring Web**, **Spring Data JPA**, **PostgreSQL Driver**,
    **Flyway Migration**, **Validation**, **Spring Boot Actuator**, **Testcontainers**
  - Baixar o `.zip` e descompactar em `arquitetura-moderna-3semana/biblioteca-monolito/`
    (achatar se o zip criar subpasta com o nome do artifact)
- [x] **0.2** `pom.xml` já completado neste repo: `mapstruct` + `mapstruct-processor`
  (via `annotationProcessorPaths`), `springdoc-openapi-starter-webmvc-ui`,
  `spring-boot-testcontainers` + `testcontainers:junit-jupiter` + `testcontainers:postgresql`,
  `flyway-database-postgresql`. `spring-boot-devtools` removido. Sem Lombok.
- [x] **0.3** Classe main gerada `BibliotecaMonolitoApplication` no pacote
  `com.tesseracodelabs.biblioteca_monolito`. Os subpacotes de feature (`autor`, `editora`,
  `livro`, `aluguel`, `shared`, `config`) ficam abaixo dele.
- [x] **0.4** `application.yaml` conforme doc 02 §5 (datasource Postgres, `open-in-view: false`, `ddl-auto: validate`, Flyway on)
- [x] **0.5** `application-test.yaml` (Testcontainers; `spring.testcontainers.ryuk.enabled=false`)
- [x] **0.6** `podman-compose.yml` na raiz de `biblioteca-monolito/` com serviço `postgres` (ver doc 08)
- [x] **0.7** Subir Postgres: `podman-compose up -d postgres`; validar conexão

## Épico 1 — Fundação (`shared/` + `config/`)

- [x] **1.1** `shared/domain/EntidadeBase.java` — `@MappedSuperclass`, campos `id` (Long, SEQUENCE
  `allocationSize=50`), `criadoEm`, `atualizadoEm` (`@CreationTimestamp` / `@UpdateTimestamp`)
- [x] **1.2** `shared/domain/DomainException.java` + subclasses `RecursoNaoEncontradoException`, `RegraNegocioException`
- [x] **1.3** `shared/GeneroLiterario.java` (enum, 12 valores — ver doc 01 §2)
- [x] **1.4** `shared/web/GlobalExceptionHandler.java` — `@RestControllerAdvice`, converte exceções em `ProblemDetail` (RFC 9457)
- [x] **1.5** `shared/web/PageResponse.java` — `record PageResponse<T>(List<T> conteudo, int pagina, int tamanho, long total)`
- [x] **1.6** `config/OpenApiConfig.java` — `@Bean OpenAPI` com título "Biblioteca Monolito API", versão, descrição
- [x] **1.7** `config/JpaConfig.java` — `@EnableJpaAuditing`
- [x] **1.8** Migration `V0__extensoes_schemas.sql` — `CREATE SCHEMA IF NOT EXISTS autor/editora/catalogo/aluguel`

## Épico 2 — Módulo `autor` (fatia vertical)

- [x] **2.1** `autor/domain/Autor.java` — construtor validando invariantes (nome obrigatório, nascimento no passado), métodos de negócio, sem setter público
- [x] **2.2** `autor/domain/AutorRepository.java` — port: `salvar`, `buscarPorId`, `listar(page,size)`, `remover`, `existePorId`
- [x] **2.3** `autor/application/port/AutorConsultaPort.java` — API pública p/ outros módulos: `boolean existePorId(Long)`, `Optional<AutorResumo> buscarResumo(Long)`
- [x] **2.4** Use cases: `CriarAutorUseCase`, `AtualizarAutorUseCase`, `BuscarAutorUseCase`, `ListarAutoresUseCase`, `RemoverAutorUseCase` (todos `@Transactional` no que muda estado)
- [x] **2.5** `autor/infrastructure/AutorEntity.java` — `@Entity @Table(schema="autor", name="autores")`, herda `EntidadeBase`
- [x] **2.6** `autor/infrastructure/AutorJpaRepository.java` — `extends JpaRepository<AutorEntity, Long>`
- [x] **2.7** `autor/infrastructure/AutorPersistenceMapper.java` — MapStruct `Autor ↔ AutorEntity`
- [x] **2.8** `autor/infrastructure/AutorJpaAdapter.java` — implementa `AutorRepository` **e** `AutorConsultaPort`
- [x] **2.9** `autor/web/dto/` — `CriarAutorRequest`, `AtualizarAutorRequest`, `AutorResponse` (records + Bean Validation)
- [x] **2.10** `autor/web/AutorWebMapper.java` — MapStruct DTO ↔ domínio
- [x] **2.11** `autor/web/AutorController.java` — 5 endpoints sob `/api/autores`
- [x] **2.12** Migration `V1__schema_autor.sql` — tabela `autor.autores` + sequence
- [x] **2.13** Testes: `AutorTest` (domínio/invariantes), `CriarAutorUseCaseTest` (Mockito),
  `AutorJpaAdapterIT` (`@DataJpaTest` + Testcontainers), `AutorControllerTest` (`@WebMvcTest`)
- [x] **2.14** Validar no Swagger: criar autor, listar, buscar, atualizar, remover

## Épico 3 — Módulo `editora` (fatia vertical)

- [x] **3.1–3.14** Mesma estrutura do Épico 2, para `Editora` (nome, cnpj único e válido, cidade, site).
  Migration `V2__schema_editora.sql`. Port pública `EditoraConsultaPort`.

## Épico 4 — Módulo `livro` (integra autor + editora)

- [x] **4.1** `livro/domain/Livro.java` — invariantes (páginas > 0, exemplares coerentes, isbn, gênero), métodos `baixarExemplar()`, `retornarExemplar()`
- [x] **4.2** `livro/domain/LivroRepository.java` — port de persistência (+ `listar` com filtro por gênero/autor)
- [x] **4.3** `livro/application/port/LivroConsultaPort.java` — p/ o módulo aluguel: `Optional<LivroDisponibilidade> disponibilidade(Long)`, `void baixarExemplar(Long)`, `void retornarExemplar(Long)`
- [x] **4.4** Use cases: Criar (valida via `AutorConsultaPort` + `EditoraConsultaPort`), Atualizar, Buscar, Listar (filtros), Remover
- [x] **4.5** `livro/infrastructure/` — `LivroEntity` (`schema="catalogo"`, FK para `autor.autores` e `editora.editoras`), JpaRepository, Adapter, PersistenceMapper
- [x] **4.6** `livro/web/` — DTOs (record), `LivroController` sob `/api/livros`, WebMapper.
  `LivroResponse` traz `autorNome` e `editoraNome` resolvidos (via `AutorConsultaPort`)
- [x] **4.7** Migration `V3__schema_catalogo.sql` — tabela `catalogo.livros` + FKs + índice em `genero`, `autor_id`
- [x] **4.8** Testes: domínio, use case (mock dos ports de autor/editora), adapter IT, controller
- [x] **4.9** Swagger: criar livro apontando autor+editora existentes; filtro por gênero

## Épico 5 — Módulo `aluguel` (+ OOP de tarifação)

- [x] **5.1** `aluguel/domain/PoliticaTarifacao.java` — classe **abstract**, template method `calcular(ContextoTarifacao)`, `abstract valorDiario()`, hook `ajustar(...)`
- [x] **5.2** Subclasses: `TarifacaoPadrao`, `TarifacaoPorGenero`, `TarifacaoPromocional` (ver doc 01 §4)
- [x] **5.3** `aluguel/domain/ContextoTarifacao.java` — record imutável
- [x] **5.4** `aluguel/domain/SeletorPolitica.java` — devolve a política conforme regra (ex.: por gênero)
- [x] **5.5** `aluguel/domain/Aluguel.java` — factory `criar(...)`, métodos `devolver(LocalDate)`, `marcarAtraso()`; `StatusAluguel`; estado encapsulado
- [x] **5.6** `aluguel/domain/AluguelRepository.java` — port
- [x] **5.7** Use cases: `AlugarUseCase` (valida disponibilidade via `LivroConsultaPort`, calcula taxa, salva, baixa exemplar — **tudo numa `@Transactional`**), `DevolverUseCase`, `BuscarAluguelUseCase`, `ListarPorLocatarioUseCase`, `ListarAtrasadosUseCase`
- [x] **5.8** `aluguel/infrastructure/` — `AluguelEntity` (`schema="aluguel"`), JpaRepository, Adapter, PersistenceMapper
- [x] **5.9** `aluguel/web/` — DTOs, `AluguelController` sob `/api/alugueis`, WebMapper
- [x] **5.10** Migration `V4__schema_aluguel.sql`
- [x] **5.11** Testes:
  - `PoliticaTarifacaoTest` — `@ParameterizedTest` cobrindo as 3 políticas
  - `AlugarUseCaseTest` — mock `LivroConsultaPort`; caso livro indisponível
  - `AluguelTest` — devolução, atraso
  - `AluguelJpaAdapterIT`, `AluguelControllerTest`
  - `FluxoAluguelIT` — `@SpringBootTest` + Testcontainers: criar autor→editora→livro→alugar→devolver
- [x] **5.12** Swagger: alugar um livro, ver taxa calculada, devolver, listar atrasados

## Épico 6 — Fechamento do monolito

- [x] **6.1** Migration `V5__dados_exemplo.sql` — 3 autores, 2 editoras, 6 livros, 2 aluguéis
- [x] **6.2** `README.md` do projeto (já existe em `arquitetura-moderna-3semana/biblioteca-monolito/README.md` — atualizar se preciso)
- [x] **6.3** `./mvnw verify` verde ponta a ponta
- [x] **6.4** `podman-compose down -v && podman-compose up -d` → app sobe limpa, migrations aplicam
- [x] **6.5** Conferir regra "sem import cruzado entre módulos" (revisão manual ou teste ArchUnit opcional)
- [x] **6.6** `Containerfile` multi-stage do monolito + serviço `app` no `podman-compose.yml`
- [x] **6.7** Atualizar **graphify** com todos os arquivos do monolito
- [x] **6.8** Commit: `feat(monolito): sistema de reserva de livros — monolito modular completo`

## Convenções de nomenclatura (lembrete)

| Tipo | Sufixo |
| --- | --- |
| Use case | `UseCase` |
| Port de saída (persistência) | `Repository` |
| Port de saída (outro módulo/serviço) | `ConsultaPort` / `Gateway` |
| Adapter JPA | `JpaAdapter` |
| Entidade JPA | `Entity` |
| DTO entrada | `Request` |
| DTO saída | `Response` |
| Mapper MapStruct | `Mapper` |
| Teste unitário | `Test` |
| Teste integração | `IT` |
