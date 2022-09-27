package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger("FilmService");
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getFilms() {        // метод получения списка фильмов
        log.debug("Вывод списка фильмов из хранилища");
        return filmStorage.getFilms();
    }

    public Film getFilmById(int id) {           // метод получения фильма по Id
        log.debug("Вывод фильма из хранилища по ID");
        return filmStorage.getFilmById(id);
    }

    public Collection<Film> getFilmPopular(int count) {     // метод получения списка популярных фильмов
        return filmStorage.getFilms().stream()
                .sorted((p0, p1) -> {
                    int comp = (p1.getLikes().size() - (p0.getLikes().size()));
                    return comp;
                })
                .limit(count).collect(Collectors.toList());
    }

    public Film postFilm(Film film) {       // метод добавления фильма
        Film postFilm = filmStorage.addFilm(film);
        log.info("Фильм {} успешно добавлен в хранилище", postFilm.getName());
        return postFilm;
    }

    public Film putFilm(Film film) {        // метод обновления фильма
        Film putFilm = filmStorage.refreshFilm(film);
        log.info("Фильм {} добавлен/обновлен в хранилище", film.getName());
        return putFilm;
    }

    public void addLike(Integer filmId, Integer userID) {       // метод добавления лайка
        validationId(filmId, userID);
        filmStorage.getFilmById(filmId).getLikes().add(userID);
        log.info("Фильм id = {} лайкнули", filmId);
    }

    public void deleteLike(Integer filmId, Integer userID) {     // метод удаления лайка
        validationId(filmId, userID);
        filmStorage.getFilmById(filmId).getLikes().remove(userID);
        log.info("Фильм id = {} дизлайкнули", filmId);
    }

    private void validationId(Integer filmId, Integer userID) {     //метод валидации Id фильма и пользователя
        if ((filmStorage.getFilmById(filmId) == null) || (userStorage.getUserById(userID) == null)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз.");
        }
        log.debug("Проверка по ID пройдена");
    }
}
