package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> getUsers();

    User getUserById(int id);

    User addUser(User user);

    User refreshUser(User user);

    Collection<User> getUsersFriends(Integer userId);

    Collection<User> getUsersMutualFriends(Integer userId, Integer otherId);

    void addFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);

}

