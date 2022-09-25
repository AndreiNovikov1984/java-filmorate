package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.support.Validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger("FilmController");
    private static int identificator = 0;


    @GetMapping
    public List<Film> getFilms() {
        List<Film> listFilms = new ArrayList<>();
        listFilms.addAll(films.values());
        log.debug("Вывод списка фильмов");
        return listFilms;
    }

    @PostMapping
    public Film postFilm(@RequestBody Film film) {
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

    @PutMapping
    public Film putFilm(@RequestBody Film film) {
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