# 10 — Implementação do `biblioteca-monolito` (registro do processo)

> Documenta **o que foi construído** na Fase 1 e as decisões tomadas durante a
> implementação. Complementa o checklist do `06-TASKS-MONOLITO.md` (todos os
> épicos 0–6 concluídos).
>
> Data: 2026-09-10 · Build: `./mvnw clean verify` → **BUILD SUCCESS**
> (84 testes unitários + 16 de integração, 0 falhas).

## 1. O que existe agora

```
arquitetura-moderna-3semana/biblioteca-monolito/
├── pom.xml                      Spring Boot 4.1.1 · Java 25 · MapStruct · Flyway · Testcontainers · ArchUnit
├── Containerfile                multi-stage (JDK 25 build → JRE 25 runtime, usuário não-root, layered JAR)
├── podman-compose.yml           serviço postgres (+ serviço app no profile "app")
└── src/
    ├── main/java/com/tesseracodelabs/biblioteca_monolito/
    │   ├── BibliotecaMonolitoApplication.java
    │   ├── shared/
    │   │   ├── domain/   EntidadeBase? não — só domínio: DomainException + 3 subclasses,
    │   │   │             GeneroLiterario (enum), Pagina<T>, PedidoPagina
    │   │   ├── infrastructure/ EntidadeBase (@MappedSuperclass, auditoria)
    │   │   └── web/      GlobalExceptionHandler (ProblemDetail RFC 9457), PageResponse<T>
    │   ├── config/       OpenApiConfig (springdoc), JpaConfig (@EnableJpaAuditing)
    │   ├── autor/        domain · application(+port) · infrastructure · web    (5 use cases)
    │   ├── editora/      idem + value object Cnpj (14 dígitos + DV)            (5 use cases)
    │   ├── livro/        idem; application usa AutorConsultaPort + EditoraConsultaPort (5 use cases)
    │   └── aluguel/      domain/tarifacao (PoliticaTarifacao + 3 subclasses + SeletorPolitica),
    │                     application usa LivroConsultaPort                     (5 use cases)
    ├── main/resources/
    │   ├── application.yaml               datasource, open-in-view:false, ddl-auto:validate,
    │   │                                  flyway locations = db/migration,db/seed
    │   ├── db/migration/  V1 baseline (schemas) · V2 autor · V3 editora · V4 catalogo · V5 aluguel
    │   └── db/seed/       V6 dados de exemplo (fora do profile de teste)
    └── test/…            AbstractPostgresIT (Singleton Container + @ActiveProfiles("test")),
                          testes por camada + ArquiteturaModularTest (ArchUnit) + FluxoAluguelIT
```

90 arquivos `.java` em `main`, 25 em `test`.

## 2. Decisões e desvios do plano original

| # | Decisão | Motivo |
| --- | --- | --- |
| D1 | Pacote base **`com.tesseracodelabs.biblioteca_monolito`** (não `...biblioteca`) | é o que o Spring Initializr gera a partir do artifact `biblioteca-monolito`; alinhar em vez de renomear à mão |
| D2 | **`EntidadeBase` em `shared/infrastructure`**, não em `shared/domain` | ela é `@MappedSuperclass` (JPA) — o domínio não pode conhecê-la. O `ArquiteturaModularTest` garante isso |
| D3 | Domínio não usa `Page`/`Pageable` do Spring Data | criados `Pagina<T>` e `PedidoPagina` em `shared/domain`; a conversão para/de Spring Data fica só nos adapters e no `web` |
| D4 | **`spring-boot-starter-flyway`** em vez de `flyway-core` avulso | no Spring Boot 4 a auto-config do Flyway mora no módulo `spring-boot-flyway`; sem o starter as migrations não rodavam |
| D5 | Imports de teste-slice do Spring Boot 4 mudaram de pacote | `org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`, `...webmvc.test.autoconfigure.WebMvcTest`, `...jdbc.test.autoconfigure.AutoConfigureTestDatabase` |
| D6 | `@WebMvcTest` do Spring Boot 4 não injeta `ObjectMapper` bean por padrão | nos testes de controller, instanciar `new ObjectMapper()` (com `JavaTimeModule` onde há datas) |
| D7 | `maven-failsafe-plugin` adicionado ao pom | o Initializr só configura o Surefire; sem o Failsafe os `*IT` não rodavam no `verify` |
| D8 | Seed `V6` movido para `db/seed/` e **não** carregado no profile `test` | os `*JpaAdapterIT` e o `FluxoAluguelIT` colidiam com os CNPJs/ISBNs fixos do seed. Runtime carrega `db/migration,db/seed`; teste carrega só `db/migration` |
| D9 | `CnpjFake` (gerador de CNPJ válido único) para os ITs que semeiam editora | o `FluxoAluguelIT` commita (não faz rollback como `@DataJpaTest`); ITs precisam de dados distintos |
| D10 | Numeração das migrations: `V1` cria os 4 schemas; tabelas em `V2`–`V5` | o doc 02 listava `V1__schema_autor`; ajustado porque já havia um `V1__baseline` da preparação |
| D11 | `SeletorPolitica` exposto como `@Bean` via `AluguelDomainConfig` | é objeto de domínio puro (sem anotação Spring); o use case precisa injetá-lo |
| D12 | Java 25: o Maven da máquina usa JDK 21 por padrão | build roda com `JAVA_HOME=C:\Program Files\Java\jdk-25`; na IDE, selecionar o JDK 25 |
| D13 | Podman no Windows: `podman run` manual publicou em `::1` (IPv6) e a JVM tenta IPv4 | subir o banco **via `podman-compose`** (faz bind `0.0.0.0`), não `podman run` avulso |

