package ru.yandex.practicum.filmorate.storage.film.genre;

import java.util.List;
import java.util.Map;

public interface FilmGenreStorage {

    void create(int filmId, int genreId);

    void createBatch(List<Map.Entry<Integer, Integer>> filmGenre);

    void delete(int filmId, int genreId);

    void delete(int filmId);

}
