# Graph Report - arquiteturas-modernas-3semanas  (2026-09-10)

## Corpus Check
- Corpus is ~37,758 words - fits in a single context window. You may not need a graph.

## Summary
- 953 nodes · 2718 edges · 45 communities (30 shown, 15 thin omitted)
- Extraction: 85% EXTRACTED · 15% INFERRED · 0% AMBIGUOUS · INFERRED: 398 edges (avg confidence: 0.81)
- Token cost: 91,253 input · 0 output

## Community Hubs (Navigation)
- Use Cases: Autor + Aluguel
- Use Cases: Editora
- Config do Monolito (yaml, podman, Testcontainers)
- Adapters JPA e Persistence Mappers
- Aluguel (dominio) e AluguelTest
- Tarifacao: PoliticaTarifacao + subclasses
- Listagem paginada e Repositories (ports)
- ConsultaPorts e Use Cases de Livro
- Diagrama-Fonte do Estudo
- AluguelEntity e StatusAluguel
- Entities JPA (Autor/Editora base)
- LivroEntity
- Use Cases de Aluguel + Web Mapper
- AluguelController e Aluguel
- Livro (dominio)
- AluguelRepository e adapters
- GeneroLiterario e LivroConsultaPort
- Arquitetura de Microsservicos (Fase 2)
- Testes de Integracao (IT) e helpers
- EditoraEntity
- Community 20
- Community 21
- Community 22
- Community 23
- Community 24
- Community 25
- Community 26
- Community 27
- Community 28
- Community 29
- Community 30
- Community 31
- Community 32
- Community 33
- Community 34
- Community 35
- Community 36
- Community 37
- Community 38
- Community 39
- Community 40
- Community 41
- Community 42
- Community 43
- Community 44

## God Nodes (most connected - your core abstractions)
1. `GeneroLiterario` - 56 edges
2. `Livro` - 50 edges
3. `Aluguel` - 47 edges
4. `RegraNegocioException` - 44 edges
5. `Autor` - 35 edges
6. `Editora` - 35 edges
7. `LivroEntity` - 33 edges
8. `AutorRepository` - 31 edges
9. `AluguelEntity` - 29 edges
10. `EditoraRepository` - 28 edges

## Surprising Connections (you probably didn't know these)
- `Modulo aluguel (monolito)` --semantically_similar_to--> `aluguel-service (servico)`  [INFERRED] [semantically similar]
  doc/02-ARQUITETURA-MONOLITO.md → arquitetura-moderna-3semana/biblioteca-microservices/README.md
- `Monolith vs Microservices Architecture Diagram` --semantically_similar_to--> `Monolith vs Microservices Architecture Diagram (SVG)`  [INFERRED] [semantically similar]
  Diagrama sem nome.drawio.png → Diagrama sem nome.drawio.svg
- `Modulo livro (monolito)` --semantically_similar_to--> `livro-service (servico)`  [INFERRED] [semantically similar]
  doc/02-ARQUITETURA-MONOLITO.md → arquitetura-moderna-3semana/biblioteca-microservices/README.md
- `Modulo autor (monolito)` --semantically_similar_to--> `autor-service (servico)`  [INFERRED] [semantically similar]
  doc/02-ARQUITETURA-MONOLITO.md → arquitetura-moderna-3semana/biblioteca-microservices/README.md
