package ru.yandex.practicum.filmorate.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.support.Validation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> films = new HashMap<>();
    private static int identificator = 0;

    @Override
    public Collection<Film> getFilms() {        // метод получения списка фильмов
        return films.values();
    }

    @Override
    public Film getFilmById(int id) {        // метод получения фильма по Id
        return films.values().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз."));
    }

    @Override
    public Film addFilm(Film film) {        // метод добавления фильма
        if (Validation.validationFilm(film)) {
            if (!films.containsKey(film.getId())) {
                identificator++;
                film.setId(identificator);
            }
            films.put(film.getId(), film);
        }
        return film;
    }

    @Override
    public Film refreshFilm(Film film) {        // метод обновления фильма
        if (Validation.validationFilm(film)) {
            if (!films.containsKey(film.getId())) {
                identificator++;
                film.setId(identificator);
            }
            films.put(film.getId(), film);
        }
        return film;
    }

    @Override
    public Collection<Film> getFilmPopular(int count) {
        return getFilms().stream()
                .sorted((p0, p1) -> {
                    int comp = (p1.getLikes().size() - (p0.getLikes().size()));
                    return comp;
                })
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void addLike(Integer filmId, Integer userID) {       // метод добавления лайка
        getFilmById(filmId).getLikes().add(userID);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userID) {     // метод удаления лайка
        getFilmById(filmId).getLikes().remove(userID);
    }
}
