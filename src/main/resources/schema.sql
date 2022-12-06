create table MPA
(
    MPA_ID   INTEGER auto_increment,
    MPA_NAME CHARACTER VARYING(10) not null,
    constraint "MPA_pk"
        primary key (MPA_ID)
);

create table GENRES
(
    GENRE_ID   INTEGER auto_increment,
    GENRE_NAME CHARACTER VARYING(20) not null,
    constraint "GENRES_pk"
        primary key (GENRE_ID)
);

create table FILMS
(
    FILM_ID           INTEGER auto_increment,
    FILM_NAME         CHARACTER VARYING(30)  not null,
    FILM_DESCRIPTION  CHARACTER VARYING(200) not null,
    FILM_RELEASE_DATE DATE                   not null,
    FILM_DURATION     BIGINT                 not null,
    FILM_MPA          INTEGER,
    constraint "FILMS_pk"
        primary key (FILM_ID),
    constraint FILMS_MPA_MPA_ID_FK
        foreign key (FILM_MPA) references MPA
);

create table FILM_GENRES
(
    FILM_ID  INTEGER,
    GENRE_ID INTEGER,
    constraint "FILM_GENRES_pk"
        unique (GENRE_ID, FILM_ID),
    constraint "FILM_GENRES_FILMS_null_fk"
        foreign key (FILM_ID) references FILMS
            on delete cascade,
    constraint "FILM_GENRES_GENRES_null_fk"
        foreign key (GENRE_ID) references GENRES
            on delete cascade
);

create table if not exists USERS
(
    USER_ID       INTEGER auto_increment,
    USER_EMAIL    CHARACTER VARYING(50) not null,
    USER_LOGIN    CHARACTER VARYING(50) not null,
    USER_NAME     CHARACTER VARYING(50),
    USER_BIRTHDAY DATE                  not null,
    constraint "USERS_pk"
        primary key (USER_ID)
);

create table if not exists LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint "LIKES_unique"
        unique (FILM_ID, USER_ID),
    constraint "LIKES_FILMS_null_fk"
        foreign key (FILM_ID) references FILMS
            on delete cascade,
    constraint "LIKES_USERS_null_fk"
        foreign key (USER_ID) references USERS
            on delete cascade
);

create table FRIENDS
(
    USER_ID      INTEGER not null,
    FRIEND_ID    INTEGER not null,

    IS_CONFIRMED BOOLEAN default FALSE,
    constraint "FRIENDS_unique"
        unique (USER_ID, FRIEND_ID),
    constraint "FRIENDS_USERS_null_fk1"
        foreign key (USER_ID) references USERS
            on delete cascade,
    constraint "FRIENDS_USERS_null_fk2"
        foreign key (FRIEND_ID) references USERS
            on delete cascade
);