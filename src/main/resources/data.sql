-- Вставка данных в таблицу FRIENDSHIP_STATUS
INSERT INTO friendship_status (name)
SELECT 'REQUESTED' WHERE NOT EXISTS (SELECT 1 FROM friendship_status WHERE name = 'REQUESTED');

INSERT INTO friendship_status (name)
SELECT 'CONFIRMED' WHERE NOT EXISTS (SELECT 1 FROM friendship_status WHERE name = 'CONFIRMED');


-- Вставка данных в таблицу GENRES
INSERT INTO genres (id, name)
SELECT 1, 'Комедия' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE id = 1);

INSERT INTO genres (id, name)
SELECT 2, 'Драма' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE id = 2);

INSERT INTO genres (id, name)
SELECT 3, 'Мультфильм' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE id = 3);

INSERT INTO genres (id, name)
SELECT 4, 'Триллер' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE id = 4);

INSERT INTO genres (id, name)
SELECT 5, 'Документальный' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE id = 5);

INSERT INTO genres (id, name)
SELECT 6, 'Боевик' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE id = 6);

-- Вставка данных в таблицу MPA
INSERT INTO mpa (id, name, description)
SELECT 1, 'G', 'У фильма нет возрастных ограничений'
WHERE NOT EXISTS (SELECT 1 FROM mpa WHERE id = 1);

INSERT INTO mpa (id, name, description)
SELECT 2, 'PG', 'Детям рекомендуется смотреть фильм с родителями'
WHERE NOT EXISTS (SELECT 1 FROM mpa WHERE id = 2);

INSERT INTO mpa (id, name, description)
SELECT 3, 'PG-13', 'Детям до 13 лет просмотр нежелателен'
WHERE NOT EXISTS (SELECT 1 FROM mpa WHERE id = 3);

INSERT INTO mpa (id, name, description)
SELECT 4, 'R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого'
WHERE NOT EXISTS (SELECT 1 FROM mpa WHERE id = 4);

INSERT INTO mpa (id, name, description)
SELECT 5, 'NC-17', 'Лицам до 18 лет просмотр запрещён'
WHERE NOT EXISTS (SELECT 1 FROM mpa WHERE id = 5);