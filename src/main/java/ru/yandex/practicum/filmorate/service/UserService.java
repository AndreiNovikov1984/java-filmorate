package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger("UserService");
    private final UserStorage userStorage;

    private UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getUsers() {    // метод получения списка пользователей
        log.debug("Вывод списка пользователей из хранилища");
        return userStorage.getUsers();
    }

    public User getUserById(int id) {       // метод получения пользователя по Id
        log.debug("Вывод пользователя из хранилища по ID");
        return userStorage.getUserById(id);
    }

    public User postUser(User user) {       // метод добавления пользователя
        User postUser = userStorage.addUser(user);
        log.debug("Пользователь успешно {} добавлен в хранилище", user.getId());
        return postUser;
    }

    public User putUser(User user) {        // метод обновления пользователя
        User putUser = userStorage.refreshUser(user);
        log.debug("Пользователь успешно {} добавлен/обновлен в хранилище", user.getId());
        return putUser;
    }

    public Set<User> getUsersFriends(Integer userId) {      // метод получения друзей пользователя
        validationId(userId);
        Set<User> friends = new HashSet<>();
        for (int num : userStorage.getUserById(userId).getFriends()) {
            friends.add(userStorage.getUserById(num));
        }
        log.debug("Информация о друзьях пользователя {} передана", userStorage.getUserById(userId).getId());
        return friends;
    }

    public Set<User> getUsersMutualFriends(Integer userId, Integer otherId) {   // метод получения совпадающих друзей пользователей
        validationId(userId);
        validationId(otherId);
        Set<Integer> mutual = new HashSet<>();
        Set<User> mutualFriends = new HashSet<>();
        mutual.addAll(userStorage.getUserById(userId).getFriends());
        mutual.retainAll(userStorage.getUserById(otherId).getFriends());
        for (int num : mutual) {
            mutualFriends.add(userStorage.getUserById(num));
        }
        log.debug("Информация о друзьях пользователя {} передана", userStorage.getUserById(userId).getId());
        return mutualFriends;
    }

    public void addFriend(Integer userId, Integer friendId) {       // метод добавления друзей
        validationId(userId);
        validationId(friendId);
        userStorage.getUserById(userId).getFriends().add(friendId);
        userStorage.getUserById(friendId).getFriends().add(userId);
        log.debug("Информация о друзьях пользователя {} внесена", userStorage.getUserById(userId).getId());
    }

    public void deleteFriend(Integer userId, Integer friendId) {    // метод удаления друзей
        validationId(userId);
        validationId(friendId);
        userStorage.getUserById(userId).getFriends().remove(friendId);
        userStorage.getUserById(friendId).getFriends().remove(userId);
        log.debug("Информация о друзьях пользователя {} удалена", userStorage.getUserById(userId).getId());
    }

    private void validationId(Integer id) {         //метод валидации id пользователя
        if (userStorage.getUserById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз.");
        }
        log.debug("Проверка пользователя {} пройдена", userStorage.getUserById(id).getId());
    }

    private void validationId(Integer UserId, Integer friendId) {   //метод валидации id пользователя и друга
        if ((userStorage.getUserById(UserId) == null) || (userStorage.getUserById(friendId) == null)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз.");
        }
        log.debug("Проверка пользователя {} и друга {} пройдена", userStorage.getUserById(UserId).getId(),
                userStorage.getUserById(friendId).getId());
    }

}
