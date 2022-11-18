package ru.yandex.practicum.filmorate.service.validate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.controller.exception.ValidationException;
import ru.yandex.practicum.filmorate.controller.validation.FilmValidationUtils;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

@Component
public class ValidateFilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public ValidateFilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void validate(Film film) {
        if (!FilmValidationUtils.isReleaseDateValid(film.getReleaseDate())) {
            throw new ValidationException("Object Film has invalid release date: " + film.getReleaseDate());
        }
        if (!FilmValidationUtils.isDurationValid(film.getDuration())) {
            throw new ValidationException("Object Film has invalid release duration: " + film.getDuration());
        }
    }

    public void validateForUpdate(Film film) {
        if (!isEntityExists(film)) {
            throw new EntityNotFoundException("No film entity with id: " + film.getId());
        }
        validate(film);
    }

    public boolean isEntityExists(Film film) {
        return filmStorage.getById(film.getId()) != null;
    }

    public boolean isEntityExists(int filmId) {
        return filmStorage.getById(filmId) != null;
    }
}
