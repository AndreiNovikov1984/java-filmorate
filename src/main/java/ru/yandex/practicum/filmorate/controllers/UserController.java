package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger("UserController");


    @GetMapping
    public List<User> getUsers() {
        List<User> listUsers = new ArrayList<>();
        listUsers.addAll(users.values());
        log.debug("Вывод списка пользователей");
        return listUsers;
    }

    @PostMapping
    public User postUser(@RequestBody User user) {
        if ((user.getEmail() == null) || (user.getEmail().equals("")) || (user.getEmail().equals("null")) ||
                (!user.getEmail().contains("@"))) {
            log.warn("Ошибка в email - {}", user.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail не корректный. Попробуйте еще раз.");
        }
        if ((user.getLogin() == null) || (user.getLogin().contains(" ")) || (user.getLogin().equals("null")) ||
                (user.getLogin().equals(""))) {
            log.warn("Ошибка в логине - {}", user.getLogin());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Логин не может быть пустым или содержать пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения из будущего");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Дата рождения не может быть позже текущей даты.");
        }
        if (user.getId() < 0) {
            log.warn("Некорректный id пользователя в запросе - {}", user.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз.");
        }
        if ((user.getName() == null) || (user.getName().equals("")) || (user.getName().equals("null"))) {
            user.setName(user.getLogin());
        }
        int id = User.getIdentificator();
        if (!users.containsKey(user.getId())) {
            id++;
            user.setId(id);
            User.setIdentificator(id);
        }
        users.put(user.getId(), user);
        log.debug("Пользователь {} добавлен/обновлен", user.getName());
        return user;
    }

    @PutMapping
    public User putUser(@RequestBody User user) {
        return postUser(user);
    }
}
