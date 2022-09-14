package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.support.Validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger("UserController");
    private static int identificator = 0;


    @GetMapping
    public List<User> getUsers() {
        List<User> listUsers = new ArrayList<>();
        listUsers.addAll(users.values());
        log.debug("Вывод списка пользователей");
        return listUsers;
    }

    @PostMapping
    public User postUser(@RequestBody User user) {
        if (Validation.validationUser(user, log)) {
            if (!users.containsKey(user.getId())) {
                identificator++;
                user.setId(identificator);
            }
            users.put(user.getId(), user);
            log.debug("Пользователь {} добавлен", user.getName());
        }
        return user;
    }

    @PutMapping
    public User putUser(@RequestBody User user) {
        if (Validation.validationUser(user, log)) {
            if (!users.containsKey(user.getId())) {
                identificator++;
                user.setId(identificator);
            }
            users.put(user.getId(), user);
            log.debug("Пользователь {} добавлен/обновлен", user.getName());
        }
        return user;
    }
}
