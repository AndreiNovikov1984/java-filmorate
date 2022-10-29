package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.Collection;


@Component
@Slf4j
public class GenresDbStorage implements GenresStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenresDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genres> getGenres() {         //метод получения всех жанров фильма из таблицы GENRES
        String genreRows = "SELECT * FROM GENRES";
        log.info("Вывод списка жанров из базы данных");
        return jdbcTemplate.query(genreRows, (rs, rowNum) -> new Genres(
                rs.getInt("GENRE_ID"),
                rs.getString("GENRE_NAME")));
    }

    @Override
    public Genres getGenreById(int id) {            //метод получения жанров по ID из таблицы GENRES
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM GENRES WHERE GENRE_ID = ?", id);
        if (genreRows.next()) {
            log.info("По данному запросу ID = {} жанр успешно найден", id);
            return new Genres(genreRows.getInt("GENRE_ID"), genreRows.getString("GENRE_NAME"));
        } else {
            log.info("По данному ID = {} жанр не найден", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз.");
        }
    }
}