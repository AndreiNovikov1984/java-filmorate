package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Component
public class MpaDbStorage implements MpaStorage {
    private static final Logger log = LoggerFactory.getLogger("MpaDbStorage");
    JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Mpa> getMpas() { //метод получения всех рейтингов фильма из таблицы FILMS_RATING
        String mpaRows = "SELECT * FROM FILMS_RATING";
        log.info("Вывод списка рейтингов из базы данных");
        return jdbcTemplate.query(mpaRows, (rs, rowNum) -> new Mpa(
                rs.getInt("RATING_ID"),
                rs.getString("RATING_NAME")));
    }

    @Override
    public Mpa getMpaById(int id) {             // метод получения рейтинга по ID
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILMS_RATING WHERE RATING_ID = ?", id);
        if (mpaRows.next()) {
            log.info("По данному запросу ID = {} рейтинг успешно найден", id);
            return new Mpa(mpaRows.getInt("RATING_ID"), mpaRows.getString("RATING_NAME"));
        } else {
            log.info("По данному ID = {} рейтинг не найден", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз.");
        }
    }
}
