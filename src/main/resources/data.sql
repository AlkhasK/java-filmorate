MERGE INTO GENRES KEY (GENRE_ID)
    VALUES (1, 'Комедия');
MERGE INTO GENRES KEY (GENRE_ID)
    VALUES (2, 'Драма');
MERGE INTO GENRES KEY (GENRE_ID)
    VALUES (3, 'Мультфильм');
MERGE INTO GENRES KEY (GENRE_ID)
    VALUES (4, 'Триллер');
MERGE INTO GENRES KEY (GENRE_ID)
    VALUES (5, 'Документальный');
MERGE INTO GENRES KEY (GENRE_ID)
    VALUES (6, 'Боевик');

MERGE INTO MPA KEY (MPA_ID)
    VALUES (1, 'G');
MERGE INTO MPA KEY (MPA_ID)
    VALUES (2, 'PG');
MERGE INTO MPA KEY (MPA_ID)
    VALUES (3, 'PG-13');
MERGE INTO MPA KEY (MPA_ID)
    VALUES (4, 'R');
MERGE INTO MPA KEY (MPA_ID)
    VALUES (5, 'NC-17');