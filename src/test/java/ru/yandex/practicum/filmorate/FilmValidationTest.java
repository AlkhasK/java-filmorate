package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmValidationTest {

    private FilmController filmController;

    @BeforeEach
    public void initFilmController() {
        filmController = new FilmController();
    }

    @Test
    void verifyValidationForValidName() {
        String name = "n";
        Film film = new Film(name, "description", LocalDate.now(), Duration.ofSeconds(1));
        Film createdFilm = filmController.create(film);
        assertEquals(film, createdFilm);
    }

    @Test
    void verifyValidationForEmptyName() {
        String name = "";
        Film film = new Film(name, "description", LocalDate.now(), Duration.ofSeconds(1));
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void verifyValidationForNullName() {
        String name = null;
        assertThrows(NullPointerException.class, () -> new Film(name, "description", LocalDate.now(),
                Duration.ofSeconds(1)));
    }

    @Test
    void verifyValidationForValidDescriptionWithLength200() {
        String description = "description with length equals 199 description with length equals 199 description with " +
                "length equals 199 description with length equals 199 description with length equals 199 description " +
                "with length e";
        Film film = new Film("name", description, LocalDate.now(), Duration.ofSeconds(1));
        Film createdFilm = filmController.create(film);
        assertEquals(film, createdFilm);
    }

    @Test
    void verifyValidationForInvalidDescriptionWithLength201() {
        String description = "description with length equals 199 description with length equals 199 description with " +
                "length equals 199 description with length equals 199 description with length equals 199 description " +
                "with length eR";
        Film film = new Film("name", description, LocalDate.now(), Duration.ofSeconds(1));
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void verifyValidationForNullDescription() {
        String description = null;
        assertThrows(NullPointerException.class, () -> new Film("name", description, LocalDate.now(),
                Duration.ofSeconds(1)));
    }

    @Test
    void verifyValidationForValidReleaseDate28_12_1895() {
        LocalDate releaseDate = LocalDate.of(1895,12,28);
        Film film = new Film("name", "description", releaseDate, Duration.ofSeconds(1));
        Film createdFilm = filmController.create(film);
        assertEquals(film, createdFilm);
    }

    @Test
    void verifyValidationForInvalidReleaseDate27_12_1895() {
        LocalDate releaseDate = LocalDate.of(1895,12,27);
        Film film = new Film("name", "description", releaseDate, Duration.ofSeconds(1));
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void verifyValidationForNullReleaseDate() {
        LocalDate releaseDate = null;
        assertThrows(NullPointerException.class, () -> new Film("name", "description", releaseDate,
                Duration.ofSeconds(1)));
    }

    @Test
    void verifyValidationForValidDuration0() {
        Duration duration = Duration.ofSeconds(0);
        Film film = new Film("name", "description", LocalDate.now(), duration);
        Film createdFilm = filmController.create(film);
        assertEquals(film, createdFilm);
    }

    @Test
    void verifyValidationForInvalidDurationNegative() {
        Duration duration = Duration.ofSeconds(-1);
        Film film = new Film("name", "description", LocalDate.now(), duration);
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void verifyValidationForNullDuration() {
        Duration duration = null;
        assertThrows(NullPointerException.class, () -> new Film("name", "description", LocalDate.now(),
                duration));
    }
}