- `Modulo editora (monolito)` --semantically_similar_to--> `editora-service (servico)`  [INFERRED] [semantically similar]
  doc/02-ARQUITETURA-MONOLITO.md → arquitetura-moderna-3semana/biblioteca-microservices/README.md

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **biblioteca-microservices: POM pai + 5 modulos (gateway + 4 servicos)** — biblioteca_microservices_pom_pai, servico_api_gateway, servico_livro_service, servico_autor_service, servico_editora_service, servico_aluguel_service [EXTRACTED 1.00]
- **Hierarquia PoliticaTarifacao (heranca/polimorfismo OOP)** — doc_01_dominio_modelo_politicatarifacao, doc_01_dominio_modelo_tarifacaopadrao, doc_01_dominio_modelo_tarifacaoporgenero, doc_01_dominio_modelo_tarifacaopromocional [EXTRACTED 1.00]
- **Hierarquia OOP de PoliticaTarifacao (heranca/polimorfismo/template method)** — doc_01_dominio_modelo_politicatarifacao, doc_01_dominio_modelo_tarifacaopadrao, doc_01_dominio_modelo_tarifacaoporgenero, doc_01_dominio_modelo_tarifacaopromocional, doc_01_dominio_modelo_contextotarifacao [EXTRACTED 0.90]
- **biblioteca-monolito: 4 modulos internos com fronteiras explicitas** — modulo_autor_monolito, modulo_editora_monolito, modulo_livro_monolito, modulo_aluguel_monolito [EXTRACTED 1.00]
- **Fluxo distribuido de aluguel (aluguel-service chama livro-service, com compensacao)** — doc_03_arquitetura_microservices_aluguel_service, doc_03_arquitetura_microservices_livrogateway, doc_03_arquitetura_microservices_livro_service, doc_03_arquitetura_microservices_compensacao_saga [EXTRACTED 0.85]
- **Microservices Topology: Actors to API Gateway to Services** — diagrama_sem_nome_drawio_actors, diagrama_sem_nome_drawio_api_gateway, diagrama_sem_nome_drawio_blue_service_one, diagrama_sem_nome_drawio_blue_service_two, diagrama_sem_nome_drawio_orange_service_one, diagrama_sem_nome_drawio_orange_service_two [EXTRACTED 1.00]
- **Orange Services Sharing a Single Database** — diagrama_sem_nome_drawio_orange_service_one, diagrama_sem_nome_drawio_orange_service_two, diagrama_sem_nome_drawio_shared_database [EXTRACTED 0.95]
- **Blue Services Each Owning a Dedicated Database** — diagrama_sem_nome_drawio_blue_service_one, diagrama_sem_nome_drawio_blue_service_one_database, diagrama_sem_nome_drawio_blue_service_two, diagrama_sem_nome_drawio_blue_service_two_database, diagrama_sem_nome_drawio_database_per_service_pattern [INFERRED 0.85]
- **Module 2 Principais Topicos** — screenshot_1_arquiteturas_modernas_module, screenshot_1_microservices_vs_modular_monolith, screenshot_1_domain_driven_design, screenshot_1_service_mesh, screenshot_1_api_gateway_topic, screenshot_1_sagas_distributed_transactions, screenshot_1_resilience_patterns, screenshot_1_outbox_pattern_idempotency [EXTRACTED 1.00]
- **E-commerce Modular Mini-Project and Stack** — screenshot_1_mini_projeto_ecommerce_modular, screenshot_1_catalog_service, screenshot_1_api_gateway_topic, screenshot_1_payment_service, screenshot_1_tech_java, screenshot_1_tech_spring_boot, screenshot_1_tech_kafka [EXTRACTED 1.00]
- **Spring Boot 4 migration gotchas (biblioteca-monolito)** — doc_10_implementacao_monolito_spring_boot_starter_flyway, doc_10_implementacao_monolito_spring_boot_4_test_slice_package_moves, doc_10_implementacao_monolito_webmvctest_new_objectmapper, doc_10_implementacao_monolito_maven_failsafe_plugin [INFERRED 0.85]
- **4 modulos hexagonais do biblioteca-monolito** — doc_06_tasks_monolito_modulo_autor, doc_06_tasks_monolito_modulo_editora, doc_06_tasks_monolito_modulo_livro, doc_06_tasks_monolito_modulo_aluguel [EXTRACTED 0.75]
- **PoliticaTarifacao OOP hierarchy (heranca/polimorfismo/template method)** — doc_06_tasks_monolito_politica_tarifacao, doc_06_tasks_monolito_tarifacao_padrao, doc_06_tasks_monolito_tarifacao_por_genero, doc_06_tasks_monolito_tarifacao_promocional [EXTRACTED 0.75]

## Communities (45 total, 15 thin omitted)

### Community 0 - "Use Cases: Autor + Aluguel"
Cohesion: 0.06
Nodes (33): AtualizarAutorUseCase, Comando, BuscarAutorUseCase, Comando, CriarAutorUseCase, ListarAutoresUseCase, RemoverAutorUseCase, Autor (+25 more)

