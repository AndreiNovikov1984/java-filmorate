package ru.yandex.practicum.filmorate.storage;

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

    @Override
    public Collection<User> getUsers() {    // метод получения списка пользователей
        return users.values();
    }

    @Override
    public User getUserById(int id) {   // метод получения пользователя по Id
        return users.values().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id. Попробуйте еще раз."));
    }

    @Override
    public User addUser(User user) {        // метод добавления пользователя
        if (Validation.validationUser(user)) {
            if (!users.containsKey(user.getId())) {
                identificator++;
                user.setId(identificator);
            }
            users.put(user.getId(), user);
        }
        return user;
    }

    @Override
    public User refreshUser(User user) {        // метод обновления пользователя
        if (Validation.validationUser(user)) {
            if (!users.containsKey(user.getId())) {
                identificator++;
                user.setId(identificator);
            }
            users.put(user.getId(), user);
        }
        return user;
    }

    @Override
    public Set<User> getUsersFriends(Integer userId) {      // метод получения друзей пользователя
        Set<User> friends = new HashSet<>();
        for (User user : getUserById(userId).getFriends()) {
            friends.add(user);
        }
        return friends;
    }

    @Override
    public Set<User> getUsersMutualFriends(Integer userId, Integer otherId) {   // метод получения совпадающих друзей пользователей
        Set<User> mutual = new HashSet<>();
        Set<User> mutualFriends = new HashSet<>();
        mutual.addAll(getUserById(userId).getFriends());
        mutual.retainAll(getUserById(otherId).getFriends());
        for (User user : mutual) {
            mutualFriends.add(user);
        }
        return mutualFriends;
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {       // метод добавления друзей
        getUserById(userId).getFriends().add(getUserById(friendId));
        getUserById(friendId).getFriends().add(getUserById(userId));
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {    // метод удаления друзей
        getUserById(userId).getFriends().remove(friendId);
        getUserById(friendId).getFriends().remove(userId);
    }
}
