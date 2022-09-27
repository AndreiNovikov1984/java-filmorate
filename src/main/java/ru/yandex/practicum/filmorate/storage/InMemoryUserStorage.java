package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.support.Validation;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Integer, User> users = new HashMap<>();
    private static int identificator = 0;
    private static final Logger log = LoggerFactory.getLogger("InMemoryUserStorage");

    @Override
    public Collection<User> getUsers() {    // метод получения списка пользователей
        log.debug("Получение списка пользователей");
        return users.values();
    }

    @Override
    public User getUserById(int id) {   // метод получения пользователя по Id
        log.debug("Получение пользователя по id");
        return users.values().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз."));
    }

    @Override
    public User addUser(User user) {        // метод добавления пользователя
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

    @Override
    public User refreshUser(User user) {        // метод обновления пользователя
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
