package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.support.Validation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> films = new HashMap<>();
    private static int identificator = 0;
    private static final Logger log = LoggerFactory.getLogger("InMemoryFilmStorage");

    @Override
    public Collection<Film> getFilms() {        // метод получения списка фильмов
        log.debug("Получение списка фильмов");
        return films.values();
    }

    @Override
    public Film getFilmById(int id) {        // метод получения фильма по Id
        log.debug("Получение фильма по id");
        return films.values().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз."));
    }

    @Override
    public Film addFilm(Film film) {        // метод добавления фильма
        if (Validation.validationFilm(film, log)) {
            if (!films.containsKey(film.getId())) {
                identificator++;
                film.setId(identificator);
            }
            films.put(film.getId(), film);
            log.info("Фильм {} добавлен", film.getName());
        }
        return film;
    }

    @Override
    public Film refreshFilm(Film film) {        // метод обновления фильма
        if (Validation.validationFilm(film, log)) {
            if (!films.containsKey(film.getId())) {
                identificator++;
                film.setId(identificator);
            }
            films.put(film.getId(), film);
            log.info("Фильм {} добавлен/обновлен", film.getName());
        }
        return film;
    }
}
