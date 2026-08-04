Excelente. Essa primeira fase é a mais importante do roadmap. Se você dominar esses fundamentos, todas as próximas fases (Microservices, Kafka, Kubernetes, IA, DevOps, etc.) ficarão muito mais fáceis.

Como seu objetivo é chegar em **nível Arquiteto de Software**, eu estruturaria as tarefas como uma Sprint de 2 semanas.

---

# FASE 1 — FUNDAMENTOS

## Duração

**2 semanas**

## Objetivo

Construir um CRUD distribuído em Java/Spring Boot entendendo os fundamentos que sustentam toda arquitetura distribuída.

---

# Site Oficial de Estudo (Único)

Se eu tivesse que indicar **apenas um**, seria:

> **Martin Fowler**
>
> [https://martinfowler.com](https://martinfowler.com)

É simplesmente a maior referência mundial em arquitetura de software.

Você encontrará praticamente tudo sobre:

* Distributed Systems
* Microservices
* Event Driven
* API
* REST
* DDD
* Hexagonal Architecture
* CQRS
* Event Sourcing
* Messaging
* Load Balance
* Integration Patterns
* Architecture Decision Records

Na minha opinião nenhum outro site consegue reunir tanta qualidade.

---

# TASK 01

## Conceitos de Sistemas Distribuídos

### Estudo

* [ ] O que é Sistema Distribuído
* [ ] Diferença entre Monólito e Distribuído
* [ ] Escalabilidade Vertical x Horizontal
* [ ] Transparência de localização
* [ ] Tipos de falhas distribuídas

### Mini Projeto

* [ ] Criar projeto Spring Boot
* [ ] Criar CRUD de Tarefas
* [ ] Criar arquitetura em camadas
* [ ] Conectar ao PostgreSQL
* [ ] Executar via Podman

---

# TASK 02

## CAP Theorem e Consistência

### Estudo

* [ ] Entender Consistency
* [ ] Entender Availability
* [ ] Entender Partition Tolerance
* [ ] Strong Consistency
* [ ] Eventual Consistency

### Mini Projeto

* [ ] Simular queda do banco
* [ ] Testar comportamento da API
* [ ] Registrar impacto
* [ ] Criar documentação ADR
* [ ] Comparar cenários

---

# TASK 03

## RPC e REST

### Estudo

* [ ] REST
* [ ] HTTP
* [ ] RPC
* [ ] gRPC
* [ ] Quando utilizar cada um

### Mini Projeto

* [ ] Criar API REST
* [ ] Implementar OpenAPI
* [ ] Criar Client HTTP
* [ ] Testar chamadas
* [ ] Documentar contrato

---

# TASK 04

## Sincronização de Tempo

### Estudo

* [ ] NTP
* [ ] Clock Drift
* [ ] Timestamp
* [ ] TimeZone
* [ ] Relógios Lógicos

### Mini Projeto

* [ ] Padronizar UTC
* [ ] Criar Auditoria
* [ ] Registrar CreatedAt
* [ ] Registrar UpdatedAt
* [ ] Testar TimeZone

---

# TASK 05

## Arquitetura Client / Hexagonal

### Estudo

* [ ] Ports and Adapters
* [ ] Dependency Rule
* [ ] Adapter
* [ ] Port
* [ ] Domain Isolation

### Mini Projeto

* [ ] Separar Domain
* [ ] Criar Application
* [ ] Criar Infrastructure
* [ ] Criar Controller
* [ ] Criar Repository Adapter

---

# TASK 06

## Event Driven Architecture (EDA)

### Estudo

* [ ] Evento
* [ ] Producer
* [ ] Consumer
* [ ] Publish Subscribe
* [ ] Event Notification

### Mini Projeto

Mesmo sem Kafka ainda.

* [ ] Criar Domain Event
* [ ] Publicar evento Spring
* [ ] Criar Listener
* [ ] Registrar Log
* [ ] Criar Event Handler

---

# TASK 07

## Load Balance

### Estudo

* [ ] Round Robin
* [ ] Least Connections
* [ ] Sticky Session
* [ ] Health Check
* [ ] Reverse Proxy

### Mini Projeto

* [ ] Criar duas instâncias da API
* [ ] Configurar Nginx
* [ ] Balancear requisições
* [ ] Testar Failover
* [ ] Medir tempo de resposta

---

# Bibliotecas Java (pom.xml)

Essas bibliotecas já permitem estudar praticamente toda a Fase 1.

### Spring Boot

```xml
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-validation
spring-boot-starter-actuator
spring-boot-starter-aop
spring-boot-starter-cache
spring-boot-starter-test
```

---

### Banco

```xml
postgresql
flyway-core
```

---

### Documentação

```xml
springdoc-openapi-starter-webmvc-ui
```

---

### Mapeamento

```xml
mapstruct
lombok
```

---

### Observabilidade

```xml
micrometer-registry-prometheus
```

---

### Logs

```xml
logback-classic
logstash-logback-encoder
```

---

### HTTP Client

```xml
spring-boot-starter-webflux
```

(para utilizar `WebClient`, que substituirá o `RestTemplate`)

---

### Resiliência

```xml
resilience4j-spring-boot3
```

---

### Testes

```xml
testcontainers
testcontainers-postgresql
mockito
assertj
```

---

### Cache

```xml
caffeine
```

---

### Utilidades

```xml
commons-lang3
jackson-datatype-jsr310
```

---

# Infraestrutura Podman

Você pode subir todo o ambiente com um `podman-compose.yml` contendo:

* PostgreSQL
* PgAdmin
* Prometheus
* Grafana
* Nginx (para balanceamento)
* Aplicação Spring Boot

Isso permitirá que você pratique, desde a primeira fase, conceitos de:

* containers;
* banco de dados;
* observabilidade;
* balanceamento de carga;
* métricas;
* monitoramento.

---

# Estrutura do projeto

```text
task-api/

src/main/java
 ├── application
 ├── domain
 ├── infrastructure
 │      ├── persistence
 │      ├── configuration
 │      ├── messaging
 │      └── web
 └── shared
```

Essa organização já segue a filosofia da Arquitetura Hexagonal e servirá de base para todas as fases seguintes do roadmap.

## Entregáveis da Fase 1

Ao concluir esta etapa, você deverá ter:

* [ ] CRUD de tarefas funcionando em Spring Boot.
* [ ] PostgreSQL executando em Podman.
* [ ] Migrações de banco com Flyway.
* [ ] API documentada com OpenAPI/Swagger.
* [ ] Arquitetura Hexagonal organizada.
* [ ] Eventos de domínio usando o mecanismo de eventos do Spring.
* [ ] Observabilidade básica com Actuator e métricas para Prometheus.
* [ ] Logs estruturados.
* [ ] Balanceamento de carga via Nginx entre duas instâncias da aplicação.
* [ ] ADR documentando as principais decisões arquiteturais.

Essa base deixará seu projeto preparado para a Fase 2 (Arquiteturas Modernas), onde você poderá evoluir o mesmo sistema para microserviços, API Gateway, mensageria e padrões avançados, sem precisar reconstruí-lo do zero.
