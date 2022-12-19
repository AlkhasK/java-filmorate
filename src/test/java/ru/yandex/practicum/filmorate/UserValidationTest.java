package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.service.validate.ValidateUserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.friend.InMemoryFriendStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserValidationTest {

    private UserController userController;

    private Validator validator;

    @BeforeEach
    public void initUserController() {
        var inMemoryUserStorage = new InMemoryUserStorage();
        var validateUserService = new ValidateUserService(inMemoryUserStorage);
        var friendStorage = new InMemoryFriendStorage();
        var userService = new UserService(inMemoryUserStorage, validateUserService, friendStorage);
        userController = new UserController(userService);
    }

    @BeforeEach
    public void initValidator() {
        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void verifyValidationForValidEmail() {
        String email = "some@yandex.ru";

        User user = new User();
        user.setEmail(email);
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> validations = validator.validate(user);
        assertEquals(0, validations.size());
    }

    @Test
    void verifyValidationForInvalidEmailEmpty() {
        String email = "";

        User user = new User();
        user.setEmail(email);
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> validations = validator.validate(user);
        assertEquals(1, validations.size());

        ConstraintViolation<User> violation = validations.stream().findFirst().orElse(null);
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    void verifyValidationForInvalidEmailWithoutAtSign() {
        String email = "email";

        User user = new User();
        user.setEmail(email);
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> validations = validator.validate(user);
        assertEquals(1, validations.size());

        ConstraintViolation<User> violation = validations.stream().findFirst().orElse(null);
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    void verifyValidationForNullEmail() {
        String email = null;

        User user = new User();
        user.setEmail(email);
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> validations = validator.validate(user);
        assertEquals(1, validations.size());

        ConstraintViolation<User> violation = validations.stream().findFirst().orElse(null);
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    void verifyValidationForValidLogin() {
        String login = "l";

        User user = new User();
        user.setEmail("some@yandex.ru");
        user.setLogin(login);
        user.setName("name");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> validations = validator.validate(user);
        assertEquals(0, validations.size());
    }

    @Test
    void verifyValidationForInvalidLoginEmpty() {
        String login = "";

        User user = new User();
        user.setEmail("some@yandex.ru");
        user.setLogin(login);
        user.setName("name");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> validations = validator.validate(user);
        assertEquals(1, validations.size());

        ConstraintViolation<User> violation = validations.stream().findFirst().orElse(null);
        assertEquals("login", violation.getPropertyPath().toString());
    }

    @Test
    void verifyValidationForInvalidLoginWithSpace() {
        String login = "login with space";

        User user = new User();
        user.setEmail("some@yandex.ru");
        user.setLogin(login);
        user.setName("name");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> validations = validator.validate(user);
        assertEquals(1, validations.size());

        ConstraintViolation<User> violation = validations.stream().findFirst().orElse(null);
        assertEquals("login", violation.getPropertyPath().toString());
    }

    @Test
    void verifyValidationForNullLogin() {
        String login = null;

        User user = new User();
        user.setEmail("some@yandex.ru");
        user.setLogin(login);
        user.setName("name");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> validations = validator.validate(user);
        assertEquals(1, validations.size());

        ConstraintViolation<User> violation = validations.stream().findFirst().orElse(null);
        assertEquals("login", violation.getPropertyPath().toString());
    }

    @Test
    void verifyTransformationForEmptyName() {
        String name = "";
        String login = "login";

        User user = new User();
        user.setEmail("some@yandex.ru");
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(LocalDate.now());

        User createdUser = userController.create(user);
        assertEquals(login, createdUser.getName());
    }

    @Test
    void verifyTransformationForNullName() {
        String name = null;
        String login = "login";

        User user = new User();
        user.setEmail("some@yandex.ru");
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(LocalDate.now());

        User createdUser = userController.create(user);
        assertEquals(login, createdUser.getName());
    }

    @Test
    void verifyValidationForInvalidBirthdayInFeature() {
        LocalDate birthday = LocalDate.now().plusDays(1);

        User user = new User();
        user.setEmail("some@yandex.ru");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(birthday);

        Set<ConstraintViolation<User>> validations = validator.validate(user);
        assertEquals(1, validations.size());

        ConstraintViolation<User> violation = validations.stream().findFirst().orElse(null);
        assertEquals("birthday", violation.getPropertyPath().toString());
    }

    @Test
    void verifyValidationForNullBirthday() {
        LocalDate birthday = null;

        User user = new User();
        user.setEmail("some@yandex.ru");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(birthday);

        Set<ConstraintViolation<User>> validations = validator.validate(user);
        assertEquals(1, validations.size());

        ConstraintViolation<User> violation = validations.stream().findFirst().orElse(null);
        assertEquals("birthday", violation.getPropertyPath().toString());
    }
}
