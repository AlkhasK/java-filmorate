package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.genre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.film.genre.GenreStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmGenreDaoTest extends AbstractDaoTest {

    @Autowired
    private FilmGenreStorage filmGenreStorage;

    @Autowired
    private GenreStorage genreStorage;

    @Test
    @Sql("classpath:filmgenre/sql/data/create_data_for_film_genre.sql")
    void createFilmGenreTest() {
        int filmId = 1;
        Genre genreForTest = new Genre(1, "Comedy");

        filmGenreStorage.create(filmId, genreForTest.getId());

        List<Genre> createdFilmGenre = genreStorage.findByFilmId(filmId);
        assertEquals(List.of(genreForTest), createdFilmGenre);
    }

    @Test
    @Sql("classpath:filmgenre/sql/data/create_data_for_film_genre_delete.sql")
    void deleteByFilmIdFilmGenreTest() {
        int filmId = 1;

        filmGenreStorage.delete(filmId);

        List<Genre> filmGenre = genreStorage.findByFilmId(filmId);
        assertEquals(0, filmGenre.size());
    }

    @Test
    @Sql("classpath:filmgenre/sql/data/create_data_for_film_genre_delete.sql")
    void deleteByIdFilmGenreTest() {
        int filmId = 1;
        int genreId = 1;

        filmGenreStorage.delete(filmId, genreId);

        List<Genre> filmGenre = genreStorage.findByFilmId(filmId);
        assertEquals(0, filmGenre.size());
    }
}
