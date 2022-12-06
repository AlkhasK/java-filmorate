package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private int id = 1;

    private final Map<Integer, User> users = new HashMap<>();

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.empty();
    }

    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public User create(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    public void delete(int id) {
    }

    @Override
    public List<User> findFriends(User user) {
        return null;
    }

    @Override
    public List<User> commonFriends(User user1, User user2) {
        return null;
    }
}
