# 04 — Comparativo: Monolito × Microsserviços

> Lado a lado, com o **mesmo sistema de reserva de livros** dos dois jeitos.

## 1. Tabela-resumo

| Dimensão | Monolito modular | Microsserviços |
| --- | --- | --- |
| **Unidades de deploy** | 1 | 5 (4 serviços + gateway) |
| **Processos em runtime** | 1 JVM | 5 JVMs |
| **Bancos** | 1 PostgreSQL (4 schemas) | 4 bancos isolados (Postgres, SQL Server ×2, Mongo) |
| **Comunicação entre partes** | chamada de método (ns) | chamada de rede REST (ms) — pode falhar |
| **Transação "alugar + baixar exemplar"** | 1 transação ACID | saga / compensação manual |
| **Consulta "livro + autor + editora"** | 1 SQL com `JOIN` | 1 query local + 2 chamadas HTTP (ou cache) |
| **Consistência dos dados** | forte, imediata | eventual entre serviços |
| **Escala** | tudo junto (vertical, ou N cópias do monolito inteiro) | por serviço (só o aluguel, se for o gargalo) |
| **Isolamento de falha** | baixo — erro fatal derruba tudo | alto — aluguel-service cair não impede cadastrar autor |
| **Heterogeneidade de tecnologia** | uma stack para tudo | cada serviço escolhe (Mongo só no aluguel) |
| **Deploy independente por time** | não — release compartilhado | sim |
| **Refactor entre partes** | mudança de compilação | mudança de contrato de rede (versionamento) |
| **Ambiente local** | 1 app + 1 banco | 5 apps + 4 bancos + rede |
| **Observabilidade** | 1 log, 1 profiler — trivial | logs distribuídos, tracing, correlação — obrigatório |
| **Custo de infra** | baixo | alto (mais contêineres, mais memória) |
| **Custo cognitivo inicial** | baixo | alto |
| **Onde o acoplamento aparece** | em `import` proibido no código | em chamada HTTP e formato de payload |

## 2. Cenários concretos deste domínio

### Cenário A — "Alugar um livro"

**Monolito:**
```java
@Transactional
public AluguelResponse alugar(AlugarCommand cmd) {
    Livro livro = livroRepository.buscar(cmd.livroId());   // mesma JVM, mesma tx
    livro.baixarExemplar();                                // muda estado
    BigDecimal taxa = politica.calcular(ctx);
    Aluguel aluguel = Aluguel.criar(livro.id(), cmd, taxa);
    aluguelRepository.salvar(aluguel);
    livroRepository.salvar(livro);
    return mapper.toResponse(aluguel);
}   // commit → tudo ou nada
```
Se qualquer linha falha, **rollback total**. Sem esforço.

**Microsserviços:**
```java
public AluguelResponse alugar(AlugarCommand cmd) {
    var disp = livroGateway.disponibilidade(cmd.livroId());   // HTTP → pode dar timeout
    if (!disp.disponivel()) throw new LivroIndisponivelException();

    BigDecimal taxa = politica.calcular(ctx);
    Aluguel aluguel = aluguelRepository.salvar(Aluguel.criar(...));  // Mongo: já persistido

    try {
        livroGateway.baixarExemplar(cmd.livroId());          // HTTP → pode falhar AQUI
    } catch (Exception e) {
        aluguelRepository.remover(aluguel.id());             // COMPENSAÇÃO manual
        throw new AluguelNaoConcluidoException(e);
    }
    return mapper.toResponse(aluguel);
}
```
Sem transação global. O desenvolvedor **codifica a consistência**.

### Cenário B — "Listar 20 livros com nome do autor"

- **Monolito:** `SELECT l.*, a.nome FROM catalogo.livros l JOIN autor.autores a ON ...` — uma query.
- **Microsserviços:** 1 query em `livro_db` + até 20 chamadas `GET /autores/{id}`.
  Mitigações: cache local de autores no livro-service, `GET /autores?ids=1,2,3`
  (batch), ou desnormalizar `autorNome` dentro de `livro_db` (duplicação controlada).

### Cenário C — "Editora saiu do ar"

- **Monolito:** não existe esse cenário isolado — ou tudo está no ar, ou nada está.
- **Microsserviços:** `editora-service` fora → cadastro de autor e consulta de
  aluguel **continuam funcionando**. Criar livro degrada (fallback: cria sem
  validar editora, ou recusa 503). Isolamento de falha real.

### Cenário D — "O aluguel tem 100× mais tráfego que o cadastro"

- **Monolito:** sobe-se N cópias do monolito **inteiro** (autor + editora + livro
  + aluguel), mesmo que só o aluguel precise. Desperdício de memória.
- **Microsserviços:** `podman ... --replicas 5` só no `aluguel-service`. Os outros
  ficam com 1 instância.

## 3. Quando escolher cada um

### Escolha **monolito modular** quando:
- Time pequeno (1–8 devs) ou um time só.
- Domínio ainda em descoberta — as fronteiras vão mudar.
- Consistência forte é requisito frequente.
- Time-to-market > escalabilidade granular.
- Orçamento de infra/ops enxuto.
- **É quase sempre o ponto de partida certo.**

### Escolha **microsserviços** quando:
- Múltiplos times precisam liberar em ritmos diferentes.
- Partes do sistema têm perfis de carga muito distintos.
- Partes precisam de tecnologias distintas (ex.: busca, ML, streaming).
- As fronteiras do domínio já estão **estáveis e comprovadas**.
- A organização tem maturidade de DevOps (CI/CD, observabilidade, on-call).

### O caminho recomendado
> **Monolito modular primeiro.** Se ele foi bem modularizado, extrair um serviço
> depois é um trabalho contido. Começar em microsserviços sem conhecer o domínio
> é pagar todo o custo antecipado por um benefício que talvez nunca venha.

## 4. O que este estudo demonstra na prática

1. O **mesmo domínio** cabe nas duas arquiteturas — a diferença não é o "o quê", é o "como".
2. Microsserviços **trocam** simplicidade de código por flexibilidade operacional.
3. `JOIN` e transação ACID são um **luxo** que você perde ao distribuir.
4. "Banco por serviço" força o desacoplamento — e é o que mais dói.
5. Um monolito **modular** já tem 80% do valor arquitetural sem o custo distribuído.
