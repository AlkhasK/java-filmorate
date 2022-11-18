package ru.yandex.practicum.filmorate.service.validate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Slf4j
@Component
public class ValidateUserService {

    private final UserStorage userStorage;

    @Autowired
    public ValidateUserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void validateForUpdate(User user) {
        if (!isEntityExists(user)) {
            throw new EntityNotFoundException("No user entity with id: " + user.getId());
        }
    }

    public boolean isEntityExists(User user) {
        return userStorage.getById(user.getId()) != null;
    }

    public boolean isEntityExists(int userId) {
        return userStorage.getById(userId) != null;
    }
}
