package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.controller.exception.ValidationException;
import ru.yandex.practicum.filmorate.controller.validation.FilmValidationUtils;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;

    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
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
        return filmStorage.getById(film.getId()) != null;
    }

    private void initLikes(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
    }

    private void addLike(Film film, User user) {
        Integer userId = user.getId();
        initLikes(film);
        film.getLikes().add(userId);
        filmStorage.update(film);
    }

    public void addLike(int filmId, int userId) {
        log.info("Add like user id: {} to film id: {}", userId, filmId);
        Film film = getFilm(filmId);
        User user = userService.getUser(userId);
        addLike(film, user);
    }

    public Film getFilm(int filmId) {
        log.info("Get film by id: {}", filmId);
        Film film = filmStorage.getById(filmId);
        if (film == null) {
            log.warn("Film id: {} doesn't exists", filmId);
            throw new EntityNotFoundException("No film entity with id: " + filmId);
        }
        return film;
    }

    private void deleteLike(Film film, User user) {
        Integer userId = user.getId();
        initLikes(film);
        film.getLikes().remove(userId);
        filmStorage.update(film);
    }

    public void deleteLike(int filmId, int userId) {
        log.info("Remove like user id: {} from film id : {}", userId, filmId);
        Film film = getFilm(filmId);
        User user = userService.getUser(userId);
        deleteLike(film, user);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> films = filmStorage.findAll();
        return films.stream()
                .sorted(Comparator.comparing((Film f) ->
                        Optional.ofNullable(f.getLikes()).orElse(Collections.emptySet()).size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
