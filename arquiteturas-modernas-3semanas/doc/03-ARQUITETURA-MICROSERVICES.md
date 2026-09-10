# 03 — Arquitetura dos Microsserviços (`biblioteca-microservices`)

> O mesmo domínio, quebrado em **4 serviços independentes** + **1 API Gateway**.
> Cada serviço: projeto Maven próprio, deploy próprio, **banco próprio**.

## 1. Mapa dos serviços

```
                         ┌────────────────────────┐
        4 clientes  ───► │  api-gateway  (:8080)  │  Spring Cloud Gateway
                         └───┬───────┬───────┬─────┘
              ┌──────────────┘       │       └──────────────┐
              ▼              ▼       ▼                      ▼
     ┌────────────────┐ ┌──────────┐ ┌──────────────┐ ┌──────────────┐
     │ livro-service  │ │autor-svc │ │ editora-svc  │ │ aluguel-svc  │
     │    :8081       │ │  :8082   │ │    :8083     │ │    :8084     │
     └───────┬────────┘ └────┬─────┘ └──────┬───────┘ └──────┬───────┘
             ▼               ▼              ▼                ▼
      ┌─────────────┐  ┌───────────┐  ┌───────────┐   ┌────────────┐
      │ PostgreSQL  │  │ SQL Server│  │ SQL Server│   │  MongoDB   │
      │   :5432     │  │  A :1433  │  │  B :1434  │   │  :27017    │
      │ livro_db    │  │ autor_db  │  │ editora_db│   │ aluguel_db │
      └─────────────┘  └───────────┘  └───────────┘   └────────────┘

     Comunicação serviço→serviço: REST síncrono via OpenFeign
       livro-service  ──GET /autores/{id}──►  autor-service
       livro-service  ──GET /editoras/{id}─►  editora-service
       aluguel-service ─GET /livros/{id}────►  livro-service (via gateway ou direto)
       aluguel-service ─PATCH /livros/{id}/baixa-exemplar──► livro-service
```

## 2. Responsabilidade de cada serviço

| Serviço | Porta | Banco | Domínio | Depende de |
| --- | --- | --- | --- | --- |
| **api-gateway** | 8080 | — | Roteamento, CORS, agregação de rotas, ponto único de entrada | todos |
| **livro-service** | 8081 | PostgreSQL `livro_db` | Livro, GeneroLiterario, exemplares (total/disponível) | autor-service, editora-service |
| **autor-service** | 8082 | SQL Server A `autor_db` | Autor | — |
| **editora-service** | 8083 | SQL Server B `editora_db` | Editora | — |
| **aluguel-service** | 8084 | MongoDB `aluguel_db` | Aluguel, PoliticaTarifacao, StatusAluguel | livro-service |

`autor-service` e `editora-service` não dependem de ninguém — são folhas.

## 3. Estrutura interna de um serviço (ex.: `livro-service`)

Mesma arquitetura hexagonal do monolito, só que o "outro módulo" virou **cliente HTTP**:

```
com.tesseracodelabs.livro
├── LivroServiceApplication.java
├── domain
│   ├── Livro.java
│   ├── GeneroLiterario.java          (enum — cópia local, é contrato)
│   └── LivroRepository.java          (PORT saída - persistência)
├── application
│   ├── CriarLivroUseCase.java
│   ├── ...
│   └── port
│       ├── AutorGateway.java         (PORT saída - chama autor-service)
│       └── EditoraGateway.java       (PORT saída - chama editora-service)
├── infrastructure
│   ├── persistence
│   │   ├── LivroEntity.java
│   │   ├── LivroJpaRepository.java
│   │   ├── LivroJpaAdapter.java
│   │   └── LivroPersistenceMapper.java
│   └── client
│       ├── AutorFeignClient.java     (@FeignClient(name="autor-service"))
│       ├── AutorFeignAdapter.java    (implementa AutorGateway; + fallback)
│       ├── EditoraFeignClient.java
│       └── EditoraFeignAdapter.java
└── web
    ├── LivroController.java
    ├── dto/ (CriarLivroRequest, LivroResponse, ...)
    └── LivroWebMapper.java
```

## 4. Contratos entre serviços (o que cada um expõe)

### autor-service expõe
| Método | Rota | Resposta |
| --- | --- | --- |
| GET | `/autores/{id}` | `AutorResponse { id, nome, nacionalidade }` |
| GET | `/autores/{id}/existe` | `200` ou `404` |

### editora-service expõe
| Método | Rota | Resposta |
| --- | --- | --- |
| GET | `/editoras/{id}` | `EditoraResponse { id, nome, cidade }` |
| GET | `/editoras/{id}/existe` | `200` ou `404` |

