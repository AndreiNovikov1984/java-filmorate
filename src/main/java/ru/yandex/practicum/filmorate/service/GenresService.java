package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.storage.GenresStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenresService {
    private final GenresStorage genresStorage;
    private static final Logger log = LoggerFactory.getLogger("GenreService");


    public Collection<Genres> getGenres() {        // метод получения списка всех жанров
        return genresStorage.getGenres();
    }

    public Genres getGenreById(int id) {            // метод получения списка всех жанров
        if (id <= 0) {
            log.warn("Передан некорректный ID {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз.");
        }
        return genresStorage.getGenreById(id);
    }
}

