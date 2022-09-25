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
    private User user;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
        user = User.builder()
                .login("Vasilek")
                .name("Vasiliy")
                .email("vasya@vasiliy.ru")
                .birthday(LocalDate.of(1999, 9, 9))
                .build();
    }


    @Test
    public void getAllUsers() {
        User test = userController.postUser(user);
        List<User> listUsers = userController.getUsers();
        assertEquals(test, listUsers.get(0), "Данные не получены");
        assertEquals(1, listUsers.size(), "Данные не получены");
    }

    @Test
    public void postUser() {
        User test = userController.postUser(user);
        assertEquals(test, user, "Данные не получены");
    }

    @Test
    public void postUserEmailEmpty() {
        user.setEmail("");
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            User test = userController.postUser(user);
        });
        Assertions.assertEquals("400 BAD_REQUEST \"E-mail не корректный. Попробуйте еще раз.\"", exeption.getMessage());
    }

    @Test
    public void postUserEmailWithout() {
        user.setEmail("vasya-vasiliy.ru");
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            User test = userController.postUser(user);
        });
        Assertions.assertEquals("400 BAD_REQUEST \"E-mail не корректный. Попробуйте еще раз.\"", exeption.getMessage());
    }

    @Test
    public void postUserLoginEmpty() {
        user.setLogin("");
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            User test = userController.postUser(user);
        });
        Assertions.assertEquals("400 BAD_REQUEST \"Логин не может быть пустым или содержать пробелы.\"", exeption.getMessage());
    }

    @Test
    public void postUserLoginWithSpace() {
        user.setLogin("Vasil ek");
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            User test = userController.postUser(user);
        });
        Assertions.assertEquals("400 BAD_REQUEST \"Логин не может быть пустым или содержать пробелы.\"", exeption.getMessage());
    }

    @Test
    public void postUserWithoutName() {
        user.setName("");
        User test = userController.postUser(user);
        assertEquals(test, user, "Данные не получены");
    }

    @Test
    public void postUserBirthdayInFuture() {
        user.setBirthday(LocalDate.of(2099, 9, 9));
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            User test = userController.postUser(user);
        });
        Assertions.assertEquals("400 BAD_REQUEST \"Дата рождения не может быть позже текущей даты.\"", exeption.getMessage());
    }

    @Test
    public void putUser() {
        userController.putUser(user);
        user.setId(1);
        User test = userController.putUser(user);
        assertEquals(test, user, "Данные не получены");
    }

    @Test
    public void putUserIdIncorrect() {
        user.setId(-1);
        ResponseStatusException exeption = Assertions.assertThrows(ResponseStatusException.class, () -> {
            User test = userController.putUser(user);
        });
        Assertions.assertEquals("404 NOT_FOUND \"Некорректный id. Попробуйте еще раз.\"", exeption.getMessage());
    }
}