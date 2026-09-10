# 08 — Contêineres com Podman

> **Podman**, nunca Docker. `podman-compose` para orquestrar. Windows: máquina
> Podman iniciada (`podman machine start`).

## 1. `podman-compose.yml` — monolito

Arquivo em `arquitetura-moderna-3semana/biblioteca-monolito/podman-compose.yml`:

```yaml
version: "3.9"

services:
  postgres:
    image: docker.io/library/postgres:17-alpine
    container_name: biblioteca-postgres
    environment:
      POSTGRES_DB: biblioteca_db
      POSTGRES_USER: biblioteca
      POSTGRES_PASSWORD: biblioteca
    ports:
      - "5432:5432"
    volumes:
      - biblioteca-pg-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U biblioteca -d biblioteca_db"]
      interval: 10s
      timeout: 5s
      retries: 5

  # opcional: subir a própria app em contêiner
  app:
    build:
      context: .
      dockerfile: Containerfile
    container_name: biblioteca-monolito
    depends_on:
      postgres:
        condition: service_healthy
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/biblioteca_db
      SPRING_DATASOURCE_USERNAME: biblioteca
      SPRING_DATASOURCE_PASSWORD: biblioteca
    ports:
      - "8080:8080"

volumes:
  biblioteca-pg-data:
```

Uso:
```powershell
podman machine start
podman-compose -f podman-compose.yml up -d postgres      # só o banco (dev pela IDE)
podman-compose -f podman-compose.yml up -d               # tudo
podman-compose logs -f
podman-compose down          # para
podman-compose down -v       # para e apaga volumes (reset total)
```

## 2. `podman-compose.yml` — microsserviços

Arquivo em `arquitetura-moderna-3semana/biblioteca-microservices/podman-compose.yml`:

```yaml
version: "3.9"

networks:
  biblioteca-net:

volumes:
  pg-livro-data:
  mssql-autor-data:
  mssql-editora-data:
  mongo-aluguel-data:

services:
  # ---------- BANCOS ----------
  postgres:
    image: docker.io/library/postgres:17-alpine
    environment:
      POSTGRES_DB: livro_db
      POSTGRES_USER: livro
      POSTGRES_PASSWORD: livro
    ports: ["5432:5432"]
    volumes: ["pg-livro-data:/var/lib/postgresql/data"]
    networks: ["biblioteca-net"]
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U livro -d livro_db"]
      interval: 10s
      timeout: 5s
      retries: 5

  sqlserver-autor:
    image: mcr.microsoft.com/mssql/server:2022-latest
    environment:
      ACCEPT_EULA: "Y"
      MSSQL_SA_PASSWORD: "Autor@Str0ng!"
      MSSQL_PID: "Developer"
    ports: ["1433:1433"]
    volumes: ["mssql-autor-data:/var/opt/mssql"]
    networks: ["biblioteca-net"]
    healthcheck:
      test: ["CMD-SHELL", "/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P \"$$MSSQL_SA_PASSWORD\" -C -Q 'SELECT 1' || exit 1"]
      interval: 15s
      timeout: 10s
      retries: 10

  sqlserver-editora:
    image: mcr.microsoft.com/mssql/server:2022-latest
    environment:
      ACCEPT_EULA: "Y"
      MSSQL_SA_PASSWORD: "Editora@Str0ng!"
      MSSQL_PID: "Developer"
    ports: ["1434:1433"]          # host 1434 → contêiner 1433
    volumes: ["mssql-editora-data:/var/opt/mssql"]
    networks: ["biblioteca-net"]
    healthcheck:
      test: ["CMD-SHELL", "/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P \"$$MSSQL_SA_PASSWORD\" -C -Q 'SELECT 1' || exit 1"]
      interval: 15s
      timeout: 10s
      retries: 10

  mongo:
    image: docker.io/library/mongo:7
    environment:
      MONGO_INITDB_DATABASE: aluguel_db
    ports: ["27017:27017"]
    volumes: ["mongo-aluguel-data:/data/db"]
    networks: ["biblioteca-net"]
    healthcheck:
      test: ["CMD", "mongosh", "--eval", "db.adminCommand('ping')"]
      interval: 10s
      timeout: 5s
      retries: 5

  # ---------- SERVIÇOS ----------
  autor-service:
    build: { context: ./autor-service, dockerfile: Containerfile }
    environment:
      SPRING_DATASOURCE_URL: "jdbc:sqlserver://sqlserver-autor:1433;databaseName=autor_db;encrypt=false"
      SPRING_DATASOURCE_USERNAME: sa
      SPRING_DATASOURCE_PASSWORD: "Autor@Str0ng!"
    ports: ["8082:8082"]
    networks: ["biblioteca-net"]
    depends_on:
      sqlserver-autor: { condition: service_healthy }

  editora-service:
    build: { context: ./editora-service, dockerfile: Containerfile }
    environment:
      SPRING_DATASOURCE_URL: "jdbc:sqlserver://sqlserver-editora:1433;databaseName=editora_db;encrypt=false"
      SPRING_DATASOURCE_USERNAME: sa
      SPRING_DATASOURCE_PASSWORD: "Editora@Str0ng!"
    ports: ["8083:8083"]
    networks: ["biblioteca-net"]
    depends_on:
      sqlserver-editora: { condition: service_healthy }

  livro-service:
    build: { context: ./livro-service, dockerfile: Containerfile }
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/livro_db
      SPRING_DATASOURCE_USERNAME: livro
      SPRING_DATASOURCE_PASSWORD: livro
      CLIENTS_AUTOR_URL: http://autor-service:8082
      CLIENTS_EDITORA_URL: http://editora-service:8083
    ports: ["8081:8081"]
    networks: ["biblioteca-net"]
    depends_on:
      postgres: { condition: service_healthy }
      autor-service: { condition: service_started }
      editora-service: { condition: service_started }

  aluguel-service:
    build: { context: ./aluguel-service, dockerfile: Containerfile }
    environment:
      SPRING_DATA_MONGODB_URI: mongodb://mongo:27017/aluguel_db
      CLIENTS_LIVRO_URL: http://livro-service:8081
    ports: ["8084:8084"]
    networks: ["biblioteca-net"]
    depends_on:
      mongo: { condition: service_healthy }
      livro-service: { condition: service_started }

  api-gateway:
    build: { context: ./api-gateway, dockerfile: Containerfile }
    ports: ["8080:8080"]
    networks: ["biblioteca-net"]
    depends_on:
      - livro-service
      - autor-service
      - editora-service
      - aluguel-service
```

