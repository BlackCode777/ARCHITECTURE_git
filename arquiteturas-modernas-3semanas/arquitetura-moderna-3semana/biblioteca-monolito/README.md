# biblioteca-monolito

> **Fase 1** do estudo Monolito × Microsserviços. Sistema de reserva/aluguel de
> livros como **monolito modular** — uma aplicação Spring Boot, um banco PostgreSQL,
> quatro módulos internos (`autor`, `editora`, `livro`, `aluguel`) com fronteiras
> explícitas.
>
> Projeto **gerado no start.spring.io** e descompactado nesta pasta. Ver
> `README` da pasta-mãe (`../README.md`) para os parâmetros do Initializr.

## Documentação

Em [`../../doc/`](../../doc/README.md):

- **Arquitetura deste projeto**: [`../../doc/02-ARQUITETURA-MONOLITO.md`](../../doc/02-ARQUITETURA-MONOLITO.md)
- **Checklist de implementação**: [`../../doc/06-TASKS-MONOLITO.md`](../../doc/06-TASKS-MONOLITO.md)
- Modelo de domínio: [`../../doc/01-DOMINIO-MODELO.md`](../../doc/01-DOMINIO-MODELO.md)
- Contêineres: [`../../doc/08-PODMAN-COMPOSE.md`](../../doc/08-PODMAN-COMPOSE.md)
- Testes: [`../../doc/09-TESTES-ESTRATEGIA.md`](../../doc/09-TESTES-ESTRATEGIA.md)

## Stack

| Item | Versão / escolha |
| --- | --- |
| Java | 25 |
| Spring Boot | 4.1.1 |
| Banco | PostgreSQL 17 (4 schemas: `autor`, `editora`, `catalogo`, `aluguel`) |
| Arquitetura | Hexagonal (Ports & Adapters) + DDD tático, por feature |
| DTOs | `record` Java — **sem Lombok** |
| Mapeamento | MapStruct (compile-time) |
| API docs | springdoc-openapi (Swagger UI) |
| Migrations | Flyway |
| Testes | JUnit 5, Mockito, AssertJ, Testcontainers |
| Contêineres | Podman (`podman-compose`) |

## Módulos

```
com.tesseracodelabs.biblioteca
├── autor      → CRUD de autores        (folha)
├── editora    → CRUD de editoras       (folha)
├── livro      → CRUD de livros         (usa autor + editora via port)
├── aluguel    → alugar/devolver livro  (usa livro via port; tem PoliticaTarifacao)
├── shared     → EntidadeBase, GeneroLiterario, GlobalExceptionHandler, PageResponse
└── config     → OpenApiConfig, JpaConfig
```

Regra: **um módulo nunca importa `domain`/`infrastructure` de outro** — só a
`application/port` pública. Isso deixa a extração para microsserviço (Fase 2) mecânica.

## Como rodar

```powershell
# 1. subir o Postgres
podman machine start
podman-compose up -d postgres

# 2. rodar a aplicação (pela IDE ou):
./mvnw spring-boot:run

# 3. abrir o Swagger
#    http://localhost:8080/swagger-ui.html
```

## Testes

```powershell
./mvnw test        # unitários
./mvnw verify      # unitários + integração (*IT) + JaCoCo
```

Testcontainers no Windows com Podman:
```powershell
$env:DOCKER_HOST = "npipe:////./pipe/podman-machine-default"
$env:TESTCONTAINERS_RYUK_DISABLED = "true"
```

## Endpoints

Base: `/api` — `/api/autores`, `/api/editoras`, `/api/livros`, `/api/alugueis`.
Detalhe completo em [`../../doc/02-ARQUITETURA-MONOLITO.md`](../../doc/02-ARQUITETURA-MONOLITO.md) §6.

## Fluxo de exemplo

```
POST /api/autores      { "nome": "Isaac Asimov", ... }              → 201  {id: 1}
POST /api/editoras     { "nome": "Aleph", "cnpj": "..." }           → 201  {id: 1}
POST /api/livros       { "titulo": "Fundação", "autorId": 1,
                         "editoraId": 1, "genero": "FICCAO_CIENTIFICA",
                         "numeroPaginas": 244, "exemplaresTotais": 3 } → 201  {id: 1}
POST /api/alugueis     { "livroId": 1, "nomeLocatario": "Ana",
                         "dias": 7 }                                 → 201  {taxa: 14.00}
POST /api/alugueis/1/devolucao                                       → 200  {status: "DEVOLVIDO"}
```
