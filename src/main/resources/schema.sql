DROP TABLE IF EXISTS USERS cascade;
DROP TABLE IF EXISTS FILMS_RATING cascade;
DROP TABLE IF EXISTS GENRES cascade;
DROP TABLE IF EXISTS FILMS cascade;
DROP TABLE IF EXISTS FILMS_GENRES cascade;
DROP TABLE IF EXISTS FILMS_LIKES cascade;
DROP TABLE IF EXISTS FRIENDS cascade;


create table IF NOT EXISTS USERS
(
    USER_ID    INTEGER auto_increment
        primary key,
    USER_LOGIN CHARACTER VARYING(50) not null
        unique,
    USER_NAME  CHARACTER VARYING(50) not null,
    USER_EMAIL CHARACTER VARYING(70) not null
        unique,
    BIRTHDATE  DATE                  not null
);

create table IF NOT EXISTS FILMS_RATING
(
    RATING_ID   INTEGER primary key,
    RATING_NAME CHARACTER VARYING(50) not null
);

create table IF NOT EXISTS GENRES
(
    GENRE_ID   INTEGER primary key,
    GENRE_NAME CHARACTER VARYING(50) not null
);

create table IF NOT EXISTS FILMS
(
    FILM_ID      INTEGER auto_increment
        primary key,
    FILM_NAME    CHARACTER VARYING(50) not null,
    DESCRIPTION  CHARACTER VARYING(200),
    RELEASE_DATE DATE,
    DURATION     INTEGER,
    RATING_ID    INTEGER,
    constraint FILMS_FILMS_RATING_RATING_ID_FK
        foreign key (RATING_ID) references FILMS_RATING
);

create table IF NOT EXISTS FILMS_GENRES
(
    FILMS_GENRE_ID INTEGER auto_increment
        primary key,
    FILM_ID       INTEGER,
    GENRE_ID       INTEGER,
    constraint FILMS_GENRES_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS,
    constraint FILMS_GENRES_GENRES_GENRE_ID_FK
        foreign key (GENRE_ID) references GENRES
);

create table IF NOT EXISTS FILMS_LIKES
(
    FILMS_LIKES_ID INTEGER auto_increment
        primary key,
    FILM_ID        INTEGER,
    USER_ID        INTEGER,
    constraint FILMS_LIKES_FILMS_FILMS_ID_FK
        foreign key (FILM_ID) references FILMS,
    constraint FILMS_LIKES_USERS_USERS_ID_FK
        foreign key (USER_ID) references USERS
);

create table IF NOT EXISTS FRIENDS
(
    FRIENDS_ID        INTEGER auto_increment
        primary key,
    USER_ID           INTEGER,
    FRIEND_ID         INTEGER,
    constraint FRIENDS_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS,
    constraint FRIENDS_USERS_USER_ID_FK_2
        foreign key (FRIEND_ID) references USERS
);