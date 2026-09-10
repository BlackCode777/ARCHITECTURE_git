-- Módulo livro (catálogo): tabela catalogo.livros + sequence + FKs.
-- FK cross-schema para autor.autores e editora.editoras (integridade referencial
-- no monólito; nos microsserviços isso vira validação por chamada de API).

CREATE SEQUENCE catalogo.livros_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE catalogo.livros (
    id                     BIGINT       NOT NULL DEFAULT nextval('catalogo.livros_seq'),
    titulo                 VARCHAR(300) NOT NULL,
    isbn                   VARCHAR(20)  NOT NULL,
    numero_paginas         INTEGER      NOT NULL,
    ano_publicacao         INTEGER,
    genero                 VARCHAR(30)  NOT NULL,
    sinopse                VARCHAR(4000),
    autor_id               BIGINT       NOT NULL,
    editora_id             BIGINT       NOT NULL,
    exemplares_totais      INTEGER      NOT NULL,
    exemplares_disponiveis INTEGER      NOT NULL,
    criado_em              TIMESTAMPTZ  NOT NULL,
    atualizado_em          TIMESTAMPTZ  NOT NULL,
    CONSTRAINT pk_livros PRIMARY KEY (id),
    CONSTRAINT uq_livros_isbn UNIQUE (isbn),
    CONSTRAINT fk_livros_autor   FOREIGN KEY (autor_id)   REFERENCES autor.autores (id),
    CONSTRAINT fk_livros_editora FOREIGN KEY (editora_id) REFERENCES editora.editoras (id),
    CONSTRAINT ck_livros_paginas       CHECK (numero_paginas > 0),
    CONSTRAINT ck_livros_exemplares    CHECK (exemplares_disponiveis BETWEEN 0 AND exemplares_totais)
);

CREATE INDEX ix_livros_genero ON catalogo.livros (genero);
CREATE INDEX ix_livros_autor  ON catalogo.livros (autor_id);