### Community 1 - "Use Cases: Editora"
Cohesion: 0.06
Nodes (27): AtualizarEditoraUseCase, Comando, BuscarEditoraUseCase, Comando, CriarEditoraUseCase, ListarEditorasUseCase, RemoverEditoraUseCase, Cnpj (+19 more)

### Community 2 - "Config do Monolito (yaml, podman, Testcontainers)"
Cohesion: 0.05
Nodes (64): biblioteca-monolito, podman-compose app profile (Containerfile), podman-compose postgres service (biblioteca-monolito), PostgreSQL 17-alpine, Actuator endpoints health,info,metrics, application.yaml (biblioteca-monolito), Datasource PostgreSQL (biblioteca_db), hibernate ddl-auto: validate (+56 more)

### Community 3 - "Adapters JPA e Persistence Mappers"
Cohesion: 0.06
Nodes (18): AutorJpaAdapter, Override, AutorJpaRepository, AutorPersistenceMapper, EditoraJpaAdapter, Override, EditoraJpaRepository, EditoraPersistenceMapper (+10 more)

### Community 4 - "Aluguel (dominio) e AluguelTest"
Cohesion: 0.09
Nodes (6): AluguelTest, ArquiteturaModularTest, com.tngtech.archunit.core.domain.JavaClasses, org.junit.jupiter.api.BeforeAll, org.junit.jupiter.api.Test, SuppressWarnings

### Community 5 - "Tarifacao: PoliticaTarifacao + subclasses"
Cohesion: 0.08
Nodes (14): ContextoTarifacao, PoliticaTarifacao, Override, TarifacaoPadrao, Override, TarifacaoPorGenero, Override, TarifacaoPromocional (+6 more)

### Community 6 - "Listagem paginada e Repositories (ports)"
Cohesion: 0.14
Nodes (6): FiltroLivro, Pagina, PedidoPagina, PageResponse, org.springframework.stereotype.Component, org.springframework.web.bind.annotation.GetMapping

### Community 7 - "ConsultaPorts e Use Cases de Livro"
Cohesion: 0.21
Nodes (13): AutorConsultaPort, EditoraConsultaPort, AtualizarLivroUseCase, BuscarLivroUseCase, CriarLivroUseCase, ListarLivrosUseCase, RemoverLivroUseCase, LivroRepository (+5 more)

### Community 8 - "Diagrama-Fonte do Estudo"
Cohesion: 0.10
Nodes (29): Actors (System Clients), API Gateway, Blue Service 1 (Dedicated Database), Blue Service 1 Database, Blue Service 2 (Dedicated Database), Blue Service 2 Database, Book Reservation / Rental System, Database per Service Pattern (+21 more)

### Community 9 - "AluguelEntity e StatusAluguel"
Cohesion: 0.08
Nodes (6): StatusAluguel, ATIVO, ATRASADO, DEVOLVIDO, AluguelEntity, Override

### Community 10 - "Entities JPA (Autor/Editora base)"
Cohesion: 0.11
Nodes (6): AutorEntity, EntidadeBase, Override, jakarta.persistence.Entity, jakarta.persistence.MappedSuperclass, jakarta.persistence.Table

### Community 12 - "Use Cases de Aluguel + Web Mapper"
Cohesion: 0.18
Nodes (10): AluguelWebMapperImpl, AlugarUseCase, BuscarAluguelUseCase, DevolverUseCase, ListarAlugueisPorLocatarioUseCase, ListarAtrasadosUseCase, AluguelRepository, SeletorPolitica (+2 more)

### Community 13 - "AluguelController e Aluguel"
Cohesion: 0.19
Nodes (7): Comando, AluguelController, AluguelWebMapper, AlugarRequest, AluguelResponse, DevolucaoRequest, io.swagger.v3.oas.annotations.Operation

### Community 16 - "GeneroLiterario e LivroConsultaPort"
Cohesion: 0.12
Nodes (14): Comando, GeneroLiterario, ACAO, AVENTURA, BIOGRAFIA, FANTASIA, FICCAO_CIENTIFICA, HISTORIA (+6 more)

