package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenresServiceTest {
    private final GenresService genresService;
    private Genres genres;

    @Test
    @Sql(scripts = {"file:src/test/resources/test.sql"})
    public void getAllGenres() {
        Collection<Genres> genres = genresService.getGenres();
        assertNotNull(genres);
        assertEquals(6, genres.size(), "Ошибка");
    }

    @Test
    public void getGenresbyId() {
        genres = genresService.getGenreById(1);
        assertNotNull(genres);
        assertEquals(1, genres.getId(), "Данные не получены");
    }

    @Test
    public void getGenresbyIncorrectId() {
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            genresService.getGenreById(-1);
        });
        Assertions.assertEquals("404 NOT_FOUND \"Некорректный id. Попробуйте еще раз.\"", exeption.getMessage());
    }
}

