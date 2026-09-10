-- Módulo autor: tabela autor.autores + sequence de IDs.
-- INCREMENT 50 casa com EntidadeBase.ALLOCATION_SIZE / @SequenceGenerator.

CREATE SEQUENCE autor.autores_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE autor.autores (
    id             BIGINT       NOT NULL DEFAULT nextval('autor.autores_seq'),
    nome           VARCHAR(200) NOT NULL,
    nacionalidade  VARCHAR(100),
    nascimento     DATE,
    biografia      VARCHAR(4000),
    criado_em      TIMESTAMPTZ  NOT NULL,
    atualizado_em  TIMESTAMPTZ  NOT NULL,
    CONSTRAINT pk_autores PRIMARY KEY (id)
);

CREATE INDEX ix_autores_nome ON autor.autores (nome);
