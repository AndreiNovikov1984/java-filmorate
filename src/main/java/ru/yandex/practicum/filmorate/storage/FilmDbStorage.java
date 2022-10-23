package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger("FilmDbStorage");
    private Map<Integer, Film> films = new HashMap<>();
    private static int identificator;
    JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> getFilms() {
        String sql = "select * from FILMS";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film getFilmById(int id) {
        SqlRowSet filmsRows = jdbcTemplate.queryForRowSet("select * from FILMS where FILM_ID = ?", id);
        if (filmsRows.next()) {
            Film film = Film.builder()
                    .id(filmsRows.getInt("FILM_ID"))
                    .name(filmsRows.getString("FILM_NAME"))
                    .description(filmsRows.getString("DESCRIPTION"))
                    .releaseDate(filmsRows.getDate("RELEASE_DATE").toLocalDate())
                    .duration(filmsRows.getInt("DURATION"))
                    .rating(getFilmRating(filmsRows.getInt("RATING_ID"))
                    ).build();
            log.info("Найден фильм: ID - {} - {}", film.getId(), film.getName());
            return film;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            return null;
        }
    }

    @Override
    public Film addFilm(Film film) {
        return null;
    }

    @Override
    public Film refreshFilm(Film film) {
        return null;
    }

    private Film makeFilm(ResultSet filmsRows) throws SQLException {
        Film film = Film.builder()
                .id(filmsRows.getInt("FILM_ID"))
                .name(filmsRows.getString("FILM_NAME"))
                .description(filmsRows.getString("DESCRIPTION"))
                .releaseDate(filmsRows.getDate("RELEASE_DATE").toLocalDate())
                .duration(filmsRows.getInt("DURATION"))
                .rating(getFilmRating(filmsRows.getInt("RATING_ID")))
                .build();
        return film;
    }

    private String getFilmRating(int id) {
        SqlRowSet ratingRows = jdbcTemplate.queryForRowSet("select * from FILMS_RATING where RATING_ID = ?", id);
        if (ratingRows.next()) {
            return ratingRows.getString("RATING_NAME");
        } else {
            return null;
        }
    }
}
