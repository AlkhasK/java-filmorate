package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.service.validate.ValidateFilmService;
import ru.yandex.practicum.filmorate.service.validate.ValidateUserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.like.LikeStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    private final LikeStorage likeStorage;

    private final ValidateFilmService validateFilmService;

    private final ValidateUserService validateUserService;

    @Autowired
    public FilmService(FilmStorage filmStorage, LikeStorage likeStorage,
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
        return filmStorage.update(film);
    }

    public Film create(Film film) {
        validateFilmService.validate(film);
        return filmStorage.create(film);
    }

    public Film getFilm(int filmId) {
        Film film = filmStorage.getById(filmId);
        if (film == null) {
            throw new EntityNotFoundException("No film entity with id: " + filmId);
        }
        return film;
    }

    public void addLike(int filmId, int userId) {
        if (!validateFilmService.isEntityExists(filmId)) {
            throw new EntityNotFoundException("No film entity with id: " + filmId);
        }
        if (!validateUserService.isEntityExists(userId)) {
            throw new EntityNotFoundException("No user entity with id: " + userId);
        }
        Like like = new Like(filmId, userId);
        likeStorage.add(like);
    }

    public void deleteLike(int filmId, int userId) {
        Optional<Like> like = likeStorage.getById(filmId, userId);
        if (like.isEmpty()) {
            throw new EntityNotFoundException(String.format("No like entity with filmId : %s and userId : %s",
                    filmId, userId));
        }
        likeStorage.delete(like.get());
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> allFilms = filmStorage.findAll();
        Map<Integer, Set<Like>> likes = likeStorage.findAll().stream()
                .collect(Collectors.groupingBy(Like::getFilmId, Collectors.toSet()));
        List<Integer> sortedByLikeFilmIds = getSortedByLikesFilmsIds(likes);
        List<Film> filmsWithoutLikes = getFilmsWithoutLikes(sortedByLikeFilmIds, allFilms);
        List<Film> sortedByLikeFilm = mapFilmIdToFilm(sortedByLikeFilmIds, allFilms);
        sortedByLikeFilm.addAll(filmsWithoutLikes);
        return sortedByLikeFilm.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    private List<Integer> getSortedByLikesFilmsIds(Map<Integer, Set<Like>> likes) {
        return likes.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<Integer, Set<Like>> me) ->
                                Optional.ofNullable(me.getValue()).orElse(Collections.emptySet()).size())
                        .reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private List<Film> getFilmsWithoutLikes(List<Integer> sortedByLikeFilmIds, List<Film> allFilms) {
        return allFilms.stream()
                .filter(f -> !sortedByLikeFilmIds.contains(f.getId()))
                .collect(Collectors.toList());
    }

    private List<Film> mapFilmIdToFilm(List<Integer> sortedByLikeFilmIds, List<Film> allFilms) {
        return sortedByLikeFilmIds.stream()
                .map(filmId -> allFilms.stream()
                        .filter(f -> f.getId().equals(filmId))
                        .findFirst()
                        .orElse(null))
                .collect(Collectors.toList());
    }
}
