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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserService userService;
    private User user;


    @BeforeEach
    public void beforeEach() {
        user = User.builder()
                .login("Vasilek")
                .name("Vasiliy")
                .email("vasya@vasiliy.ru")
                .birthday(LocalDate.of(1999, 9, 9))
                .build();
    }

    @Test
    @Sql(scripts = {"file:src/test/resources/test.sql"})
    public void getAllUsers() {
        Collection<User> users = userService.getUsers();
        assertNotNull(users);
        assertEquals(3, users.size(), "Ошибка");
    }

    @Test
    public void getUserById() {
        user = userService.getUserById(1);
        assertNotNull(user);
        assertEquals(1, user.getId(), "Данные не получены");
    }

    @Test
    public void getUserByIdIncorrect() {
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            userService.getUserById(0);
        });
        Assertions.assertEquals("404 NOT_FOUND \"Некорректный id пользователя. Попробуйте еще раз.\"", exeption.getMessage());
    }

    @Test
    @Sql(scripts = {"file:src/test/resources/test.sql"})
    public void addUser() {
        userService.postUser(user);
        User test = userService.getUserById(4);
        assertNotNull(user);
        assertEquals(test, user, "Данные не получены");
    }

    @Test
    public void addUserEmailEmpty() {
        user.setEmail("");
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            userService.postUser(user);
        });
        Assertions.assertEquals("400 BAD_REQUEST \"E-mail не корректный. Попробуйте еще раз.\"", exeption.getMessage());
    }

    @Test
    public void addUserEmailWithout() {
        user.setEmail("vasya-vasiliy.ru");
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            userService.postUser(user);
        });
        Assertions.assertEquals("400 BAD_REQUEST \"E-mail не корректный. Попробуйте еще раз.\"", exeption.getMessage());
    }

    @Test
    public void addUserLoginEmpty() {
        user.setLogin("");
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            userService.postUser(user);
        });
        Assertions.assertEquals("400 BAD_REQUEST \"Логин не может быть пустым или содержать пробелы.\"", exeption.getMessage());
    }

    @Test
    public void addUserLoginWithSpace() {
        user.setLogin("Vasil ek");
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            userService.postUser(user);
        });
        Assertions.assertEquals("400 BAD_REQUEST \"Логин не может быть пустым или содержать пробелы.\"", exeption.getMessage());
    }

    @Test
    @Sql(scripts = {"file:src/test/resources/test.sql"})
    public void addUserWithoutName() {
        user.setName("");
        User test = userService.postUser(user);
        assertEquals(test, user, "Данные не получены");
    }

    @Test
    public void addUserBirthdayInFuture() {
        user.setBirthday(LocalDate.of(2099, 9, 9));
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            userService.postUser(user);
        });
        Assertions.assertEquals("400 BAD_REQUEST \"Дата рождения не может быть позже текущей даты.\"", exeption.getMessage());
    }

    @Test
    @Sql(scripts = {"file:src/test/resources/test.sql"})
    public void refreshUser() {
        user = userService.postUser(user);
        User test = userService.putUser(user);
        assertEquals(test, user, "Данные не получены");
    }

    @Test
    public void putUserIdIncorrect() {
        user.setId(-1);
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            userService.putUser(user);
        });
        Assertions.assertEquals("404 NOT_FOUND \"Некорректный id. Попробуйте еще раз.\"", exeption.getMessage());
    }

    @Test
    public void getUsersFriends() {
        Collection<User> users = userService.getUsersFriends(1);
        assertNotNull(users);
        assertEquals(2, users.size(), "Данные не получены");
    }

    @Test
    public void getUsersMutualFriends() {
        Collection<User> users = userService.getUsersMutualFriends(1, 2);
        assertNotNull(users);
        assertEquals(1, users.size(), "Данные не получены");
    }

    @Test
    @Sql(scripts = {"file:src/test/resources/test.sql"})
    public void getUsersNoMutualFriends() {
        userService.postUser(user);
        Collection<User> users = userService.getUsersMutualFriends(1, 4);
        assertEquals(0, users.size(), "Данные не получены");
    }

    @Test
    @Sql(scripts = {"file:src/test/resources/test.sql"})
    public void addFriendUser() {
        userService.postUser(user);
        userService.addFriend(1, 4);
        Collection<User> users = userService.getUsersFriends(1);
        assertNotNull(users);
        assertEquals(3, users.size(), "Данные не получены");
    }

    @Test
    @Sql(scripts = {"file:src/test/resources/test.sql"})
    public void addFriendWhitoutUser() {
        userService.postUser(user);
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            userService.addFriend(5, 4);
        });
        Assertions.assertEquals("404 NOT_FOUND \"Некорректный id пользователя. Попробуйте еще раз.\"", exeption.getMessage());
    }

    @Test
    @Sql(scripts = {"file:src/test/resources/test.sql"})
    public void deleteFriendUser() {
        userService.deleteFriend(1, 2);
        Collection<User> users = userService.getUsersFriends(1);
        assertNotNull(users);
        assertEquals(1, users.size(), "Данные не получены");
    }

    @Test
    @Sql(scripts = {"file:src/test/resources/test.sql"})
    public void deleteFriendUserWhitoutUser() {
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            userService.deleteFriend(4, 2);
        });
        Assertions.assertEquals("404 NOT_FOUND \"Некорректный id пользователя. Попробуйте еще раз.\"", exeption.getMessage());
    }


}