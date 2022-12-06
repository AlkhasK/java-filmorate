package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    void delete(int id);

    List<Film> findAll();

    Optional<Film> findById(int id);

    List<Film> popularFilms(int count);
}
