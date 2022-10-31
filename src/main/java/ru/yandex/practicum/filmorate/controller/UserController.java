package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.controller.exception.ValidationException;
import ru.yandex.practicum.filmorate.controller.validation.UserValidationUtils;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private int id = 1;

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.info("Update film : {}", user);
        validateForUpdate(user);
        setNameIfEmpty(user);
        users.put(user.getId(), user);
        return user;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Create film : {}", user);
        validate(user);
        setNameIfEmpty(user);
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    private void validate(User user) {
        if (!UserValidationUtils.isEmailValid(user.getEmail())) {
            log.warn("Invalid field email: {}", user.getEmail());
            throw new ValidationException("Object User has invalid email: " + user.getEmail());
        }
        if (!UserValidationUtils.isLoginValid(user.getLogin())) {
            log.warn("Invalid field login: {}", user.getLogin());
            throw new ValidationException("Object User has invalid login: " + user.getLogin());
        }
        if (!UserValidationUtils.isBirthdayValid(user.getBirthday())) {
            log.warn("Invalid field birthday: {}", user.getBirthday());
            throw new ValidationException("Object User has invalid birthday: " + user.getBirthday());
        }
    }

    private boolean isEntityExists(User user) {
        return users.containsKey(user.getId());
    }

    private void validateForUpdate(User user) {
        if (!isEntityExists(user)) {
            log.warn("User with id: {} doesn't exists", user.getId());
            throw new EntityNotFoundException("No user entity with id: " + user.getId());
        }
        validate(user);
    }

    private void setNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            String login = user.getLogin();
            user.setName(login);
        }
    }
}
