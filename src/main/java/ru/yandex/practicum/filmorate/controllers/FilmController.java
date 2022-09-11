package ru.yandex.practicum.filmorate.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger("FilmController");

    @GetMapping
    public List<Film> getFilms() {
        List<Film> listFilms = new ArrayList<>();
        listFilms.addAll(films.values());
        log.debug("Вывод списка фильмов");
        return listFilms;
    }

    @PostMapping
    public Film postFilm(@RequestBody Film film) {
        if ((film.getName() == null) || (film.getName().equals("")) || (film.getName().equals("null"))) {
            log.warn("Пустое название");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Название фильма не может быть пустым.");
        }
        if (film.getDescription().length() >= 200) {
            log.warn("Слишком длинное описание фильма");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Число символов описания фильма не должно превышать 200.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза фильма слишком рано");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата релиза фильма не может быть ранее 28.12.1895.");
        }
        if (film.getDuration() < 0) {
            log.warn("Отрицательная длительность фильма");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Длительность фильма не может быть меньше 0.");
        }
        if (film.getId() < 0) {
            log.warn("Некорректный id фильма в запросе - {}", film.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз.");
        }
        int id = Film.getIdentificator();
        if (!films.containsKey(film.getId())) {
            id++;
            film.setId(id);
            Film.setIdentificator(id);
        }
        films.put(film.getId(), film);
        log.info("Фильм {} добавлен/обновлен", film.getName());
        return film;
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {
        return postFilm(film);
    }
}