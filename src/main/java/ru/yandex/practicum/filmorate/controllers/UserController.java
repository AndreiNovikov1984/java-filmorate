package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@Component
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> getUsers() {        // метод получения списка пользователей
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) { // метод получения пользователя по Id
        return userService.getUserById(id);
    }

    @PostMapping
    public User postUser(@RequestBody User user) {  // метод добавления пользователя
        User postUser = userService.postUser(user);
        return postUser;
    }

    @PutMapping
    public User putUser(@RequestBody User user) {   // метод обновления пользователя
        User putUser = userService.putUser(user);
        return putUser;
    }

    @GetMapping("/{id}/friends")            // метод получения друзей пользователя
    public Collection<User> getUsersFriends(@PathVariable Integer id) {
        return userService.getUsersFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")       // метод получения совпадающих друзей пользователей
    public Collection<User> getUsersMutualFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getUsersMutualFriends(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")             // метод добавления друзей
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")          // метод удаления друзей
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.deleteFriend(id, friendId);
    }
}
