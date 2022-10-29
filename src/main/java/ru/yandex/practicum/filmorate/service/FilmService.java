package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> getFilms() {        // метод получения списка фильмов
        return filmStorage.getFilms();
    }

    public Film getFilmById(int id) {           // метод получения фильма по Id
        return filmStorage.getFilmById(id);
    }

    public Collection<Film> getFilmPopular(int count) {     // метод получения списка популярных фильмов
        if (count < 0) {
            log.warn("Передана некорректная длина списка count = {} ", count);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Длина списка не может быть меньше 0");
        }
        log.info("Вывод списка самых популярных фильмов длиной {} ", count);
        return filmStorage.getFilmPopular(count);
    }

    public Film postFilm(Film film) {       // метод добавления фильма
        Film postFilm = filmStorage.addFilm(film);
        log.info("Фильм ID = {} успешно добавлен в хранилище", postFilm.getId());
        return postFilm;
    }

    public Film putFilm(Film film) {        // метод обновления фильма
        Film putFilm = filmStorage.refreshFilm(film);
        log.info("Фильм ID = {} обновлен/добавлен в хранилище", film.getId());
        return putFilm;
    }

    public void addLike(Integer filmId, Integer userID) {       // метод добавления лайка
        validationId(filmId, userID);
        filmStorage.addLike(filmId, userID);
        log.info("Лайк пользователя ID = {} фильму ID = {} добавлен", userID, filmId);
    }

    public void deleteLike(Integer filmId, Integer userID) {     // метод удаления лайка
        validationId(filmId, userID);
        filmStorage.deleteLike(filmId, userID);
        log.info("Лайк пользователя ID = {} у фильма ID = {} удален", userID, filmId);
    }

    private void validationId(Integer filmId, Integer userID) {     //метод валидации Id фильма и пользователя
        if (filmStorage.getFilmById(filmId) == null) {
            log.warn("Передан некорректный ID {}", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз.");
        }
        if (userStorage.getUserById(userID) == null) {
            log.warn("Передан некорректный ID {}", userID);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз.");
        }
        log.info("Проверка по ID пройдена");
    }
}
