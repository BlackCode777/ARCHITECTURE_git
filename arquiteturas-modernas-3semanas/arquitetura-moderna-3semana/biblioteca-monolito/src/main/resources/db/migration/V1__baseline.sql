-- V1 — baseline do schema do biblioteca-monolito.
-- Os schemas por módulo e as tabelas entram nas próximas migrations:
--   V2__schema_autor.sql
--   V3__schema_editora.sql
--   V4__schema_catalogo.sql
--   V5__schema_aluguel.sql
--   V6__dados_exemplo.sql
-- Ver ../../../doc/06-TASKS-MONOLITO.md

CREATE SCHEMA IF NOT EXISTS autor;
CREATE SCHEMA IF NOT EXISTS editora;
CREATE SCHEMA IF NOT EXISTS catalogo;
CREATE SCHEMA IF NOT EXISTS aluguel;
