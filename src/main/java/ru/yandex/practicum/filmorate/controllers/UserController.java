package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@Component
@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger("UserController");
    private final UserService userService;

    @Autowired
    private UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getUsers() {        // метод получения списка пользователей
        log.debug("Вывод списка пользователей из хранилища");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) { // метод получения пользователя по Id
        log.debug("Вывод пользователя из хранилища по ID");
        return userService.getUserById(id);
    }

    @PostMapping
    public User postUser(@RequestBody User user) {  // метод добавления пользователя
        User postUser = userService.postUser(user);
        log.debug("Пользователь успешно {} добавлен в хранилище", user.getId());
        return postUser;
    }

    @PutMapping
    public User putUser(@RequestBody User user) {   // метод обновления пользователя
        User putUser = userService.putUser(user);
        log.debug("Пользователь успешно {} добавлен/обновлен", user.getId());
        return putUser;
    }

    @GetMapping("/{id}/friends")            // метод получения друзей пользователя
    public Set<User> getUsersFriends(@PathVariable Integer id) {
        log.debug("Информация о друзьях пользователя получена");
        return userService.getUsersFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")       // метод получения совпадающих друзей пользователей
    public Set<User> getUsersMutualFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.debug("Информация о совпадающих друзьях пользователя получена");
        return userService.getUsersMutualFriends(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) { // метод добавления друзей
        userService.addFriend(id, friendId);
        log.debug("Информация о друзьях пользователя добавлена");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {  // метод удаления друзей
        userService.deleteFriend(id, friendId);
        log.debug("Информация о друзьях пользователя удалена");
    }
}
