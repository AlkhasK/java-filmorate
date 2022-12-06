package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.service.validate.ValidateFilmService;
import ru.yandex.practicum.filmorate.service.validate.ValidateUserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.like.LikeStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    private final LikeStorage likeStorage;

    private final ValidateFilmService validateFilmService;

    private final ValidateUserService validateUserService;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Qualifier("LikeDbStorage") LikeStorage likeStorage,
                       ValidateFilmService validateFilmService, ValidateUserService validateUserService) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.validateFilmService = validateFilmService;
        this.validateUserService = validateUserService;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film update(Film film) {
        validateFilmService.validateForUpdate(film);
        removeGenreDuplicate(film);
        return filmStorage.update(film);
    }

    private void removeGenreDuplicate(Film film) {
        List<Genre> genres = film.getGenres();
        if (genres != null) {
            genres = genres.stream().distinct().collect(Collectors.toList());
            film.setGenres(genres);
        }
    }

    public Film create(Film film) {
        validateFilmService.validate(film);
        removeGenreDuplicate(film);
        return filmStorage.create(film);
    }

    public Film getFilm(int filmId) {
        Optional<Film> film = filmStorage.findById(filmId);
        if (film.isEmpty()) {
            throw new EntityNotFoundException("No film entity with id: " + filmId);
        }
        return film.get();
    }

    public void addLike(int filmId, int userId) {
        if (!validateFilmService.isEntityExists(filmId)) {
            throw new EntityNotFoundException("No film entity with id: " + filmId);
        }
        if (!validateUserService.isEntityExists(userId)) {
            throw new EntityNotFoundException("No user entity with id: " + userId);
        }
        Like like = new Like(filmId, userId);
        likeStorage.create(like);
    }

    public void deleteLike(int filmId, int userId) {
        Optional<Like> like = likeStorage.findById(filmId, userId);
        if (like.isEmpty()) {
            throw new EntityNotFoundException(String.format("No like entity with filmId : %s and userId : %s",
                    filmId, userId));
        }
        likeStorage.delete(like.get());
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.popularFilms(count);
    }
}
