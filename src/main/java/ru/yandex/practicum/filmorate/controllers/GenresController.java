package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.service.GenresService;

import java.util.Collection;

@Component
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenresController {
    private final GenresService genresService;

    @GetMapping                     // метод получения списка всех жанров
    public Collection<Genres> getFilmGenres() {
        return genresService.getGenres();
    }

    @GetMapping("/{id}")                     // метод получения списка всех жанров
    public Genres getFilmGenres(@PathVariable int id) {
        return genresService.getGenreById(id);
    }
}
