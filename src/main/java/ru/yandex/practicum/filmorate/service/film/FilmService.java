package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.controller.exception.ValidationException;
import ru.yandex.practicum.filmorate.controller.validation.FilmValidationUtils;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film update(Film film) {
        log.info("Update film : {}", film);
        validateForUpdate(film);
        return filmStorage.update(film);
    }

    public Film create(Film film) {
        log.info("Create film : {}", film);
        validate(film);
        return filmStorage.create(film);
    }

    private void validate(Film film) {
        if (!FilmValidationUtils.isReleaseDateValid(film.getReleaseDate())) {
            log.warn("Invalid field release date: {}", film.getReleaseDate());
            throw new ValidationException("Object Film has invalid release date: " + film.getReleaseDate());
        }
        if (!FilmValidationUtils.isDurationValid(film.getDuration())) {
            log.warn("Invalid field duration: {}", film.getDuration());
            throw new ValidationException("Object Film has invalid release duration: " + film.getDuration());
        }
    }

    private void validateForUpdate(Film film) {
        if (!isEntityExists(film)) {
            log.warn("Film with id: {} doesn't exists", film.getId());
            throw new EntityNotFoundException("No film entity with id: " + film.getId());
        }
        validate(film);
    }

    private boolean isEntityExists(Film film) {
        return filmStorage.findAll().stream()
                .anyMatch(f -> f.getId().equals(film.getId()));
    }
}
