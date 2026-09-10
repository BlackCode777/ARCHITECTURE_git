-- Dados de exemplo para explorar a API pelo Swagger.
-- IDs fixos (baixos) — as sequences começam em 1 e alocam de 50 em 50, então
-- não colidem com o primeiro nextval da aplicação.

-- Autores
INSERT INTO autor.autores (id, nome, nacionalidade, nascimento, biografia, criado_em, atualizado_em) VALUES
 (1, 'Isaac Asimov',        'Russo-americano', DATE '1920-01-02', 'Autor de ficcao cientifica e divulgacao.', now(), now()),
 (2, 'Ursula K. Le Guin',   'Americana',       DATE '1929-10-21', 'Ficcao cientifica e fantasia.',           now(), now()),
 (3, 'Machado de Assis',    'Brasileiro',      DATE '1839-06-21', 'Maior nome do realismo brasileiro.',      now(), now());

-- Editoras
INSERT INTO editora.editoras (id, nome, cnpj, cidade, site, criado_em, atualizado_em) VALUES
 (1, 'Editora Aleph',          '11222333000181', 'Sao Paulo',      'https://editoraaleph.com.br', now(), now()),
 (2, 'Companhia das Letras',   '45723174000110', 'Sao Paulo',      'https://companhiadasletras.com.br', now(), now());

-- Livros
INSERT INTO catalogo.livros
 (id, titulo, isbn, numero_paginas, ano_publicacao, genero, sinopse, autor_id, editora_id, exemplares_totais, exemplares_disponiveis, criado_em, atualizado_em) VALUES
 (1, 'Fundacao',                 '9788576570000', 244, 1951, 'FICCAO_CIENTIFICA', 'O imperio galactico em declinio.', 1, 1, 3, 3, now(), now()),
 (2, 'Eu, Robo',                 '9788576570017', 320, 1950, 'FICCAO_CIENTIFICA', 'Contos sobre as tres leis da robotica.', 1, 1, 2, 2, now(), now()),
 (3, 'A Mao Esquerda da Escuridao','9788535920000', 300, 1969, 'FICCAO_CIENTIFICA', 'Genero e politica em um planeta gelido.', 2, 2, 2, 2, now(), now()),
 (4, 'Os Despossuidos',          '9788535920017', 400, 1974, 'FICCAO_CIENTIFICA', 'Utopia ambigua.', 2, 2, 1, 1, now(), now()),
 (5, 'Dom Casmurro',             '9788535910000', 256, 1899, 'ROMANCE',           'Bentinho, Capitu e o ciume.', 3, 2, 4, 4, now(), now()),
 (6, 'Memorias Postumas de Bras Cubas','9788535910017', 208, 1881, 'ROMANCE',       'Narrado por um defunto autor.', 3, 2, 2, 2, now(), now());

-- Alugueis de exemplo (um ativo, um ja devolvido)
INSERT INTO aluguel.alugueis
 (id, livro_id, nome_locatario, data_retirada, data_devolucao_prevista, data_devolucao_real, taxa, status, politica, criado_em, atualizado_em) VALUES
 (1, 5, 'Ana Souza',   CURRENT_DATE - 3, CURRENT_DATE + 4, NULL,             14.00, 'ATIVO',     'PADRAO', now(), now()),
 (2, 1, 'Bruno Lima',  CURRENT_DATE - 20, CURRENT_DATE - 13, CURRENT_DATE - 15, 14.00, 'DEVOLVIDO', 'PADRAO', now(), now());

-- Ajusta exemplares disponiveis do livro 5 (1 alugado ativo)
UPDATE catalogo.livros SET exemplares_disponiveis = 3 WHERE id = 5;

-- Avanca as sequences para depois dos IDs inseridos manualmente, senao o
-- primeiro nextval da aplicacao colide com estes registros.
SELECT setval('autor.autores_seq',   (SELECT max(id) FROM autor.autores));
SELECT setval('editora.editoras_seq',(SELECT max(id) FROM editora.editoras));
SELECT setval('catalogo.livros_seq', (SELECT max(id) FROM catalogo.livros));
SELECT setval('aluguel.alugueis_seq',(SELECT max(id) FROM aluguel.alugueis));
