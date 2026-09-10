-- Módulo aluguel: tabela aluguel.alugueis + sequence + FK para catalogo.livros.

CREATE SEQUENCE aluguel.alugueis_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE aluguel.alugueis (
    id                       BIGINT        NOT NULL DEFAULT nextval('aluguel.alugueis_seq'),
    livro_id                 BIGINT        NOT NULL,
    nome_locatario           VARCHAR(200)  NOT NULL,
    data_retirada            DATE          NOT NULL,
    data_devolucao_prevista  DATE          NOT NULL,
    data_devolucao_real      DATE,
    taxa                     NUMERIC(10,2) NOT NULL,
    status                   VARCHAR(20)   NOT NULL,
    politica                 VARCHAR(30)   NOT NULL,
    criado_em                TIMESTAMPTZ   NOT NULL,
    atualizado_em            TIMESTAMPTZ   NOT NULL,
    CONSTRAINT pk_alugueis PRIMARY KEY (id),
    CONSTRAINT fk_alugueis_livro FOREIGN KEY (livro_id) REFERENCES catalogo.livros (id),
    CONSTRAINT ck_alugueis_taxa   CHECK (taxa >= 0),
    CONSTRAINT ck_alugueis_datas  CHECK (data_devolucao_prevista > data_retirada)
);

CREATE INDEX ix_alugueis_locatario ON aluguel.alugueis (lower(nome_locatario));
CREATE INDEX ix_alugueis_status    ON aluguel.alugueis (status);
CREATE INDEX ix_alugueis_previsao  ON aluguel.alugueis (data_devolucao_prevista);
