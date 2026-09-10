-- Módulo editora: tabela editora.editoras + sequence.

CREATE SEQUENCE editora.editoras_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE editora.editoras (
    id             BIGINT       NOT NULL DEFAULT nextval('editora.editoras_seq'),
    nome           VARCHAR(200) NOT NULL,
    cnpj           VARCHAR(14)  NOT NULL,
    cidade         VARCHAR(100),
    site           VARCHAR(200),
    criado_em      TIMESTAMPTZ  NOT NULL,
    atualizado_em  TIMESTAMPTZ  NOT NULL,
    CONSTRAINT pk_editoras PRIMARY KEY (id),
    CONSTRAINT uq_editoras_cnpj UNIQUE (cnpj)
);

CREATE INDEX ix_editoras_nome ON editora.editoras (nome);
