package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> getFilms();

    Film getFilmById(int id);

    Film addFilm(Film film);

    Film refreshFilm(Film film);

    Collection<Film> getFilmPopular(int count);

    void addLike(Integer filmId, Integer userID);

    void deleteLike(Integer filmId, Integer userID);

}