### livro-service expõe
| Método | Rota | Uso |
| --- | --- | --- |
| GET | `/livros/{id}` | leitura; devolve nome do autor e da editora já resolvidos |
| GET | `/livros/{id}/disponibilidade` | `{ disponivel: boolean, exemplaresDisponiveis: n }` |
| PATCH | `/livros/{id}/baixa-exemplar` | aluguel-service chama ao alugar |
| PATCH | `/livros/{id}/retorno-exemplar` | aluguel-service chama ao devolver |

### aluguel-service expõe
| Método | Rota |
| --- | --- |
| POST | `/alugueis` |
| POST | `/alugueis/{id}/devolucao` |
| GET | `/alugueis/{id}` |
| GET | `/alugueis?locatario=` |

## 5. Fluxo distribuído: criar um aluguel

```
POST /alugueis  (via gateway → aluguel-service)
     │
     ▼
AlugarUseCase (aluguel-service)
  1. livroGateway.buscarDisponibilidade(livroId)   ──HTTP──► livro-service
        ← { disponivel: true, exemplaresDisponiveis: 3 }
  2. se indisponível → 409 Conflict, encerra
  3. politica = escolherPolitica(genero)            (polimorfismo)
     taxa = politica.calcular(contexto)
  4. aluguel = Aluguel.criar(livroId, locatario, datas, taxa)
  5. aluguelRepository.salvar(aluguel)              ──► MongoDB
  6. livroGateway.baixarExemplar(livroId)           ──HTTP──► livro-service
        se falhar (passo 6):
          → compensação: aluguelRepository.remover(aluguel.id)
          → 502 Bad Gateway
     │
     ▼
201 Created + Location: /alugueis/{id}
```

Aqui aparece o preço dos microsserviços: **não há transação ACID** cobrindo
"salvar aluguel" + "baixar exemplar". Precisa de lógica de compensação
(saga simplificada / try-confirm-cancel manual).

## 6. API Gateway (Spring Cloud Gateway)

`application.yaml` do gateway:

```yaml
spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: livro
          uri: http://livro-service:8081
          predicates:
            - Path=/api/livros/**
          filters:
            - StripPrefix=1
        - id: autor
          uri: http://autor-service:8082
          predicates:
            - Path=/api/autores/**
          filters:
            - StripPrefix=1
        - id: editora
          uri: http://editora-service:8083
          predicates:
            - Path=/api/editoras/**
          filters:
            - StripPrefix=1
        - id: aluguel
          uri: http://aluguel-service:8084
          predicates:
            - Path=/api/alugueis/**
          filters:
            - StripPrefix=1
server:
  port: 8080
```

> Service discovery (Eureka/Consul) fica de fora nesta fase — os hostnames vêm
> do `podman-compose` (rede interna). URIs fixas mantêm o estudo simples.

## 7. Resiliência (OpenFeign + Resilience4j)

Cada adapter Feign tem:
- **timeout** de conexão/leitura (ex.: 2s / 3s)
- **retry** (2 tentativas, backoff)
- **fallback**: se `autor-service` está fora, `livro-service` devolve o livro com
  `autorNome = "(indisponível)"` em vez de estourar 500 — degradação graciosa.

## 8. Banco por serviço — o ponto central do estudo

| Serviço | Tecnologia | Por que essa escolha (didática) |
| --- | --- | --- |
| livro-service | **PostgreSQL** | relacional maduro, JSON nativo, o "default sensato" |
| autor-service | **SQL Server** | mostrar heterogeneidade; dialeto e tipos diferentes (`UNIQUEIDENTIFIER`, `datetime2`) |
| editora-service | **SQL Server** (2ª instância) | mesmo SGBD, **instância física separada** → prova que "mesmo banco" ≠ "mesma instância"; isolamento de falha |
| aluguel-service | **MongoDB** | agregado `Aluguel` é um documento autocontido; sem migrations; consistência eventual combina com o fluxo distribuído |

Nenhum serviço acessa o banco do outro. Nem por leitura. Se precisa do dado, **pede pela API**.

## 9. O que fica mais difícil (a explorar no doc 04)

- Uma consulta "livros com nome do autor" vira N chamadas HTTP (ou um cache).
- Não há `JOIN` entre serviços.
- Transação distribuída → saga / compensação.
- 5 processos, 5 logs, 4 bancos, 1 rede → observabilidade vira requisito, não luxo.
- Versionar contrato de API entre serviços.
- Ambiente local sobe 4 apps + 4 bancos + gateway.
