# 00 — Visão Geral do Estudo: Monolito × Microsserviços

> Estudo prático comparando duas arquiteturas com o **mesmo domínio de negócio**:
> um sistema de **reserva/aluguel de livros**. Sem segurança (nada de JWT, OAuth, etc.) —
> o foco é 100% arquitetural.

## 1. Objetivo

Construir dois projetos de referência, funcionalmente equivalentes, para observar
na prática as diferenças de:

- Organização de código e fronteiras de módulo
- Modelagem e propriedade de dados (um banco × banco por serviço)
- Comunicação (chamada de método × chamada de rede)
- Transações e consistência (ACID local × consistência eventual)
- Build, deploy, observabilidade e testes
- Custo operacional e cognitivo

## 2. O diagrama que originou o estudo

Arquivo: `../Diagrama sem nome.drawio.png`

| Lado | Leitura |
| --- | --- |
| **monolith** (esquerda) | Um único contêiner lógico com 4 formas (círculo, cubo, cilindro, paralelogramo). Uma aplicação, um processo, um banco. As "4 partes" são apenas módulos internos. |
| **microservices** (direita) | 4 contêineres separados. Cada forma virou um serviço independente, deployável sozinho. |
| **parte inferior** | 4 atores → um ponto de entrada único (documento roxo = **API Gateway**) → os 4 serviços. Cada serviço tem **seu próprio banco** (setas verdes tracejadas). |

Neste estudo adotamos **database-per-service puro**: os 4 serviços têm bancos
totalmente isolados (o diagrama original mostrava 2 serviços compartilhando um
banco — decidimos não seguir esse ponto para manter o isolamento como regra).

## 3. Domínio: Sistema de Reserva de Livros

CRUD completo, sem autenticação. Entidades principais:

| Entidade | Campos principais |
| --- | --- |
| **Livro** | título, ISBN, número de páginas, ano de publicação, **gênero** (enum), sinopse, referência a Autor e Editora, quantidade de exemplares |
| **Autor** | nome, nacionalidade, data de nascimento, biografia |
| **Editora** | nome, CNPJ, cidade, site |
| **Aluguel** | livro alugado, nome do locatário, data de retirada, data prevista de devolução, data de devolução real, **taxa** cobrada, status (ATIVO, DEVOLVIDO, ATRASADO) |

### Enum de gênero literário

```
AVENTURA, ACAO, FICCAO_CIENTIFICA, FANTASIA, ROMANCE,
TERROR, SUSPENSE, BIOGRAFIA, HISTORIA, TECNICO, INFANTIL, POESIA
```

### Regra de negócio de tarifação (usada para exercitar OOP)

O cálculo da taxa de aluguel é modelado com **herança + polimorfismo**:

```
PoliticaTarifacao (abstract)
 ├── TarifacaoPadrao        → valor fixo por dia
 ├── TarifacaoPorGenero     → multiplicador conforme o gênero (técnico custa mais)
 └── TarifacaoPromocional   → desconto por período / primeira semana grátis
```

Cada política implementa `BigDecimal calcular(Aluguel aluguel)`. O serviço de
aluguel escolhe a política em runtime — o resto do código não sabe qual é.

## 4. Os dois projetos

Ambos ficam **dentro** da pasta-mãe `arquitetura-moderna-3semana/`:

```text
arquitetura-moderna-3semana/
├── biblioteca-monolito/         ← Fase 1
└── biblioteca-microservices/    ← Fase 2 (POM pai multi-módulo)
```

| Projeto | Pasta | Fase | Descrição |
| --- | --- | --- | --- |
| **biblioteca-monolito** | `arquitetura-moderna-3semana/biblioteca-monolito/` | 1 | Uma aplicação Spring Boot, um banco PostgreSQL, 4 módulos internos com fronteiras explícitas. |
| **biblioteca-microservices** | `arquitetura-moderna-3semana/biblioteca-microservices/` | 2 | 4 serviços Spring Boot + 1 API Gateway. Cada serviço com seu banco. |

> Os dois projetos são **gerados pelo Spring Initializr** (start.spring.io) e
> descompactados dentro de `arquitetura-moderna-3semana/`. As dependências vêm do
> Maven Central. O conteúdo antigo dessa pasta (esqueleto solto do Initializr) foi
> removido — ela agora é só o contêiner dos dois projetos.

## 5. Stack comum aos dois projetos

| Item | Escolha |
| --- | --- |
| Linguagem | Java 25 |
| Framework | Spring Boot 4.1.1 |
| Arquitetura interna | Hexagonal (Ports & Adapters) + DDD tático |
| Mapeamento DTO ↔ entidade | MapStruct (compile-time) — **sem Lombok** |
| Documentação de API | springdoc-openapi (Swagger UI) |
| Build | Maven (wrapper `mvnw`) |
| Testes unitários | JUnit 5 + Mockito + AssertJ |
| Testes de integração | Testcontainers (banco real) |
| Migrations relacionais | Flyway |
| Contêineres | **Podman** (`podman-compose`) — nunca Docker |

## 6. Alocação de bancos

| Serviço / módulo | Monolito | Microsserviços |
| --- | --- | --- |
| Livro / Catálogo | PostgreSQL (schema `catalogo`) | **PostgreSQL** `:5432` |
| Autor | PostgreSQL (schema `autor`) | **SQL Server** instância A `:1433` |
| Editora | PostgreSQL (schema `editora`) | **SQL Server** instância B `:1434` |
| Aluguel | PostgreSQL (schema `aluguel`) | **MongoDB** `:27017` |

No monolito é **um banco só** (schemas apenas organizam). Nos microsserviços são
**4 bancos fisicamente separados**, cada um acessível somente pelo seu dono.

## 7. Comunicação entre serviços (só microsserviços)

- **Entrada externa**: tudo passa pelo **Spring Cloud Gateway** (`:8080`).
- **Serviço → serviço**: REST síncrono com **OpenFeign** + Resilience (timeout, retry, fallback).
- Sem broker de mensageria nesta fase (Kafka/RabbitMQ ficam para uma evolução futura).

Exemplo: ao criar um aluguel, o `aluguel-service` chama `GET /livros/{id}` no
`livro-service` (via Feign) para validar existência e disponibilidade.

## 8. Índice da documentação

| Doc | Conteúdo |
| --- | --- |
| `00-VISAO-GERAL.md` | Este arquivo |
| `01-DOMINIO-MODELO.md` | Modelo de domínio detalhado, invariantes, enum, diagrama de classes |
| `02-ARQUITETURA-MONOLITO.md` | Estrutura de pacotes, módulos, banco único, fronteiras |
| `03-ARQUITETURA-MICROSERVICES.md` | Serviços, gateway, database-per-service, Feign, contratos |
| `04-COMPARATIVO.md` | Tabela lado a lado: prós, contras, quando usar cada um |
| `05-ESTRATEGIA-DESENVOLVIMENTO.md` | Ordem de construção, do monolito à extração dos serviços |
| `06-TASKS-MONOLITO.md` | Checklist passo a passo do monolito |
| `07-TASKS-MICROSERVICES.md` | Checklist passo a passo dos microsserviços |
| `08-PODMAN-COMPOSE.md` | Composição de contêineres para cada projeto |
| `09-TESTES-ESTRATEGIA.md` | Pirâmide de testes, Testcontainers, contratos |