## 3. Regras de arquitetura verificadas por teste (`ArquiteturaModularTest`)

1. Nenhuma classe fora de `<modulo>`/`shared`/`config` depende de `<modulo>.domain` ou `<modulo>.infrastructure` — só da `application.port` pública.
2. `..domain..` não depende de `org.springframework`, `jakarta.persistence` nem `org.hibernate`.
3. Dependency Rule das camadas: `web` não é acessada por ninguém; `application` só por `web` e `infrastructure`; `infrastructure` não é acessada por ninguém.
4. Todo `@Service` em `..application..` termina com `UseCase`.

## 4. Fluxo distribuído × monolítico (o ponto do estudo)

No `AlugarUseCase` (monolito), tudo é **uma `@Transactional`**:

```
disponibilidade(livroId)  ── LivroConsultaPort (mesma JVM, mesma tx)
politica = seletor.escolher(ctx)          ← polimorfismo
taxa = politica.calcular(ctx)             ← template method
aluguelRepository.salvar(aluguel)
livroConsulta.baixarExemplar(livroId)     ← mesma tx; se falhar → rollback de tudo
```

Não há compensação manual. Na Fase 2 (microsserviços), `LivroConsultaPort` vira
um `LivroGateway` HTTP e esse mesmo fluxo precisa de saga/compensação — é
exatamente o contraste que o `04-COMPARATIVO.md` documenta.

## 5. OOP exercitado (`aluguel/domain/tarifacao/`)

| Pilar | Onde |
| --- | --- |
| **Herança** | `TarifacaoPadrao`, `TarifacaoPorGenero`, `TarifacaoPromocional` estendem `PoliticaTarifacao` |
| **Polimorfismo** | `AlugarUseCase` recebe `PoliticaTarifacao` do `SeletorPolitica` sem saber a implementação |
| **Encapsulamento** | `Aluguel` muda `status`/`dataDevolucaoReal` só por `devolver()`/`marcarAtraso()`; `PoliticaTarifacao.calcular()` é `final` |
| **Template method** | `calcular()` fixa o algoritmo (subtotal por dias → `ajustar()` → 2 casas, nunca negativo); subclasses só preenchem `valorDiario()` e `ajustar()` |

Valores verificados na aplicação real:
- 7 dias, gênero comum → **PADRAO** R$ 14,00
- 7 dias, gênero `TECNICO` → **POR_GENERO** R$ 28,00
- 20 dias → **PROMOCIONAL** R$ 23,40 (7 dias grátis, 10% no restante)

## 6. Endpoints (todos testados via Swagger + PowerShell)

| Verbo | Rota | Resultado observado |
| --- | --- | --- |
| POST | `/api/autores` `/api/editoras` `/api/livros` `/api/alugueis` | 201 + `Location` |
| GET | `/api/{recurso}/{id}` | 200 / 404 ProblemDetail |
| GET | `/api/livros?genero=&autorId=&page=&size=` | 200 paginado, `autorNome`/`editoraNome` resolvidos |
| PUT | `/api/{recurso}/{id}` | 200 |
| DELETE | `/api/{recurso}/{id}` | 204 |
| POST | `/api/alugueis/{id}/devolucao` | 200, status `DEVOLVIDO`, exemplar volta |
| GET | `/api/alugueis?locatario=` · `/api/alugueis/atrasados` | 200 |

Erros: 400 (com lista `erros[].campo`), 404, 409 (CNPJ/ISBN duplicado), 422
(regra de negócio) — todos `application/problem+json` (RFC 9457).

## 7. Como rodar

```powershell
# 1. Postgres (via compose — nunca `podman run` avulso no Windows)
podman machine start
cd arquitetura-moderna-3semana\biblioteca-monolito
podman-compose up -d postgres

# 2. App (JDK 25!)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25"
.\mvnw spring-boot:run
#   Swagger: http://localhost:8080/swagger-ui/index.html

# 3. Testes
$env:DOCKER_HOST = "npipe:////./pipe/podman-machine-default"
$env:TESTCONTAINERS_RYUK_DISABLED = "true"
.\mvnw verify

# 4. App em contêiner
podman-compose --profile app up -d --build
```

## 8. Pendências / próximos passos

- [ ] Cobertura JaCoCo com threshold (LINE 80% / BRANCH 70%) — plugin ainda não configurado
- [ ] `AtualizarLivroUseCaseTest` e `DevolverUseCaseTest` dedicados (a lógica está coberta por IT/fluxo, falta o unitário isolado)
- [ ] Índice único de "1 aluguel ativo por locatário+livro" se a regra for desejada
- [ ] Fase 2: extrair os 4 módulos para `biblioteca-microservices/` (doc 07)
