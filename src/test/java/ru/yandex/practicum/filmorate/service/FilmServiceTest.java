package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmServiceTest {
    private final FilmService filmService;
    private Film film;
    private ResponseStatusException exeption;

    @BeforeEach
    public void beforeEach() {
        film = Film.builder()
                .name("Vasiliy")
                .description("Vasiliy..")
                .releaseDate(LocalDate.of(1999, 9, 9))
                .duration(190)
                .mpa(new Mpa(1, "G"))
                .build();
    }

    @Test
    @Sql(scripts = {"file:src/test/resources/test.sql"})
    public void getAllFilms() {
        Collection<Film> films = filmService.getFilms();
        assertNotNull(films);
        assertEquals(3, films.size(), "Ошибка");
    }

    @Test
    public void getFIlmById() {
        film = filmService.getFilmById(1);
        assertNotNull(film);
        assertEquals(1, film.getId(), "Данные не получены");
    }

    @Test
    public void getFilmByIdIncorrect() {
        exeption = Assertions.assertThrows(ResponseStatusException.class, () ->
                filmService.getFilmById(0));
        Assertions.assertEquals("404 NOT_FOUND \"Некорректный id фильма. Попробуйте еще раз.\"", exeption.getMessage());
    }

    @Test
    @Sql(scripts = {"file:src/test/resources/test.sql"})
    public void addFilm() {
        Film test = filmService.postFilm(film);
        assertNotNull(test);
        assertEquals(test, film, "Данные не получены");
    }

    @Test
    public void addFilmNameEmpty() {
        film.setName("");
        exeption = Assertions.assertThrows(ResponseStatusException.class, () ->
                filmService.postFilm(film));
        Assertions.assertEquals("400 BAD_REQUEST \"Название фильма не может быть пустым.\"", exeption.getMessage());
    }

    @Test
    public void addFilmDescriptionLong() {
        film.setDescription("Vasiliy Vasilievich Vasiliev get to meet Ivan Ivanovich Ivanov. " +
                "They are meeting in the Ivanov city on the Vasilievskaya street. They were very happy to meet each " +
                "other. And they lived happily ever after");
        exeption = Assertions.assertThrows(ResponseStatusException.class, () ->
                filmService.postFilm(film));
        Assertions.assertEquals("400 BAD_REQUEST \"Число символов описания фильма не должно превышать 200.\"", exeption.getMessage());
    }

    @Test
    public void addFilmReleaseDateEarly() {
        film.setReleaseDate(LocalDate.of(1099, 9, 9));
        exeption = Assertions.assertThrows(ResponseStatusException.class, () ->
                filmService.postFilm(film));
        Assertions.assertEquals("400 BAD_REQUEST \"Дата релиза фильма не может быть ранее 28.12.1895.\"", exeption.getMessage());
    }

    @Test
    public void addFilmDurationNegative() {
        film.setDuration(-20);
        exeption = Assertions.assertThrows(ResponseStatusException.class, () ->
                filmService.postFilm(film));
        Assertions.assertEquals("400 BAD_REQUEST \"Длительность фильма не может быть меньше 0.\"", exeption.getMessage());
    }

    @Test
    @Sql(scripts = {"file:src/test/resources/test.sql"})
    public void refreshFilm() {
        film = filmService.postFilm(film);
        Film test = filmService.putFilm(film);
        assertEquals(test, film, "Данные не получены");
    }

    @Test
    public void putFilmIdIncorrect() {
        film.setId(-1);
        exeption = Assertions.assertThrows(ResponseStatusException.class, () ->
                filmService.putFilm(film));
        Assertions.assertEquals("404 NOT_FOUND \"Некорректный id. Попробуйте еще раз.\"", exeption.getMessage());
    }

    @Test
    @Sql(scripts = {"file:src/test/resources/test.sql"})
    public void getFilmPopular() {
        Collection<Film> popolar = filmService.getFilmPopular(5);
        assertNotNull(popolar);
        assertEquals(3, popolar.size(), "Данные не получены");
    }

    @Test
    public void getFilmPopularIncorrectCount() {
        exeption = Assertions.assertThrows(ResponseStatusException.class, () ->
                filmService.getFilmPopular(-1));
        Assertions.assertEquals("400 BAD_REQUEST \"Длина списка не может быть меньше 0\"", exeption.getMessage());
    }

    @Test
    @Sql(scripts = {"file:src/test/resources/test.sql"})
    public void addFilmLike() {
        filmService.addLike(1, 3);
        Set<Integer> likes = filmService.getFilmById(1).getLikes();
        assertNotNull(likes);
        assertEquals(2, likes.size(), "Данные не получены");
    }

    @Test
    public void addFilmLikeIncorrectFilmId() {
        exeption = Assertions.assertThrows(ResponseStatusException.class, () ->
                filmService.addLike(5, 3));
        Assertions.assertEquals("404 NOT_FOUND \"Некорректный id фильма. Попробуйте еще раз.\"", exeption.getMessage());
    }

    @Test
    public void addFilmLikeIncorrectUserId() {
        exeption = Assertions.assertThrows(ResponseStatusException.class, () ->
                filmService.addLike(1, 5));
        Assertions.assertEquals("404 NOT_FOUND \"Некорректный id пользователя. Попробуйте еще раз.\"", exeption.getMessage());
    }

    @Test
    @Sql(scripts = {"file:src/test/resources/test.sql"})
    public void deleteFilmLike() {
        filmService.deleteLike(2, 3);
        Set<Integer> likes = filmService.getFilmById(1).getLikes();
        assertNotNull(likes);
        assertEquals(1, likes.size(), "Данные не получены");
    }

    @Test
    public void deleteFilmLikeIncorrectFilmId() {
        exeption = Assertions.assertThrows(ResponseStatusException.class, () ->
                filmService.deleteLike(5, 3));
        Assertions.assertEquals("404 NOT_FOUND \"Некорректный id фильма. Попробуйте еще раз.\"", exeption.getMessage());
    }

    @Test
    public void deleteFilmLikeIncorrectUserId() {
        exeption = Assertions.assertThrows(ResponseStatusException.class, () ->
                filmService.deleteLike(1, 5));
        Assertions.assertEquals("404 NOT_FOUND \"Некорректный id пользователя. Попробуйте еще раз.\"", exeption.getMessage());
    }

}
