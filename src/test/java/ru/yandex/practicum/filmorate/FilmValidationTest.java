package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmValidationTest {

    private FilmController filmController;
    private Validator validator;

    @BeforeEach
    public void initFilmController() {
        filmController = new FilmController();
    }

    @BeforeEach
    public void initValidator() {
        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void verifyValidationForValidName() {
        String name = "n";

        Film film = new Film();
        film.setName(name);
        film.setDescription("description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(Duration.ofSeconds(1));

        Set<ConstraintViolation<Film>> validations = validator.validate(film);
        assertEquals(0, validations.size());
    }

    @Test
    void verifyValidationForEmptyName() {
        String name = "";

        Film film = new Film();
        film.setName(name);
        film.setDescription("description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(Duration.ofSeconds(1));

        Set<ConstraintViolation<Film>> validations = validator.validate(film);
        assertEquals(1, validations.size());

        ConstraintViolation<Film> violation = validations.stream().findFirst().orElse(null);
        assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    void verifyValidationForNullName() {
        String name = null;

        Film film = new Film();
        film.setName(name);
        film.setDescription("description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(Duration.ofSeconds(1));

        Set<ConstraintViolation<Film>> validations = validator.validate(film);
        assertEquals(1, validations.size());

        ConstraintViolation<Film> violation = validations.stream().findFirst().orElse(null);
        assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    void verifyValidationForValidDescriptionWithLength200() {
        String description = "description with length equals 200 description with length equals 200 description with " +
                "length equals 200 description with length equals 200 description with length equals 200 description " +
                "with length e";

        Film film = new Film();
        film.setName("name");
        film.setDescription(description);
        film.setReleaseDate(LocalDate.now());
        film.setDuration(Duration.ofSeconds(1));

        Set<ConstraintViolation<Film>> validations = validator.validate(film);
        assertEquals(0, validations.size());
    }

    @Test
    void verifyValidationForInvalidDescriptionWithLength201() {
        String description = "description with length equals 201 description with length equals 201 description with " +
                "length equals 201 description with length equals 201 description with length equals 201 description " +
                "with length eR";


        Film film = new Film();
        film.setName("name");
        film.setDescription(description);
        film.setReleaseDate(LocalDate.now());
        film.setDuration(Duration.ofSeconds(1));

        Set<ConstraintViolation<Film>> validations = validator.validate(film);
        assertEquals(1, validations.size());

        ConstraintViolation<Film> violation = validations.stream().findFirst().orElse(null);
        assertEquals("description", violation.getPropertyPath().toString());
    }

    @Test
    void verifyValidationForNullDescription() {
        String description = null;

        Film film = new Film();
        film.setName("name");
        film.setDescription(description);
        film.setReleaseDate(LocalDate.now());
        film.setDuration(Duration.ofSeconds(1));

        Set<ConstraintViolation<Film>> validations = validator.validate(film);
        assertEquals(1, validations.size());

        ConstraintViolation<Film> violation = validations.stream().findFirst().orElse(null);
        assertEquals("description", violation.getPropertyPath().toString());
    }

    @Test
    void verifyValidationForValidReleaseDate28_12_1895() {
        LocalDate releaseDate = LocalDate.of(1895, 12, 28);

        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(releaseDate);
        film.setDuration(Duration.ofSeconds(1));

        Film createdFilm = filmController.create(film);
        assertEquals(film, createdFilm);
    }

    @Test
    void verifyValidationForInvalidReleaseDate27_12_1895() {
        LocalDate releaseDate = LocalDate.of(1895, 12, 27);

        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(releaseDate);
        film.setDuration(Duration.ofSeconds(1));

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void verifyValidationForNullReleaseDate() {
        LocalDate releaseDate = null;

        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(releaseDate);
        film.setDuration(Duration.ofSeconds(1));

        Set<ConstraintViolation<Film>> validations = validator.validate(film);
        assertEquals(1, validations.size());

        ConstraintViolation<Film> violation = validations.stream().findFirst().orElse(null);
        assertEquals("releaseDate", violation.getPropertyPath().toString());
    }

    @Test
    void verifyValidationForValidDuration0() {
        Duration duration = Duration.ofSeconds(0);

        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(duration);

        Film createdFilm = filmController.create(film);
        assertEquals(film, createdFilm);
    }

    @Test
    void verifyValidationForInvalidDurationNegative() {
        Duration duration = Duration.ofSeconds(-1);

        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(duration);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void verifyValidationForNullDuration() {
        Duration duration = null;

        Film film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(duration);

        Set<ConstraintViolation<Film>> validations = validator.validate(film);
        assertEquals(1, validations.size());

        ConstraintViolation<Film> violation = validations.stream().findFirst().orElse(null);
        assertEquals("duration", violation.getPropertyPath().toString());
    }
}
