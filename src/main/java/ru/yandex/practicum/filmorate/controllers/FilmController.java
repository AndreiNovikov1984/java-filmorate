package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger("FilmController");
    private final FilmService filmService;

    @Autowired
    private FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getFilms() {        // метод получения списка фильмов
        log.debug("Вывод списка фильмов из хранилища");
        return filmService.getFilms();
    }

    @GetMapping("/{id}")                        // метод получения фильма по Id
    public Film getFilmById(@PathVariable int id) {
        log.debug("Вывод фильма из хранилища по ID");
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")                     // метод получения списка популярных фильмов
    public Collection<Film> getFilmPopular(@RequestParam(defaultValue = "10", required = false) int count) {
        log.debug("Вывод самых популярных фильмов");
        return filmService.getFilmPopular(count);
    }

    @PostMapping
    public Film postFilm(@RequestBody Film film) {      // метод добавления фильма
        Film postFilm = filmService.postFilm(film);
        log.info("Фильм {} успешно добавлен в хранилище", postFilm.getName());
        return postFilm;
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {       // метод обновления фильма
        Film putFilm = filmService.putFilm(film);
        log.info("Фильм {} добавлен/обновлен в хранилище", film.getName());
        return putFilm;
    }

    @PutMapping("/{id}/like/{userId}")                  // метод добавления лайка
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.addLike(id, userId);
        log.info("Фильм лайкнули");
    }

    @DeleteMapping("/{id}/like/{userId}")               // метод удаления лайка
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.deleteLike(id, userId);
        log.info("Фильм дизлайкнули");
    }
}