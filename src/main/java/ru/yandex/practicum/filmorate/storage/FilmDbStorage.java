package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        String sqlAll = "select * from FILMS";
        log.info("Вывод списка фильмов из базы данных");
        return jdbcTemplate.query(sqlAll, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film getFilmById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from FILMS where FILM_ID = ?", id);
        if (filmRows.next()) {
            Film film = Film.builder()
                    .id(filmRows.getInt("FILM_ID"))
                    .name(filmRows.getString("FILM_NAME"))
                    .description(filmRows.getString("DESCRIPTION"))
                    .releaseDate(filmRows.getDate("RELEASE_DATE").toLocalDate())
                    .duration(filmRows.getInt("DURATION"))
                    .likes(getSetLikes(filmRows.getInt("FILM_ID")))
                    .genre(getFilmGenre(filmRows.getInt("FILM_ID")))
                    .rating(getFilmRating(filmRows.getInt("RATING_ID"))
                    ).build();
            log.info("Найден фильм: ID - {} - {}", id, film.getName());
            return film;
        } else {
            log.info("По данному запросу ID = {} фильм не найден", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id фильма. Попробуйте еще раз.");
        }
    }

    @Override
    public Film addFilm(Film film) {
        String sqlFilmAdd = "INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection ->
        {
            PreparedStatement stmt = connection.prepareStatement(sqlFilmAdd, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, getFilmRatingId(film.getRating()));
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        return film;
    }

    @Override
    public Film refreshFilm(Film film) {
        return null;
    }

    private Film makeFilm(ResultSet filmsRows) throws SQLException {        //метод создания фильма для полного списка
        Film film = Film.builder()
                .id(filmsRows.getInt("FILM_ID"))
                .name(filmsRows.getString("FILM_NAME"))
                .description(filmsRows.getString("DESCRIPTION"))
                .releaseDate(filmsRows.getDate("RELEASE_DATE").toLocalDate())
                .duration(filmsRows.getInt("DURATION"))
                .rating(getFilmRating(filmsRows.getInt("RATING_ID")))
                .build();
        film.setGenre(getFilmGenre(filmsRows.getInt("FILM_ID")));
        film.setLikes(getSetLikes(filmsRows.getInt("FILM_ID")));
        return film;
    }

    private String getFilmRating(int id) {          //метод получения данных о фильме из таблицы FILMS_RATING
        SqlRowSet ratingRows = jdbcTemplate.queryForRowSet("select * from FILMS_RATING where RATING_ID = ?", id);
        if (ratingRows.next()) {
            return ratingRows.getString("RATING_NAME");
        } else {
            log.info("По данному запросу ID = {} рейтинг фильма не найден", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз.");
        }
    }

    private int getFilmRatingId(String rating) {
        SqlRowSet ratingRows = jdbcTemplate.queryForRowSet("select * from FILMS_RATING where RATING_NAME = ?", rating);
        if (ratingRows.next()) {
            return ratingRows.getInt("RATING_ID");
        } else {
            log.info("По данному запросу RATING_NAME = {} не удалось найти ID", rating);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный запрос. Попробуйте еще раз.");
        }
    }

    private List<String> getFilmGenre(int id) {     //метод получения жанра фильма из таблицы FILMS_GENRE
        String sql = "select * from FILMS_GENRES where FILMS_ID = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> getGenre(rs), id);
    }

    private String getGenre(ResultSet filmsGenreRows) throws SQLException { //метод получения жанра фильма из таблицы GENRE
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from GENRES where GENRE_ID = ?", filmsGenreRows.getInt("GENRE_ID"));
        if (genreRows.next()) {
            return genreRows.getString("GENRE_NAME");
        } else {
            log.info("По данному запросу ID = {} в таблице FILMS_GENRE рейтинг фильма не найден", filmsGenreRows.getInt("GENRE_ID"));
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз.");
        }
    }

    private Set<Integer> getSetLikes(int id) {
        String sqlLikes = "select * from FILMS_LIKES where FILM_ID = ?";
        return new HashSet<>((jdbcTemplate.query(sqlLikes, (rs, rowNum) -> getLikes(rs), id)));
    }

    private int getLikes(ResultSet likesRows) throws SQLException {
        return likesRows.getInt("USER_ID");
    }
}
