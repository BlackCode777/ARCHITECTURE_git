# 01 — Modelo de Domínio: Sistema de Reserva de Livros

> Vale para os dois projetos. No monolito é um único modelo; nos microsserviços
> cada serviço tem **sua fatia** do modelo e referencia as outras por ID.

## 1. Diagrama de classes (conceitual)

```
                 ┌───────────────┐          ┌───────────────┐
                 │    Autor      │          │   Editora     │
                 ├───────────────┤          ├───────────────┤
                 │ id            │          │ id            │
                 │ nome          │          │ nome          │
                 │ nacionalidade │          │ cnpj          │
                 │ nascimento    │          │ cidade        │
                 │ biografia     │          │ site          │
                 └───────┬───────┘          └───────┬───────┘
                         │ 1                        │ 1
                         │                          │
                         │ N                        │ N
                     ┌───┴──────────────────────────┴───┐
                     │             Livro                │
                     ├──────────────────────────────────┤
                     │ id                               │
                     │ titulo                           │
                     │ isbn                             │
                     │ numeroPaginas                    │
                     │ anoPublicacao                    │
                     │ genero : GeneroLiterario  (enum) │
                     │ sinopse                          │
                     │ autorId    ──► Autor             │
                     │ editoraId  ──► Editora           │
                     │ exemplaresTotais                 │
                     │ exemplaresDisponiveis            │
                     └───────────────┬──────────────────┘
                                     │ 1
                                     │
                                     │ N
                          ┌──────────┴───────────┐
                          │       Aluguel        │
                          ├──────────────────────┤
                          │ id                   │
                          │ livroId   ──► Livro  │
                          │ nomeLocatario        │
                          │ dataRetirada         │
                          │ dataDevolucaoPrevista│
                          │ dataDevolucaoReal    │
                          │ taxa : BigDecimal    │
                          │ status : StatusAluguel│
                          │ politica : String     │
                          └──────────────────────┘
```

## 2. Enums

### GeneroLiterario

```java
public enum GeneroLiterario {
    AVENTURA, ACAO, FICCAO_CIENTIFICA, FANTASIA, ROMANCE,
    TERROR, SUSPENSE, BIOGRAFIA, HISTORIA, TECNICO, INFANTIL, POESIA
}
```

### StatusAluguel

```java
public enum StatusAluguel {
    ATIVO,      // livro retirado, dentro do prazo
    DEVOLVIDO,  // livro devolvido
    ATRASADO    // passou da data prevista e não foi devolvido
}
```

## 3. Invariantes (regras que o domínio nunca pode violar)

### Livro
- `numeroPaginas` > 0
- `exemplaresDisponiveis` entre 0 e `exemplaresTotais`
- `isbn` único
- `genero` obrigatório

### Autor
- `nome` obrigatório
- `nascimento` no passado

### Editora
- `nome` obrigatório
- `cnpj` único e válido (14 dígitos)

### Aluguel
- `dataDevolucaoPrevista` > `dataRetirada`
- só pode criar aluguel se o livro tiver `exemplaresDisponiveis` > 0
- ao criar: decrementa `exemplaresDisponiveis` do livro
- ao devolver: incrementa `exemplaresDisponiveis`, grava `dataDevolucaoReal`, muda status
- `taxa` >= 0, calculada por uma `PoliticaTarifacao` (nunca informada pelo cliente)

## 4. Política de tarifação — herança e polimorfismo

Classe abstrata no `domain` do módulo/serviço de aluguel:

```java
public abstract class PoliticaTarifacao {

    /** Valor base por dia de aluguel. Subclasses definem. */
    protected abstract BigDecimal valorDiario();

    /** Ganho para a subclasse ajustar o total (multiplicador, desconto...). */
    protected BigDecimal ajustar(BigDecimal subtotal, ContextoTarifacao ctx) {
        return subtotal;
    }

    /** Template method — não sobrescrever. */
    public final BigDecimal calcular(ContextoTarifacao ctx) {
        long dias = ChronoUnit.DAYS.between(ctx.retirada(), ctx.devolucaoPrevista());
        BigDecimal subtotal = valorDiario().multiply(BigDecimal.valueOf(dias));
        return ajustar(subtotal, ctx).setScale(2, RoundingMode.HALF_UP);
    }
}
```

| Subclasse | Comportamento |
| --- | --- |
| `TarifacaoPadrao` | `valorDiario()` = R$ 2,00; sem ajuste |
| `TarifacaoPorGenero` | `valorDiario()` = R$ 2,00; `ajustar()` multiplica por fator do gênero (`TECNICO` ×2, `INFANTIL` ×0,5, resto ×1) |
| `TarifacaoPromocional` | `valorDiario()` = R$ 2,00; `ajustar()` zera os 7 primeiros dias e aplica 10% de desconto no restante |

`ContextoTarifacao` é um `record` imutável com: `retirada`, `devolucaoPrevista`,
`genero`, `numeroPaginas`. **Encapsulamento**: as datas e a taxa do `Aluguel`
só mudam por métodos de negócio (`devolver()`, `marcarAtraso()`), nunca por setter público.

## 5. Casos de uso (use cases) por entidade

| Entidade | Use cases |
| --- | --- |
| Autor | Criar, Atualizar, BuscarPorId, Listar (paginado), Remover |
| Editora | Criar, Atualizar, BuscarPorId, Listar (paginado), Remover |
| Livro | Criar, Atualizar, BuscarPorId, Listar (filtro por gênero/autor), Remover |
| Aluguel | Alugar, Devolver, BuscarPorId, ListarPorLocatario, ListarAtrasados |

## 6. O que muda entre monolito e microsserviços

| Aspecto | Monolito | Microsserviços |
| --- | --- | --- |
| `Livro.autorId` | FK real no banco, `JOIN` disponível | ID "solto"; `livro-service` chama `autor-service` por REST para exibir o nome |
| Criar aluguel valida livro | consulta a tabela `livro` na mesma transação | `aluguel-service` chama `GET /livros/{id}` no `livro-service` |
| Decrementar exemplar | mesma transação ACID do aluguel | chamada REST `PATCH /livros/{id}/baixa-exemplar`; consistência eventual, com compensação se o aluguel falhar |
| Enum `GeneroLiterario` | uma classe compartilhada | **duplicada** em cada serviço que precisa (contrato, não código compartilhado) |
