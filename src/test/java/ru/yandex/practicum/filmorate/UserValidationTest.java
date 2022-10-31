package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.controller.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void verifyValidationForInvalidEmailEmpty() {
        String email = "";
        User user = new User(email, "login", "name", LocalDate.now());
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void verifyValidationForInvalidEmailWithoutAtSign() {
        String email = "email";
        User user = new User(email, "login", "name", LocalDate.now());
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void verifyValidationForNullEmail() {
        String email = null;
        assertThrows(NullPointerException.class, () -> new User(email, "login", "name", LocalDate.now()));
    }

    @Test
    void verifyValidationForValidLogin() {
        String login = "l";
        User user = new User("some@yandex.ru", login, "name", LocalDate.now());
        User createdUser = userController.create(user);
        assertEquals(user, createdUser);
    }

    @Test
    void verifyValidationForInvalidLoginEmpty() {
        String login = "";
        User user = new User("some@yandex.ru", login, "name", LocalDate.now());
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void verifyValidationForInvalidLoginWithSpace() {
        String login = "login with space";
        User user = new User("some@yandex.ru", login, "name", LocalDate.now());
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void verifyValidationForNullLogin() {
        String login = null;
        assertThrows(NullPointerException.class, () -> new User("some@yandex.ru", login, "name",
                LocalDate.now()));
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

    @Test
    void verifyValidationForInvalidBirthdayInFeature() {
        LocalDate birthday = LocalDate.now().plusDays(1);
        User user = new User("some@yandex.ru", "login", "name", birthday);
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void verifyValidationForNullBirthday() {
        LocalDate birthday = null;
        assertThrows(NullPointerException.class, () -> new User("some@yandex.ru", "login", birthday));
    }
}
