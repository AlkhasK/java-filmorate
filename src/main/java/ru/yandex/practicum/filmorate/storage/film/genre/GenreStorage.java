package ru.yandex.practicum.filmorate.storage.film.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    List<Genre> findAll();

    List<Genre> findByFilmId(int filmId);

    Genre create(Genre genre);

    Genre update(Genre genre);

    void delete(int id);

    Optional<Genre> findById(int id);
}
