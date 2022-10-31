package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserValidationTest {

    private UserController userController;

    @BeforeEach
    public void initUserController() {
        userController = new UserController();
    }

    @Test
    void verifyValidationForValidEmail() {
        String email = "some@yandex.ru";
        User user = new User(email, "login", "name", LocalDate.now());
        User createdUser = userController.create(user);
        assertEquals(user, createdUser);
    }

    @Test
    void verifyValidationForValidLogin() {
        String login = "l";
        User user = new User("some@yandex.ru", login, "name", LocalDate.now());
        User createdUser = userController.create(user);
        assertEquals(user, createdUser);
    }

    @Test
    void verifyTransformationForEmptyName() {
        String name = "";
        String login = "login";
        User user = new User("some@yandex.ru", login, name, LocalDate.now());
        User createdUser = userController.create(user);
        assertEquals(login, createdUser.getName());
    }

    @Test
    void verifyTransformationForNullName() {
        String name = null;
        String login = "login";
        User user = new User("some@yandex.ru", login, name, LocalDate.now());
        User createdUser = userController.create(user);
        assertEquals(login, createdUser.getName());
    }
}
