package ru.yandex.practicum.filmorate.storage.film.like;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;
import java.util.Optional;

public interface LikeStorage {

    Like create(Like like);

    void delete(Like like);

    List<Like> findByFilmId(int filmId);

    List<Like> findAll();

    Optional<Like> findById(Integer filmId, Integer userId);

}