### Community 17 - "Arquitetura de Microsservicos (Fase 2)"
Cohesion: 0.16
Nodes (18): Modelo de Dominio (Reserva de Livros), aluguel-service (:8084, MongoDB), api-gateway (Spring Cloud Gateway :8080), Arquitetura de Microsservicos, autor-service (:8082, SQL Server A), AutorGateway (port de saida HTTP), Compensacao manual / Saga simplificada, Database-per-service (bancos isolados por servico) (+10 more)

### Community 18 - "Testes de Integracao (IT) e helpers"
Cohesion: 0.30
Nodes (11): AluguelPersistenceMapperImpl, AluguelJpaAdapterIT, AutorJpaAdapterIT, EditoraJpaAdapterIT, AbstractPostgresIT, AutorPersistenceMapperImpl, EditoraPersistenceMapperImpl, jakarta.persistence.EntityManager (+3 more)

### Community 19 - "EditoraEntity"
Cohesion: 0.13
Nodes (3): Override, EditoraEntity, Override

### Community 21 - "Community 21"
Cohesion: 0.20
Nodes (10): BibliotecaMonolitoApplicationTests, FluxoAluguelIT, org.junit.jupiter.api.BeforeEach, org.springframework.boot.test.context.SpringBootTest, org.springframework.test.context.ActiveProfiles, org.springframework.test.context.DynamicPropertyRegistry, org.springframework.test.context.DynamicPropertySource, org.springframework.web.client.RestClient (+2 more)

### Community 22 - "Community 22"
Cohesion: 0.23
Nodes (16): biblioteca-microservices POM pai (packaging=pom), Compensacao manual (saga simplificada) no fluxo de aluguel, AutorConsultaPort (API publica do modulo), EditoraConsultaPort (API publica do modulo), Fluxo de aluguel com compensacao (salvar aluguel Mongo + baixar exemplar livro-service), Modulo autor (monolito), Modulo editora (monolito), Modulo livro (monolito) (+8 more)

### Community 23 - "Community 23"
Cohesion: 0.17
Nodes (13): Arquitetura Hexagonal (Ports & Adapters) + DDD tatico, arquitetura-moderna-3semana (pasta-mae), biblioteca-microservices (projeto), biblioteca-monolito (projeto), BibliotecaMonolitoApplication (classe main), MapStruct + record DTOs (sem Lombok), Modulo config (monolito), Monolito Modular (+5 more)

### Community 24 - "Community 24"
Cohesion: 0.24
Nodes (4): AluguelJpaAdapter, Override, AluguelJpaRepository, AluguelPersistenceMapper

### Community 25 - "Community 25"
Cohesion: 0.37
Nodes (7): GlobalExceptionHandler, java.net.URI, org.springframework.http.HttpStatus, org.springframework.http.ProblemDetail, org.springframework.web.bind.annotation.ExceptionHandler, org.springframework.web.bind.annotation.RestControllerAdvice, org.springframework.web.bind.MethodArgumentNotValidException

### Community 26 - "Community 26"
Cohesion: 0.26
Nodes (8): AluguelDomainConfig, JpaConfig, OpenApiConfig, io.swagger.v3.oas.models.OpenAPI, OpenAPI, org.springframework.context.annotation.Bean, org.springframework.context.annotation.Configuration, org.springframework.data.jpa.repository.config.EnableJpaAuditing

### Community 27 - "Community 27"
Cohesion: 0.37
Nodes (3): Comando, CriarLivroUseCaseTest, Comando

### Community 28 - "Community 28"
Cohesion: 0.23
Nodes (3): AtualizarLivroRequest, LivroResponse, Comando

### Community 29 - "Community 29"
Cohesion: 0.18
Nodes (12): ContextoTarifacao (record imutavel), PoliticaTarifacao (abstract), TarifacaoPadrao, TarifacaoPromocional, Template method calcular(ContextoTarifacao), JaCoCo (LINE 80% / BRANCH 70%), Piramide de testes (70/25/5), PoliticaTarifacaoTest (@ParameterizedTest) (+4 more)

