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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.support.Validation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Primary
public class UserDbStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger("UserDbStorage");
    JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> getUsers() {  // метод получения списка всех пользователей
        String sqlAll = "SELECT * FROM USERS";
        log.info("Вывод списка пользователей из базы данных");
        Map<Integer, Set<User>> friendsMap = getAllFriends();
        return jdbcTemplate.query(sqlAll, (rs, rowNum) -> User.builder()
                .id(rs.getInt("USER_ID"))
                .login(rs.getString("USER_LOGIN"))
                .name(rs.getString("USER_NAME"))
                .email(rs.getString("USER_EMAIL"))
                .birthday(rs.getDate("BIRTHDATE").toLocalDate())
                .friends(friendsMap.get(rs.getInt("USER_ID")))
                .build());
    }

    @Override
    public User getUserById(int id) {           // метод получения пользователя по ID
        String sql = "SELECT * FROM USERS WHERE USER_ID = " + id;
        Map<Integer, Set<User>> friendsMap = getAllFriends();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql);
        if (userRows.next()) {
            User user = User.builder()
                    .id(userRows.getInt("USER_ID"))
                    .login(userRows.getString("USER_LOGIN"))
                    .name(userRows.getString("USER_NAME"))
                    .email(userRows.getString("USER_EMAIL"))
                    .birthday(userRows.getDate("BIRTHDATE").toLocalDate())
                    .friends(friendsMap.get(id))
                    .build();
            log.info("Найден пользователь: ID - {} - {}", id, user.getName());
            return user;
        } else {
            log.info("По данному запросу ID = {} пользователь не найден", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id пользователя. Попробуйте еще раз.");
        }
    }

    @Override
    public User addUser(User user) {
        if (Validation.validationUser(user)) {
            String sqlAddUser = "INSERT INTO USERS (USER_LOGIN, USER_NAME, USER_EMAIL, BIRTHDATE) " +
                    "values (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection ->
                    {
                        PreparedStatement stmt = connection.prepareStatement(sqlAddUser, new String[]{"USER_ID"});
                        stmt.setString(1, user.getLogin());
                        stmt.setString(2, user.getName());
                        stmt.setString(3, user.getEmail());
                        stmt.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
                        return stmt;
                    },
                    keyHolder);
            user.setId(keyHolder.getKey().intValue());
            if (user.getFriends() != null) {
                addFriendWhenAddUser(user.getId(), user.getFriends());
            }
            log.info("Добавлен пользователь: ID - {}", user.getId());
        }
        return user;
    }

    @Override
    public User refreshUser(User user) {
        String sql = "SELECT USER_ID FROM USERS WHERE USER_ID = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, user.getId());
        if ((Validation.validationUser(user)) && ((userRows.next()))) {
            String sqlRefreshFilm = "UPDATE USERS SET " +
                    "USER_LOGIN = ?, USER_NAME = ?, USER_EMAIL = ?, BIRTHDATE = ? " +
                    "WHERE USER_ID = ?";
            jdbcTemplate.update(sqlRefreshFilm,
                    user.getLogin(),
                    user.getName(),
                    user.getEmail(),
                    java.sql.Date.valueOf(user.getBirthday()),
                    user.getId());
            if (user.getFriends() != null) {
                addFriendWhenRefreshUser(user.getId(), user.getFriends());
            }
            log.info("Обновлена информация пользователя: ID - {}", user.getId());
            return user;
        } else {
            log.warn("Некорректный запрос - {}", user.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Попробуйте еще раз.");
        }
    }

    @Override
    public Collection<User> getUsersFriends(Integer userId) {      // метод получения друзей пользователя
        String sql = "SELECT U.USER_ID, " +
                " U.USER_LOGIN, " +
                " U.USER_NAME, " +
                " U.USER_EMAIL, " +
                " U.BIRTHDATE " +
                "FROM FRIENDS AS F " +
                "LEFT JOIN USERS U on U.USER_ID = F.FRIEND_ID\n" +
                "WHERE F.USER_ID = ";
        log.info("Вывод списка друзей пользователя ID = {} из базы данных", userId);
        return jdbcTemplate.query(sql + userId, (rs, rowNum) -> makeUSER(rs));
    }

    @Override
    public Collection<User> getUsersMutualFriends(Integer userId, Integer otherId) {   // метод получения совпадающих друзей пользователей
        String sql = "SELECT U.USER_ID, \n" +
                "       U.USER_LOGIN, \n" +
                "       U.USER_NAME, \n" +
                "       U.USER_EMAIL, \n" +
                "       U.BIRTHDATE \n" +
                " FROM FRIENDS AS F \n" +
                " INNER JOIN FRIENDS AS F2 on F2.FRIEND_ID = F.FRIEND_ID AND F2.USER_ID = " + otherId + "\n" +
                " LEFT OUTER JOIN USERS U on F.FRIEND_ID = U.USER_ID \n" +
                " WHERE F.USER_ID = " + userId;
        log.info("Вывод списка общих друзей пользователя ID = {} и ID = {} из базы данных", userId, otherId);
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUSER(rs));
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {       // метод добавления друзей
        String sql = "INSERT INTO FRIENDS(USER_ID, FRIEND_ID) " +
                "VALUES(?, ?)";
        jdbcTemplate.update(sql,
                userId,
                friendId);
        log.info("Пользователю ID = {} добавлен друг ID = {} ", userId, friendId);
    }

    private void addFriendWhenAddUser(Integer userId, Set<User> friends) {    // метод внесения друзей в таблицу при добавлении пользователя
        for (User friend : friends) {
            String sql = "INSERT INTO FRIENDS(USER_ID, FRIEND_ID) " +
                    "VALUES(?, ?)";
            jdbcTemplate.update(sql,
                    userId,
                    friend.getId());
            log.info("Пользователю ID = {} добавлены друзья ", userId);
        }
    }

    private void addFriendWhenRefreshUser(Integer userId, Set<User> friends) {       // метод внесения друзей в таблицу при обновлении пользователя
        String sql = "DELETE FROM FRIENDS WHERE USER_ID = " + userId;
        jdbcTemplate.update(sql);
        addFriendWhenAddUser(userId, friends);
        log.info("У пользователя ID = {} обновлен список друзей ", userId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {    // метод удаления друзей
        String sql = "DELETE FROM FRIENDS WHERE USER_ID = " + userId +
                " AND FRIEND_ID = " + friendId;
        jdbcTemplate.update(sql);
        log.debug("У пользователя ID = {} удален друг ID = {} ", userId, friendId);
    }

    private Map<Integer, Set<User>> getAllFriends() {          //метод получения друзей пользователя
        String sqlAll = "SELECT * FROM USERS";
        Map<Integer, User> usersMap = jdbcTemplate.query(sqlAll, (rs, rowNum) -> makeUSER(rs)).stream()
                .collect(Collectors.toMap(User::getId,
                        Function.identity()));
        Map<Integer, Set<User>> friendsMap = new HashMap<>();
        String sql = "SELECT *,\n" +
                "FROM FRIENDS AS FG;";
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(sql);
        while (friendsRows.next()) {
            int id = friendsRows.getInt("USER_ID");
            int friendsId = friendsRows.getInt("FRIEND_ID");
            if (friendsMap.containsKey(id)) {
                Set<User> friendList = friendsMap.get(id);
                friendList.add(usersMap.get(friendsId));
                friendsMap.put(id, friendList);
            } else {
                Set<User> friendList = new HashSet<>();
                friendList.add(usersMap.get(friendsId));
                friendsMap.put(id, friendList);
            }
        }
        return friendsMap;
    }

    private User makeUSER(ResultSet usersRows) throws SQLException {        //метод получения пользователя
        User user = User.builder()
                .id(usersRows.getInt("USER_ID"))
                .login(usersRows.getString("USER_LOGIN"))
                .name(usersRows.getString("USER_NAME"))
                .email(usersRows.getString("USER_EMAIL"))
                .birthday(usersRows.getDate("BIRTHDATE").toLocalDate())
                .build();
        return user;
    }


}
