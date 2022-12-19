package ru.yandex.practicum.filmorate.storage.film.like;

public interface LikeStorage {

    void create(int filmId, int userId);

    void delete(int filmId, int userId);

}
