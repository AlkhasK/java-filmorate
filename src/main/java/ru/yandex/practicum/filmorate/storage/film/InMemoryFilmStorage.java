package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    public Film create(Film film) {
        film.setId(id++);
        films.put(film.getId(), film);
        return film;
    }

    public void delete(int id) {
    }

    public Optional<Film> findById(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> popularFilms(int count) {
        return null;
    }
}
