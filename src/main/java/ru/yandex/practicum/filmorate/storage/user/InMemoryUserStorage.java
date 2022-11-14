package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private int id = 1;

    private final Map<Integer, User> users = new HashMap<>();

    public List<User> findAll() {
        return new ArrayList<>(users.values());
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

    public User delete(Integer id) {
        return null;
    }

}
