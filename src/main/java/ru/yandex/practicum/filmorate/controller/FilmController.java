package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.controller.exception.ValidationException;
import ru.yandex.practicum.filmorate.controller.validation.FilmValidationUtils;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private int id = 1;

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.info("Update film : {}", film);
        validateForUpdate(film);
        films.put(film.getId(), film);
        return film;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Create film : {}", film);
        validate(film);
        film.setId(id++);
        films.put(film.getId(), film);
        return film;
    }

    private void validate(Film film) {
        if (!FilmValidationUtils.isNameValid(film.getName())) {
            log.warn("Invalid field name: {}", film.getName());
            throw new ValidationException("Object Film has invalid name: " + film.getName());
        }
        if (!FilmValidationUtils.isDescriptionValid(film.getDescription())) {
            log.warn("Invalid field description: {}", film.getDescription());
            throw new ValidationException("Object Film has invalid description: " + film.getDescription());
        }
        if (!FilmValidationUtils.isReleaseDateValid(film.getReleaseDate())) {
            log.warn("Invalid field release date: {}", film.getReleaseDate());
            throw new ValidationException("Object Film has invalid release date: " + film.getReleaseDate());
        }
        if (!FilmValidationUtils.isDurationValid(film.getDuration())) {
            log.warn("Invalid field duration: {}", film.getDuration());
            throw new ValidationException("Object Film has invalid release duration: " + film.getDuration());
        }
    }

    private boolean isEntityExists(Film film) {
        return films.containsKey(film.getId());
    }

    private void validateForUpdate(Film film) {
        if (!isEntityExists(film)) {
            log.warn("Film with id: {} doesn't exists", film.getId());
            throw new EntityNotFoundException("No film entity with id: " + film.getId());
        }
        validate(film);
    }
}
