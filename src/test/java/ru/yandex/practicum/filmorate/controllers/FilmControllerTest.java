package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTest {

    private FilmController filmController;
    private Film film;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
        film = Film.builder()
                .name("Vasiliy")
                .description("Vasiliy..")
                .releaseDate(LocalDate.of(1999, 9, 9))
                .duration(190)
                .build();
    }

    @Test
    public void getAllFilms() {
        Film test = filmController.postFilm(film);
        List<Film> listFilms = filmController.getFilms();
        assertEquals(test, listFilms.get(0), "Данные не получены");
        assertEquals(1, listFilms.size(), "Данные не получены");
    }

    @Test
    public void postFilm() {
        Film test = filmController.postFilm(film);
        assertEquals(test, film, "Данные не получены");
    }

    @Test
    public void postFilmNameEmpty() {
        film.setName("");
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            Film test = filmController.postFilm(film);
        });
        Assertions.assertEquals("400 BAD_REQUEST \"Название фильма не может быть пустым.\"", exeption.getMessage());
    }

    @Test
    public void postFilmDescriptionLong() {
        film.setDescription("Vasiliy Vasilievich Vasiliev get to meet Ivan Ivanovich Ivanov. " +
                "They are meeting in the Ivanov city on the Vasilievskaya street. They were very happy to meet each " +
                "other. And they lived happily ever after");
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            Film test = filmController.postFilm(film);
        });
        Assertions.assertEquals("400 BAD_REQUEST \"Число символов описания фильма не должно превышать 200.\"", exeption.getMessage());
    }


    @Test
    public void postFilmReleaseDateEarly() {
        film.setReleaseDate(LocalDate.of(1099, 9, 9));
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            Film test = filmController.postFilm(film);
        });
        Assertions.assertEquals("400 BAD_REQUEST \"Дата релиза фильма не может быть ранее 28.12.1895.\"", exeption.getMessage());
    }

    @Test
    public void postFilmDurationNegative() {
        film.setDuration(-20);
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            Film test = filmController.postFilm(film);
        });
        Assertions.assertEquals("400 BAD_REQUEST \"Длительность фильма не может быть меньше 0.\"", exeption.getMessage());
    }

    @Test
    public void putFilm() {
        filmController.postFilm(film);
        film.setId(1);
        Film test = filmController.putFilm(film);
        assertEquals(test, film, "Данные не получены");
    }

    @Test
    public void putFilmIdIncorrect() {
        film.setId(-1);
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            Film test = filmController.putFilm(film);
        });
        Assertions.assertEquals("404 NOT_FOUND \"Некорректный id. Попробуйте еще раз.\"", exeption.getMessage());
    }
}
