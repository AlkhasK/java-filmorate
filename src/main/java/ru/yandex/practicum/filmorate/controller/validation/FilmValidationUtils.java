package ru.yandex.practicum.filmorate.controller.validation;

import java.time.Duration;
import java.time.LocalDate;

public class FilmValidationUtils {

    private static final int MAX_DESCRIPTION_LENGTH = 200;

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private FilmValidationUtils() {

    }

    public static boolean isNameValid(String name) {
        return !name.isBlank();
    }

    public static boolean isDescriptionValid(String description) {
        return description.length() <= MAX_DESCRIPTION_LENGTH;
    }

    public static boolean isReleaseDateValid(LocalDate releaseDate) {
        return releaseDate.isEqual(MIN_RELEASE_DATE) || releaseDate.isAfter(MIN_RELEASE_DATE);
    }

    public static boolean isDurationValid(Duration duration) {
        return !duration.isNegative();
    }
}
