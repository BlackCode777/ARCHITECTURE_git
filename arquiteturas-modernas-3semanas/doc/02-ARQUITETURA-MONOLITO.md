# 02 — Arquitetura do Monolito (`biblioteca-monolito`)

> Uma aplicação Spring Boot. Um processo. Um banco PostgreSQL. Quatro módulos
> internos (`autor`, `editora`, `livro`, `aluguel`) com **fronteiras explícitas**.

## 1. Princípio

É um **monolito modular**: o código é uma unidade de deploy, mas cada módulo é
uma "caixa" com API pública própria. Isso deixa o caminho pronto para, no futuro,
extrair um módulo como microsserviço sem reescrever o domínio.

Regra de ouro: **um módulo nunca importa classes `domain`/`infrastructure` de
outro módulo**. A conversa entre módulos acontece só pela camada `application`
(porta de entrada) do outro módulo, ou por eventos de aplicação Spring.

## 2. Estrutura de pacotes

> Pacote base gerado pelo Initializr: `com.tesseracodelabs.biblioteca_monolito`
> (o `_monolito` vem do artifact `biblioteca-monolito`). Os subpacotes de feature
> ficam abaixo dele.

```text
com.tesseracodelabs.biblioteca_monolito
├── BibliotecaMonolitoApplication.java
│
├── autor
│   ├── domain
│   │   ├── Autor.java                     (entidade de domínio, sem anotação JPA)
│   │   └── AutorRepository.java           (PORT de saída — interface)
│   ├── application
│   │   ├── CriarAutorUseCase.java
│   │   ├── AtualizarAutorUseCase.java
│   │   ├── BuscarAutorUseCase.java
│   │   ├── ListarAutoresUseCase.java
│   │   ├── RemoverAutorUseCase.java
│   │   └── port
│   │       └── AutorConsultaPort.java     (API pública do módulo p/ outros módulos)
│   ├── infrastructure
│   │   ├── AutorEntity.java               (@Entity JPA)
│   │   ├── AutorJpaRepository.java        (Spring Data)
│   │   ├── AutorJpaAdapter.java           (implementa AutorRepository)
│   │   └── AutorPersistenceMapper.java    (MapStruct: Autor ↔ AutorEntity)
│   └── web
│       ├── AutorController.java
│       ├── dto
│       │   ├── CriarAutorRequest.java     (record)
│       │   ├── AtualizarAutorRequest.java (record)
│       │   └── AutorResponse.java         (record)
│       └── AutorWebMapper.java            (MapStruct: domínio ↔ DTO)
│
├── editora        (mesma estrutura)
├── livro          (mesma estrutura; livro/application usa AutorConsultaPort e EditoraConsultaPort)
├── aluguel        (mesma estrutura; aluguel/domain tem PoliticaTarifacao e subclasses)
│
├── shared
│   ├── domain
│   │   ├── EntidadeBase.java              (@MappedSuperclass: id, criadoEm, atualizadoEm)
│   │   └── DomainException.java
│   ├── web
│   │   ├── GlobalExceptionHandler.java    (@RestControllerAdvice → ProblemDetail)
│   │   └── PageResponse.java              (wrapper de paginação)
│   └── GeneroLiterario.java               (enum compartilhado — OK no monolito)
│
└── config
    ├── OpenApiConfig.java                 (springdoc)
    └── JpaConfig.java                     (SEQUENCE, auditing)
```

## 3. Fluxo de uma requisição (criar livro)

```
POST /api/livros
      │
      ▼
LivroController ── recebe CriarLivroRequest (record)
      │            valida com Bean Validation
      ▼
LivroWebMapper ── Request → objeto de domínio Livro (sem id)
      │
      ▼
CriarLivroUseCase
      │  1. autorConsultaPort.existePorId(autorId)     ← módulo autor
      │  2. editoraConsultaPort.existePorId(editoraId) ← módulo editora
      │  3. livro.validarInvariantes()
      │  4. livroRepository.salvar(livro)              ← PORT
      ▼
LivroJpaAdapter
      │  LivroPersistenceMapper: Livro → LivroEntity
      │  livroJpaRepository.save(entity)
      ▼
PostgreSQL (schema catalogo, tabela livros)
```

Tudo dentro de **uma transação** (`@Transactional` no use case).

## 4. Banco de dados

- **Um** PostgreSQL (`biblioteca_db`), porta `5432`.
- Schemas separam módulos: `autor`, `editora`, `catalogo`, `aluguel`.
- IDs: `SEQUENCE` com `allocationSize=50` (nunca `IDENTITY`).
- Migrations Flyway em `src/main/resources/db/migration/`:
  - `V1__schema_autor.sql`
  - `V2__schema_editora.sql`
  - `V3__schema_catalogo.sql`
  - `V4__schema_aluguel.sql`
  - `V5__dados_exemplo.sql` (seed para brincar com a API)
- `open-in-view: false` no `application.yaml`.
- `ddl-auto: validate`.

## 5. `application.yaml` (essencial)

```yaml
spring:
  application:
    name: biblioteca-monolito
  datasource:
    url: jdbc:postgresql://localhost:5432/biblioteca_db
    username: biblioteca
    password: biblioteca
  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: validate
    properties:
      hibernate.default_schema: public
  flyway:
    enabled: true

server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

## 6. Endpoints REST (todos sob `/api`)

| Método | Rota | Use case |
| --- | --- | --- |
| POST | `/api/autores` | CriarAutor |
| GET | `/api/autores/{id}` | BuscarAutor |
| GET | `/api/autores?page=&size=` | ListarAutores |
| PUT | `/api/autores/{id}` | AtualizarAutor |
| DELETE | `/api/autores/{id}` | RemoverAutor |
| *(idem para `/api/editoras`)* | | |
| POST | `/api/livros` | CriarLivro |
| GET | `/api/livros/{id}` | BuscarLivro |
| GET | `/api/livros?genero=&autorId=&page=&size=` | ListarLivros |
| PUT | `/api/livros/{id}` | AtualizarLivro |
| DELETE | `/api/livros/{id}` | RemoverLivro |
| POST | `/api/alugueis` | Alugar |
| POST | `/api/alugueis/{id}/devolucao` | Devolver |
| GET | `/api/alugueis/{id}` | BuscarAluguel |
| GET | `/api/alugueis?locatario=` | ListarPorLocatario |
| GET | `/api/alugueis/atrasados` | ListarAtrasados |

Swagger UI em `http://localhost:8080/swagger-ui.html`.

## 7. Vantagens desta versão (a explorar no doc 04)

- Uma transação cobre "criar aluguel + baixar exemplar" — consistência trivial.
- `JOIN` entre livro, autor e editora numa query só.
- Um deploy, um log, um profiler.
- Refactor entre módulos é uma mudança de compilação, não de contrato de rede.

## 8. Custos (a explorar no doc 04)

- Todo o time trabalha no mesmo código / mesmo release.
- Escala tudo junto (não dá pra escalar só o aluguel).
- Um bug de memória num módulo derruba a aplicação inteira.
- Tecnologia única (não dá pra usar Mongo só no aluguel sem trazer pro monolito).
