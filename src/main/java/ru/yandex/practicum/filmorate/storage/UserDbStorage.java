package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@Primary
public class UserDbStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger("FilmDbStorage");
    private static int identificator;
    JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> getUsers() {
        String sqlAll = "select * from USERS";
        log.info("Вывод списка пользователей из базы данных");
        return jdbcTemplate.query(sqlAll, (rs, rowNum) -> makeUSER(rs));
    }

    public User getUserById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from USERS where USER_ID = ?", id);
        if (userRows.next()) {
            User user = User.builder()
                    .id(userRows.getInt("USER_ID"))
                    .login(userRows.getString("USER_LOGIN"))
                    .name(userRows.getString("USER_NAME"))
                    .email(userRows.getString("USER_EMAIL"))
                    .birthday(userRows.getDate("BIRTHDATE").toLocalDate())
                    .build();
            log.info("Найден пользователь: ID - {} - {}", id, user.getName());
            return user;
        } else {
            log.info("По данному запросу ID = {} пользователь не найден", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id пользователя. Попробуйте еще раз.");
        }
   }

    public User addUser(User user) {
        return null;
    }

    public User refreshUser(User user) {
        return null;
    }

    private User makeUSER(ResultSet usersRows) throws SQLException {
        User user = User.builder()
                .id(usersRows.getInt("USER_ID"))
                .login(usersRows.getString("USER_LOGIN"))
                .name(usersRows.getString("USER_NAME"))
                .email(usersRows.getString("USER_EMAIL"))
                .birthday(usersRows.getDate("BIRTHDATE").toLocalDate())
                .build();
    //    user.setFriends(getUserFriends(usersRows.getInt("USER_ID")));
        return user;
    }
}
