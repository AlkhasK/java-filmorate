package ru.yandex.practicum.filmorate.controller.validation;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class UserValidationUtils {

    private static final String LOGIN_REGEX = "^[\\S]*$";

    private UserValidationUtils() {

    }

    public static boolean isEmailValid(String email) {
        return !email.isBlank() && email.contains("@");
    }

    public static boolean isLoginValid(String login) {
        Pattern pattern = Pattern.compile(LOGIN_REGEX);
        return !login.isBlank() && pattern.matcher(login).matches();
    }

    public static boolean isBirthdayValid(LocalDate birthday) {
        return !birthday.isAfter(LocalDate.now());
    }
}
