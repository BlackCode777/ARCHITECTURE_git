# Infraestrutura Base — PostgreSQL, Podman, Flyway, Swagger, Actuator

> Parte de [ARQUITETURA-SERVICOS.md](ARQUITETURA-SERVICOS.md). Entregáveis
> transversais da Fase 1 ([FASE-1-FUNDAMENTOS.md](FASE-1-FUNDAMENTOS.md)) — pré-requisito
> para todos os 7 serviços de arquitetura.

## Conceito

Esta configuração não é um conceito isolado de arquitetura distribuída, mas a base de
infraestrutura que sustenta o CRUD de Tarefas e os 7 serviços (`cap`, `rest`, `eda`,
`hexagonal`, `ntp`, `lb`, `metrics`):

* **PostgreSQL** — banco relacional principal, rodando em container via Podman.
* **Podman** — container runtime rootless (nunca Docker, regra inegociável do
  catálogo global).
* **Flyway** — versionamento de schema (`V{n}__{descricao}.sql`), garante que o
  schema do banco seja sempre reproduzível a partir do código.
* **Swagger/OpenAPI** — documentação viva da API (`springdoc-openapi`), gerada a
  partir dos controllers.
* **Actuator** — endpoints operacionais (`/actuator/health`, `/actuator/metrics`,
  `/actuator/prometheus`), base para o Serviço 7 (Observabilidade/Métricas).

## O que foi configurado

### 1. PostgreSQL via Podman

[podman-compose.yml](../fundamentos-2semanas/podman-compose.yml) — sobe um container
`postgres:16-alpine` com:

| Parâmetro | Valor |
| --- | --- |
| Banco | `fdmts-2smn-db` |
| Usuário | `fdmts` |
| Senha | `fdmts123` |
| Porta | `5432` |
| Timezone | `UTC` (alinhado ao [Serviço 5 — NTP](05-ntp-service.md)) |
| Volume | `fdmts-2smn-pgdata` (persistência entre restarts) |
| Healthcheck | `pg_isready`, 5s de intervalo |

```powershell
podman machine start
podman-compose -f podman-compose.yml up -d
podman-compose -f podman-compose.yml down
```

> **Nota de segurança:** a senha `fdmts123` está hardcoded no `podman-compose.yml`
> propositalmente — é ambiente de desenvolvimento local. Regra inegociável do
> catálogo global: credenciais sempre por variável de ambiente em **produção**
> (ver `application-prod.yaml` abaixo).

### 2. Flyway

Migration inicial: [V1__create_table_tarefas.sql](../fundamentos-2semanas/src/main/resources/db/migration/V1__create_table_tarefas.sql)
— cria a tabela `tarefas` com `SEQUENCE` (`allocationSize=50`, regra inegociável) e
colunas de auditoria `created_at`/`updated_at` em `TIMESTAMP WITH TIME ZONE` (UTC).

Duas dependências foram necessárias no `pom.xml` (Spring Boot 4.x separou o módulo de
integração do driver):

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-flyway</artifactId>
</dependency>
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

> **Armadilha encontrada:** ter apenas `flyway-database-postgresql` (driver) não é
> suficiente — sem `spring-boot-flyway` (o módulo de autoconfiguration do Spring Boot
> 4.x) o Flyway nunca roda e nenhum erro é logado, o schema simplesmente não é criado.

### 3. Swagger / OpenAPI

Dependência adicionada: `springdoc-openapi-starter-webmvc-ui:2.8.6`.

* Docs JSON: `GET /v3/api-docs`
* UI interativa: `GET /swagger-ui.html`

### 4. Actuator

Já estava no `pom.xml` desde o início do projeto. Configuração em `application.yaml`
expõe `health,info,metrics,prometheus` — o exporter Prometheus real (dependência
`micrometer-registry-prometheus`) ainda **não** foi adicionado; ver pendência no
[Serviço 7 — Metrics](07-metrics-service.md).

### 5. `application.yaml` / `application-prod.yaml`

* [application.yaml](../fundamentos-2semanas/src/main/resources/application.yaml) —
  perfil padrão (dev): credenciais locais, `open-in-view: false`, `ddl-auto: validate`,
  Flyway habilitado, Actuator, springdoc.
* [application-prod.yaml](../fundamentos-2semanas/src/main/resources/application-prod.yaml) —
  perfil `prod`: `DB_URL`/`DB_USERNAME`/`DB_PASSWORD` via variável de ambiente,
  detalhes de health ocultos (`show-details: never`).

## Validação realizada

```text
✔ podman-compose up -d          → container fdmts-2smn-postgres healthy
✔ mvnw spring-boot:run          → Started Fundamentos2semanasApplication
✔ Flyway                        → tabela "tarefas" criada, flyway_schema_history OK
✔ GET /actuator/health          → {"status":"UP"}
✔ GET /v3/api-docs              → HTTP 200
```

## Próximos passos

* [ ] Adicionar `micrometer-registry-prometheus` (ver [Serviço 7](07-metrics-service.md))
* [ ] Proteger `/actuator/**` (exceto `health`) com `ADMIN` — skill `spring-security-essencial`
* [ ] Mapear a entidade JPA `TarefaEntity` correspondente à tabela `tarefas`
* [ ] Adicionar Nginx ao `podman-compose.yml` para o [Serviço 6 — Load Balance](06-lb-service.md)

## Referências

* Skill `podman-compose-bancos-dev`, `flyway-liquibase-versionamento`,
  `spring-boot-estrutura-projeto`, `build-maven-gradle` (catálogo global)
