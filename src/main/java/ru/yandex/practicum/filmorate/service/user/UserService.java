package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
       return userStorage.findAll();
    }

    public User update(User user) {
        log.info("Update film : {}", user);
        validateForUpdate(user);
        setNameIfEmpty(user);
        return userStorage.update(user);
    }

    public User create(User user) {
        log.info("Create film : {}", user);
        setNameIfEmpty(user);
        return userStorage.create(user);
    }

    private void validateForUpdate(User user) {
        if (!isEntityExists(user)) {
            log.warn("User with id: {} doesn't exists", user.getId());
            throw new EntityNotFoundException("No user entity with id: " + user.getId());
        }
    }

    private boolean isEntityExists(User user) {
        return userStorage.findAll().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));
    }

    private void setNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            String login = user.getLogin();
            user.setName(login);
        }
    }
}
