INSERT INTO USERS (USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY)
VALUES ('some1@email.ru', 'login1', 'name1', '2020-03-03');
INSERT INTO USERS (USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY)
VALUES ('some2@email.ru', 'login2', 'name2', '2020-03-03');
INSERT INTO USERS (USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY)
VALUES ('some1@email.ru', 'login3', 'name3', '2020-03-03');
INSERT INTO USERS (USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY)
VALUES ('some2@email.ru', 'login4', 'name4', '2020-03-03');

INSERT INTO FRIENDS (USER_ID, FRIEND_ID, IS_CONFIRMED)
VALUES (1, 2, false);