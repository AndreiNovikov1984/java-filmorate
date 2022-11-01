package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.support.Validation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> getFilms() {     // метод получения списка всех фильмов
        String sqlAll = "SELECT * FROM FILMS AS F " +
                "LEFT JOIN FILMS_RATING R on R.RATING_ID = F.RATING_ID";
        log.info("Вывод списка всех фильмов из базы данных");
        getGenresAllFilms();
        return jdbcTemplate.query(sqlAll, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film getFilmById(int id) {       // метод получения фильма по ID
        String sql = "SELECT * FROM FILMS as F " +
                "LEFT JOIN FILMS_RATING R on R.RATING_ID = F.RATING_ID " +
                "WHERE FILM_ID = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);
        getGenresAllFilms();
        if (filmRows.next()) {
            Film film = Film.builder()
                    .id(filmRows.getInt("FILM_ID"))
                    .name(filmRows.getString("FILM_NAME"))
                    .description(filmRows.getString("DESCRIPTION"))
                    .releaseDate(filmRows.getDate("RELEASE_DATE").toLocalDate())
                    .duration(filmRows.getInt("DURATION"))
                    .likes(getLikes(filmRows.getInt("FILM_ID")))
                    .genres(getGenresAllFilms().get(filmRows.getInt("FILM_ID")))
                    .mpa(new Mpa(filmRows.getInt("RATING_ID"), filmRows.getString("RATING_NAME")))
                    .build();
            log.info("Найден фильм: ID - {}, название - {}", id, film.getName());
            return film;
        } else {
            log.info("По данному запросу ID = {} фильм не найден", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id фильма. Попробуйте еще раз.");
        }
    }

    @Override
    public Film addFilm(Film film) {        // метод добавления фильма
        if (Validation.validationFilm(film)) {
            String sqlAddFilm = "INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) " +
                    "VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection ->
                    {
                        PreparedStatement stmt = connection.prepareStatement(sqlAddFilm, new String[]{"FILM_ID"});
                        stmt.setString(1, film.getName());
                        stmt.setString(2, film.getDescription());
                        stmt.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
                        stmt.setInt(4, film.getDuration());
                        stmt.setInt(5, film.getMpa().getId());
                        return stmt;
                    },
                    keyHolder);
            film.setId(keyHolder.getKey().intValue());
            if (film.getGenres() != null) {
                updateFilmGenre(film.getId(), film.getGenres());
            }
            if (film.getLikes() != null) {
                refreshLikes(film.getId(), film.getLikes());
            }
            log.info("Добавлен фильм: ID - {}", film.getId());
        }
        return film;
    }

    @Override
    public Film refreshFilm(Film film) {                                            // метод обновления фильма
        String sql = "SELECT FILM_ID FROM FILMS WHERE FILM_ID = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, film.getId());
        if ((Validation.validationFilm(film)) && ((filmRows.next()))) {
            String sqlRefreshFilm = "UPDATE FILMS SET " +
                    "FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ? " +
                    "WHERE FILM_ID = ?";
            jdbcTemplate.update(sqlRefreshFilm,
                    film.getName(),
                    film.getDescription(),
                    java.sql.Date.valueOf(film.getReleaseDate()),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            if (film.getGenres() != null) {
                updateFilmGenre(film.getId(), film.getGenres());
            }
            if (film.getLikes() != null) {
                refreshLikes(film.getId(), film.getLikes());
            }
            log.info("Обновлена информация по фильму: ID - {}", film.getId());
            return film;
        } else {
            log.warn("Некорректный запрос - {}", film.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Попробуйте еще раз.");
        }
    }

    @Override
    public Collection<Film> getFilmPopular(int count) { // метод вывода списка популярных фильмов
        String sql = "SELECT DISTINCT F.FILM_ID,\n" +
                "                F.FILM_NAME,\n" +
                "                F.DESCRIPTION,\n" +
                "                F.RELEASE_DATE,\n" +
                "                F.DURATION,\n" +
                "                R.RATING_ID,\n" +
                "                R.RATING_NAME,\n" +
                "               COUNT(L.USER_ID)\n" +
                "FROM FILMS as F\n" +
                "LEFT JOIN FILMS_RATING R on R.RATING_ID = F.RATING_ID\n" +
                "LEFT JOIN FILMS_LIKES L on F.FILM_ID = L.FILM_ID\n" +
                "GROUP BY F.FILM_ID\n" +
                "ORDER BY COUNT(L.USER_ID) DESC " +
                "LIMIT " + count;
        log.info("Вывод списка самых популярных фильмов из базы данных");
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public void addLike(Integer filmId, Integer userID) {       // метод добавления лайка
        Set<Integer> likes = getFilmById(filmId).getLikes();
        likes.add(userID);
        refreshLikes(filmId, likes);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userID) {     // метод удаления лайка
        Set<Integer> likes = getFilmById(filmId).getLikes();
        likes.remove(userID);
        refreshLikes(filmId, likes);
    }

    private Film makeFilm(ResultSet filmsRows) throws SQLException {        //метод создания фильма для списка
        Film film = Film.builder()
                .id(filmsRows.getInt("FILM_ID"))
                .name(filmsRows.getString("FILM_NAME"))
                .description(filmsRows.getString("DESCRIPTION"))
                .releaseDate(filmsRows.getDate("RELEASE_DATE").toLocalDate())
                .duration(filmsRows.getInt("DURATION"))
                .genres(getGenresAllFilms().get(filmsRows.getInt("FILM_ID")))
                .mpa(new Mpa(filmsRows.getInt("RATING_ID"), filmsRows.getString("RATING_NAME")))
                .build();
        film.setLikes(getLikes(filmsRows.getInt("FILM_ID")));
        return film;
    }

    private Set<Integer> getLikes(int id) {         //метод получения данных о лайках
        String sqlLikes = "SELECT * FROM FILMS_LIKES where FILM_ID = ";
        return new HashSet<>(jdbcTemplate.query(sqlLikes + id, (rs, rowNum) -> rs.getInt("USER_ID")));
    }

    private void refreshLikes(int id, Set<Integer> likes) {  //метод обновления информации о лайках
        jdbcTemplate.update("DELETE FROM FILMS_LIKES WHERE FILM_ID = ?", id);
        for (Integer like : likes) {
            String sql = "INSERT INTO FILMS_LIKES(FILM_ID, USER_ID) " +
                    "VALUES(?, ?)";
            jdbcTemplate.update(sql,
                    id,
                    like);
            log.info("Обновлена информация по лайкам фильма: ID - {}", id);
        }
    }

    private Map<Integer, LinkedHashSet<Genres>> getGenresAllFilms() {                      //метод получения данных о жанрах
        Map<Integer, LinkedHashSet<Genres>> genreMap = new HashMap<>();
        String sql = "SELECT FILM_ID,\n" +
                "       G.GENRE_ID,\n" +
                "       G.GENRE_NAME\n" +
                "FROM FILMS_GENRES AS FG\n" +
                "LEFT JOIN GENRES G on G.GENRE_ID = FG.GENRE_ID;";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql);
        while (genreRows.next()) {
            int id = genreRows.getInt("FILM_ID");
            Genres genre = new Genres(genreRows.getInt("GENRE_ID"), genreRows.getString("GENRE_NAME"));
            if (genreMap.containsKey(id)) {
                LinkedHashSet<Genres> genreList = genreMap.get(id);
                genreList.add(genre);
                genreMap.put(id, genreList);
            } else {
                LinkedHashSet<Genres> genreList = new LinkedHashSet<>();
                genreList.add(genre);
                genreMap.put(id, genreList);
            }
        }
        return genreMap;
    }

    private void updateFilmGenre(int id, Set<Genres> genreSet) {     //метод обновления жанров фильма из таблицы FILMS_GENRE
        jdbcTemplate.update("DELETE FROM FILMS_GENRES WHERE FILM_ID = ?", id);
        for (Genres genre : genreSet) {
            String sql = "INSERT INTO FILMS_GENRES(FILM_ID, GENRE_ID) " +
                    "VALUES(?, ?)";
            jdbcTemplate.update(sql,
                    id,
                    genre.getId());
            log.info("Обновлена информация по жанрам фильма: ID - {}", id);
        }
    }
}