### Community 31 - "Community 31"
Cohesion: 0.18
Nodes (9): IDs via SEQUENCE allocationSize=50 (nunca IDENTITY), Database-per-service (puro), Decisao: database-per-service puro (nao seguir diagrama original), Diagrama sem nome.drawio.png (origem do estudo), Estrategia de Desenvolvimento em 3 Fases, Estudo Monolito x Microsservicos, Fatia Vertical (walking skeleton), Monolito: um PostgreSQL, 4 schemas (autor, editora, catalogo, aluguel) (+1 more)

### Community 32 - "Community 32"
Cohesion: 0.24
Nodes (11): Aluguel (entidade de dominio), Autor (entidade de dominio), Editora (entidade de dominio), GeneroLiterario (enum), Livro (entidade de dominio), StatusAluguel (enum), TarifacaoPorGenero, Enum GeneroLiterario duplicado como contrato (+3 more)

### Community 33 - "Community 33"
Cohesion: 0.38
Nodes (8): mvnw script, clean(), die(), exec_maven(), hash_string(), set_java_home(), trim(), verbose()

### Community 36 - "Community 36"
Cohesion: 0.29
Nodes (6): Containerfile multi-stage (layered JAR, JRE, nao-root), biblioteca-microservices podman-compose.yml, aluguel-service DB: MongoDB :27017 aluguel_db, autor-service DB: SQL Server instancia A :1433 autor_db, editora-service DB: SQL Server instancia B :1434 editora_db, Podman (podman-compose) - nunca Docker

## Ambiguous Edges - Review These
- `Monolith vs Microservices Architecture Diagram` → `Book Reservation / Rental System`  [AMBIGUOUS]
  Diagrama sem nome.drawio.png · relation: references

## Knowledge Gaps
- **53 isolated node(s):** `com.tesseracodelabs:biblioteca-microservices`, `com.tesseracodelabs:biblioteca-monolito`, `ATIVO`, `DEVOLVIDO`, `ATRASADO` (+48 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 169 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **15 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `Monolith vs Microservices Architecture Diagram` and `Book Reservation / Rental System`?**
  _Edge tagged AMBIGUOUS (relation: references) - confidence is low._
- **Why does `GeneroLiterario` connect `GeneroLiterario e LivroConsultaPort` to `Community 34`, `Adapters JPA e Persistence Mappers`, `Tarifacao: PoliticaTarifacao + subclasses`, `Listagem paginada e Repositories (ports)`, `ConsultaPorts e Use Cases de Livro`, `Entities JPA (Autor/Editora base)`, `LivroEntity`, `Use Cases de Aluguel + Web Mapper`, `Livro (dominio)`, `Testes de Integracao (IT) e helpers`, `Community 20`, `Community 27`, `Community 28`, `Community 30`?**
  _High betweenness centrality (0.081) - this node is a cross-community bridge._
- **Why does `LivroEntity` connect `LivroEntity` to `GeneroLiterario e LivroConsultaPort`, `Testes de Integracao (IT) e helpers`, `Entities JPA (Autor/Editora base)`, `Adapters JPA e Persistence Mappers`?**
  _High betweenness centrality (0.043) - this node is a cross-community bridge._
- **Why does `Aluguel` connect `AluguelRepository e adapters` to `Use Cases: Autor + Aluguel`, `Aluguel (dominio) e AluguelTest`, `AluguelEntity e StatusAluguel`, `Use Cases de Aluguel + Web Mapper`, `AluguelController e Aluguel`, `Testes de Integracao (IT) e helpers`, `Community 24`, `Community 30`?**
  _High betweenness centrality (0.043) - this node is a cross-community bridge._
- **What connects `com.tesseracodelabs:biblioteca-microservices`, `com.tesseracodelabs:biblioteca-monolito`, `ATIVO` to the rest of the system?**
  _53 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Use Cases: Autor + Aluguel` be split into smaller, more focused modules?**
  _Cohesion score 0.06056701030927835 - nodes in this community are weakly interconnected._
- **Should `Use Cases: Editora` be split into smaller, more focused modules?**
  _Cohesion score 0.05958485958485959 - nodes in this community are weakly interconnected._