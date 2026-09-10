# arquitetura-moderna-3semana

Pasta-mãe do estudo **Monolito × Microsserviços** — sistema de reserva/aluguel de
livros implementado nas duas arquiteturas, para comparação. Sem segurança (foco
100% arquitetural).

## Estrutura

```
arquitetura-moderna-3semana/
├── biblioteca-monolito/         ← Fase 1 — projeto gerado no start.spring.io
└── biblioteca-microservices/    ← Fase 2 — projeto gerado no start.spring.io
                                    (POM pai multi-módulo: gateway + 4 serviços)
```

Os dois projetos são **gerados pelo Spring Initializr** (start.spring.io) e
descompactados aqui dentro. Dependências vêm do Maven Central.

## Documentação

Toda a documentação de arquitetura está em [`../doc/`](../doc/README.md):

| Doc | Conteúdo |
| --- | --- |
| [00-VISAO-GERAL](../doc/00-VISAO-GERAL.md) | O que é, o diagrama, a stack, os dois projetos |
| [01-DOMINIO-MODELO](../doc/01-DOMINIO-MODELO.md) | Entidades, enum, invariantes, `PoliticaTarifacao` (OOP) |
| [02-ARQUITETURA-MONOLITO](../doc/02-ARQUITETURA-MONOLITO.md) | Estrutura de `biblioteca-monolito/` |
| [03-ARQUITETURA-MICROSERVICES](../doc/03-ARQUITETURA-MICROSERVICES.md) | Estrutura de `biblioteca-microservices/` |
| [04-COMPARATIVO](../doc/04-COMPARATIVO.md) | Tabela lado a lado, cenários, quando usar cada um |
| [05-ESTRATEGIA-DESENVOLVIMENTO](../doc/05-ESTRATEGIA-DESENVOLVIMENTO.md) | Fases, ordem de construção |
| [06-TASKS-MONOLITO](../doc/06-TASKS-MONOLITO.md) | Checklist do monolito |
| [07-TASKS-MICROSERVICES](../doc/07-TASKS-MICROSERVICES.md) | Checklist dos microsserviços |
| [08-PODMAN-COMPOSE](../doc/08-PODMAN-COMPOSE.md) | Contêineres de cada projeto |
| [09-TESTES-ESTRATEGIA](../doc/09-TESTES-ESTRATEGIA.md) | Pirâmide de testes, Testcontainers |

## Stack comum aos dois projetos

| Item | Escolha |
| --- | --- |
| Java | 25 |
| Spring Boot | 4.1.1 |
| Arquitetura interna | Hexagonal (Ports & Adapters) + DDD tático |
| DTOs / Mapeamento | `record` + MapStruct — **sem Lombok** |
| API docs | springdoc-openapi (Swagger UI) |
| Migrations relacionais | Flyway |
| Testes | JUnit 5, Mockito, AssertJ, Testcontainers |
| Contêineres | **Podman** (`podman-compose`) — nunca Docker |

## Bancos

| Serviço / módulo | Monolito (`biblioteca-monolito`) | Microsserviços (`biblioteca-microservices`) |
| --- | --- | --- |
| Livro / Catálogo | PostgreSQL (schema `catalogo`) | PostgreSQL `:5432` |
| Autor | PostgreSQL (schema `autor`) | SQL Server instância A `:1433` |
| Editora | PostgreSQL (schema `editora`) | SQL Server instância B `:1434` |
| Aluguel | PostgreSQL (schema `aluguel`) | MongoDB `:27017` |

## Como gerar os projetos (start.spring.io)

### biblioteca-monolito
- **Project**: Maven · **Language**: Java · **Spring Boot**: 4.1.1
- **Group**: `com.tesseracodelabs` · **Artifact**: `biblioteca-monolito` · **Package**: `com.tesseracodelabs.biblioteca`
- **Packaging**: Jar · **Java**: 25
- **Dependencies**: Spring Web, Spring Data JPA, PostgreSQL Driver, Flyway Migration, Validation, Spring Boot Actuator, Testcontainers
- Adicionar manualmente no `pom.xml`: `mapstruct` + `mapstruct-processor`, `springdoc-openapi-starter-webmvc-ui`
- Descompactar em `arquitetura-moderna-3semana/biblioteca-monolito/`

### biblioteca-microservices
Gerar **5 projetos** (`api-gateway`, `livro-service`, `autor-service`, `editora-service`, `aluguel-service`)
e criar um `pom.xml` pai (`packaging=pom`, `<modules>`) que os agrupe. Detalhe das
dependências de cada serviço em [`../doc/07-TASKS-MICROSERVICES.md`](../doc/07-TASKS-MICROSERVICES.md), épico 0.
Descompactar em `arquitetura-moderna-3semana/biblioteca-microservices/`.

## Regra do projeto

> Todo arquivo novo/alterado deve ser indexado no **graphify**, e o graphify deve
> ser consultado antes de planejar sprint/task ou responder pergunta sobre o projeto.
