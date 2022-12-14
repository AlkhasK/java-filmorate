package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User create(User user);

    User update(User user);

    void delete(int id);

    List<User> findAll();

    Optional<User> findById(int id);

    List<User> findFriends(User user);

    List<User> commonFriends(User user1, User user2);
}