Uso:
```powershell
podman machine start
# só bancos (dev pela IDE):
podman-compose up -d postgres sqlserver-autor sqlserver-editora mongo
# stack completa:
podman-compose up -d
# reset:
podman-compose down -v
```

## 3. `Containerfile` multi-stage (mesmo para monolito e cada serviço)

```dockerfile
# ---- build ----
FROM docker.io/library/eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B dependency:go-offline
COPY src/ src/
RUN ./mvnw -B clean package -DskipTests && \
    java -Djarmode=tools -jar target/*.jar extract --layers --destination target/extracted

# ---- runtime ----
FROM docker.io/library/eclipse-temurin:25-jre
WORKDIR /app
RUN useradd -r -u 1001 spring
COPY --from=build /app/target/extracted/dependencies/ ./
COPY --from=build /app/target/extracted/spring-boot-loader/ ./
COPY --from=build /app/target/extracted/snapshot-dependencies/ ./
COPY --from=build /app/target/extracted/application/ ./
USER spring
EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
```

## 4. Testcontainers + Podman no Windows

`src/test/resources/application-test.yaml` ou variáveis de ambiente:

```powershell
$env:DOCKER_HOST = "npipe:////./pipe/podman-machine-default"
$env:TESTCONTAINERS_RYUK_DISABLED = "true"
```

```yaml
# application-test.yaml
spring:
  testcontainers:
    ryuk:
      enabled: false
```

Imagens de teste:
- Postgres: `postgres:17-alpine`
- SQL Server: `mcr.microsoft.com/mssql/server:2022-latest` (`MSSQLServerContainer`)
- MongoDB: `mongo:7` (`MongoDBContainer`)

## 5. Notas Podman (vs Docker)

| Necessidade | Podman |
| --- | --- |
| Iniciar runtime | `podman machine start` |
| Compose | `podman-compose` (pacote separado) ou `podman compose` |
| Registry padrão | precisa qualificar: `docker.io/library/postgres` |
| Rootless | padrão — portas < 1024 exigem config; usamos > 1024 |
| Socket p/ Testcontainers | named pipe `podman-machine-default` |
| Alias | `alias docker=podman` funciona, mas o binário É Podman |
