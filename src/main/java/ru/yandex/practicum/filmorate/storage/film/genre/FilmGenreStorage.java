package ru.yandex.practicum.filmorate.storage.film.genre;

public interface FilmGenreStorage {

    void create(int filmId, int genreId);

    void delete(int filmId, int genreId);

    void delete(int filmId);

}
