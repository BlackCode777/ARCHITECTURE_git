# Documentação — Estudo Monolito × Microsserviços

Sistema de **reserva/aluguel de livros** implementado nas duas arquiteturas, para
estudo comparativo. Sem segurança (foco 100% arquitetural).

## Ordem de leitura

1. [`00-VISAO-GERAL.md`](00-VISAO-GERAL.md) — o que é, o diagrama, a stack, os dois projetos
2. [`01-DOMINIO-MODELO.md`](01-DOMINIO-MODELO.md) — entidades, enum, invariantes, política de tarifação (OOP)
3. [`02-ARQUITETURA-MONOLITO.md`](02-ARQUITETURA-MONOLITO.md) — estrutura, módulos, banco único
4. [`03-ARQUITETURA-MICROSERVICES.md`](03-ARQUITETURA-MICROSERVICES.md) — 4 serviços + gateway, banco por serviço, Feign
5. [`04-COMPARATIVO.md`](04-COMPARATIVO.md) — tabela lado a lado, cenários, quando usar cada um
6. [`05-ESTRATEGIA-DESENVOLVIMENTO.md`](05-ESTRATEGIA-DESENVOLVIMENTO.md) — fases, ordem de construção
7. [`06-TASKS-MONOLITO.md`](06-TASKS-MONOLITO.md) — checklist executável do monolito
8. [`07-TASKS-MICROSERVICES.md`](07-TASKS-MICROSERVICES.md) — checklist executável dos microsserviços
9. [`08-PODMAN-COMPOSE.md`](08-PODMAN-COMPOSE.md) — contêineres, compose de cada projeto
10. [`09-TESTES-ESTRATEGIA.md`](09-TESTES-ESTRATEGIA.md) — pirâmide, Testcontainers, contrato
11. [`10-IMPLEMENTACAO-MONOLITO.md`](10-IMPLEMENTACAO-MONOLITO.md) — registro do que foi construído na Fase 1, decisões e desvios

## Onde ficam os projetos

```text
arquitetura-moderna-3semana/
├── biblioteca-monolito/         ← Fase 1 — gerado no start.spring.io
└── biblioteca-microservices/    ← Fase 2 — gerado no start.spring.io (POM pai + 5 módulos)
```

## Estado atual

| Item | Status |
| --- | --- |
| Documentação de estudo | ✅ criada |
| `arquitetura-moderna-3semana/biblioteca-monolito/` | ✅ **implementado** — 4 módulos, 20 use cases, 100 testes verdes (ver doc 10) |
| `arquitetura-moderna-3semana/biblioteca-microservices/` | ⬜ POM pai criado; 5 módulos a gerar no Initializr + implementar (Fase 2) |

## Decisões fechadas (2026-09-09)

| Tema | Decisão |
| --- | --- |
| Domínio | Reserva/aluguel de livros: Livro, Autor, Editora, Aluguel |
| Divisão MS | 4 serviços, database-per-service puro |
| Origem dos projetos | Gerados no start.spring.io, descompactados em `arquitetura-moderna-3semana/` |
| Bancos MS | livro→PostgreSQL · autor→SQL Server (inst. A) · editora→SQL Server (inst. B) · aluguel→MongoDB |
| Comunicação MS | REST síncrono via Spring Cloud Gateway + OpenFeign (sem broker) |
| Stack | Spring Boot 4.1.1 + Java 25 |
| Arquitetura interna | Hexagonal (Ports & Adapters) + DDD tático |
| Mapeamento | MapStruct — sem Lombok |
| Contêineres | Podman (`podman-compose`) |
| Escopo | Só backend; sem segurança |
| OOP no estudo | Herança/polimorfismo/encapsulamento via `PoliticaTarifacao` |

## Lembrete do projeto

> Todo arquivo novo/alterado deve ser indexado no **graphify**, e o graphify deve
> ser consultado antes de planejar sprint/task ou responder pergunta sobre o projeto.
