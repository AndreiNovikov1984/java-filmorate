package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger("UserService");
    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;

    public Collection<User> getUsers() {    // метод получения списка пользователей
        log.debug("Вывод списка пользователей из хранилища");
        return userStorage.getUsers();
    }

    public User getUserById(int id) {       // метод получения пользователя по Id
        log.debug("Вывод пользователя ID = {} из хранилища по ID", id);
        return userStorage.getUserById(id);
    }

    public User postUser(User user) {       // метод добавления пользователя
        User postUser = userStorage.addUser(user);
        log.debug("Пользователь ID = {} успешно добавлен в хранилище", user.getId());
        return postUser;
    }

    public User putUser(User user) {        // метод обновления пользователя
        User putUser = userStorage.refreshUser(user);
        log.debug("Пользователь ID = {} успешно  обновлен/добавлен в хранилище", user.getId());
        return putUser;
    }

    public Set<User> getUsersFriends(Integer userId) {      // метод получения друзей пользователя
        validationId(userId);
        Set<User> friends = new HashSet<>();
        for (int num : userStorage.getUserById(userId).getFriends()) {
            friends.add(userStorage.getUserById(num));
        }
        log.debug("Информация о друзьях пользователя ID = {} успешно передана", userId);
        return friends;
    }

    public Set<User> getUsersMutualFriends(Integer userId, Integer otherId) {   // метод получения совпадающих друзей пользователей
        validationId(userId, otherId);
        Set<Integer> mutual = new HashSet<>();
        Set<User> mutualFriends = new HashSet<>();
        mutual.addAll(userStorage.getUserById(userId).getFriends());
        mutual.retainAll(userStorage.getUserById(otherId).getFriends());
        for (int num : mutual) {
            mutualFriends.add(userStorage.getUserById(num));
        }
        log.debug("Информация о совпадающих друзьях пользователя ID = {} и ID = {} передана", userId, otherId);
        return mutualFriends;
    }

    public void addFriend(Integer userId, Integer friendId) {       // метод добавления друзей
        validationId(userId, friendId);
        userStorage.getUserById(userId).getFriends().add(friendId);
        userStorage.getUserById(friendId).getFriends().add(userId);
        log.debug("Информация о друзьях пользователя ID = {} успешно добавлена", userId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {    // метод удаления друзей
        validationId(userId, friendId);
        userStorage.getUserById(userId).getFriends().remove(friendId);
        userStorage.getUserById(friendId).getFriends().remove(userId);
        log.debug("Информация о друзьях пользователя ID = {} удалена", userId);
    }

    private void validationId(Integer id) {         //метод валидации id пользователя
        if (userStorage.getUserById(id) == null) {
            log.warn("Передан некорректный ID {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз.");
        }
        log.debug("Проверка пользователя ID = {} пройдена", userStorage.getUserById(id).getId());
    }

    private void validationId(Integer UserId, Integer friendId) {   //метод валидации id пользователя и друга
        if (userStorage.getUserById(UserId) == null) {
            log.warn("Передан некорректный ID {}", UserId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз.");
        }
        if (userStorage.getUserById(friendId) == null) {
            log.warn("Передан некорректный ID {}", friendId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз.");
        }
        log.debug("Проверка пользователя ID = {} и друга ID = {} пройдена", UserId, friendId);
    }

}
