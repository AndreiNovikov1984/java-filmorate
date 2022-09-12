package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class UserControllerTest {
    private UserController userController;
    User user;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
    }

    @Test
    public void getAllUsers() {
        user = new User("Vasilek", "vasya@vasiliy.ru", LocalDate.of(1999, 9, 9));
        user.setName("Vasiliy");
        User test = userController.postUser(user);
        List<User> listUsers = userController.getUsers();
        assertEquals(test, listUsers.get(0), "Данные не получены");
        assertEquals(1, listUsers.size(), "Данные не получены");
    }

    @Test
    public void postUser() {
        user = new User("Vasilek", "vasya@vasiliy.ru", LocalDate.of(1999, 9, 9));
        user.setName("Vasiliy");
        user.setId(1);
        User test = userController.postUser(user);
        assertEquals(test, user, "Данные не получены");
    }

    @Test
    public void postUserEmailEmpty() {
        user = new User("Vasilek", "", LocalDate.of(1999, 9, 9));
        user.setName("Vasiliy");
        user.setId(1);
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            User test = userController.postUser(user);
        });
        Assertions.assertEquals("400 BAD_REQUEST \"E-mail не корректный. Попробуйте еще раз.\"", exeption.getMessage());
    }

    @Test
    public void postUserEmailWithout() {
        user = new User("Vasilek", "vasya-vasiliy.ru", LocalDate.of(1999, 9, 9));
        user.setName("Vasiliy");
        user.setId(1);
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            User test = userController.postUser(user);
        });
        Assertions.assertEquals("400 BAD_REQUEST \"E-mail не корректный. Попробуйте еще раз.\"", exeption.getMessage());
    }

    @Test
    public void postUserLoginEmpty() {
        user = new User("", "vasya@vasiliy.ru", LocalDate.of(1999, 9, 9));
        user.setName("Vasiliy");
        user.setId(1);
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            User test = userController.postUser(user);
        });
        Assertions.assertEquals("400 BAD_REQUEST \"Логин не может быть пустым или содержать пробелы.\"", exeption.getMessage());
    }

    @Test
    public void postUserLoginWithSpace() {
        user = new User("Vasil ek", "vasya@vasiliy.ru", LocalDate.of(1999, 9, 9));
        user.setName("Vasiliy");
        user.setId(1);
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            User test = userController.postUser(user);
        });
        Assertions.assertEquals("400 BAD_REQUEST \"Логин не может быть пустым или содержать пробелы.\"", exeption.getMessage());
    }

    @Test
    public void postUserWithoutName() {
        user = new User("Vasilek", "vasya@vasiliy.ru", LocalDate.of(1999, 9, 9));
        user.setId(1);
        User test = userController.postUser(user);
        user.setName("Vasilek");
        assertEquals(test, user, "Данные не получены");
    }

    @Test
    public void postUserBirthdayInFuture() {
        user = new User("Vasilek", "vasya@vasiliy.ru", LocalDate.of(2099, 9, 9));
        user.setName("Vasiliy");
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            User test = userController.postUser(user);
        });
        Assertions.assertEquals("400 BAD_REQUEST \"Дата рождения не может быть позже текущей даты.\"", exeption.getMessage());
    }

    @Test
    public void putUser() {
        user = new User("Vasilek", "vasya@vasiliy.ru", LocalDate.of(1999, 9, 9));
        user.setName("Vasiliy");
        user.setId(1);
        User test = userController.putUser(user);
        assertEquals(test, user, "Данные не получены");
    }

    @Test
    public void putUserIdIncorrect() {
        user = new User("Vasilek", "vasya@vasiliy.ru", LocalDate.of(1999, 9, 9));
        user.setName("Vasiliy");
        user.setId(-1);
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            User test = userController.putUser(user);
        });
        Assertions.assertEquals("404 NOT_FOUND \"Некорректный id. Попробуйте еще раз.\"", exeption.getMessage());
    }
}