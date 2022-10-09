package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getFilms() {        // метод получения списка фильмов
        return filmService.getFilms();
    }

    @GetMapping("/{id}")                        // метод получения фильма по Id
    public Film getFilmById(@PathVariable int id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")                     // метод получения списка популярных фильмов
    public Collection<Film> getFilmPopular(@RequestParam(defaultValue = "10", required = false) int count) {
        return filmService.getFilmPopular(count);
    }

    @PostMapping
    public Film postFilm(@RequestBody Film film) {      // метод добавления фильма
        Film postFilm = filmService.postFilm(film);
        return postFilm;
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {       // метод обновления фильма
        Film putFilm = filmService.putFilm(film);
        return putFilm;
    }

    @PutMapping("/{id}/like/{userId}")                  // метод добавления лайка
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")               // метод удаления лайка
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.deleteLike(id, userId);
    }
}