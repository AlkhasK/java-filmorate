package ru.yandex.practicum.filmorate.storage.film.like;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;
import java.util.Optional;

public interface LikeStorage {

    void add(Like like);

    void delete(Like like);

    List<Like> find(Film film);

    List<Like> findAll();

    Optional<Like> getById(Integer filmId, Integer userId);
}
