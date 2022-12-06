package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.genre.GenreStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenreDaoTest extends AbstractDaoTest {

    @Autowired
    private GenreStorage genreStorage;

    private Genre genreForTest;

    @BeforeEach
    public void createGenreForTest() {
        genreForTest = new Genre();
        genreForTest.setName("Sad film");
    }

    @Test
    void createGenre() {
        genreForTest = genreStorage.create(genreForTest);

        Optional<Genre> createdGenre = genreStorage.findById(genreForTest.getId());
        assertTrue(createdGenre.isPresent());
        assertEquals(genreForTest, createdGenre.get());
    }

    @Test
    void updateGenre() {
        genreForTest = genreStorage.create(genreForTest);

        genreForTest.setName("Updated");
        genreForTest = genreStorage.update(genreForTest);

        Optional<Genre> updatedGenre = genreStorage.findById(genreForTest.getId());
        assertTrue(updatedGenre.isPresent());
        assertEquals(genreForTest, updatedGenre.get());
    }

    @Test
    void deleteGenre() {
        genreForTest = genreStorage.create(genreForTest);

        genreStorage.delete(genreForTest.getId());

        Optional<Genre> deletedGenre = genreStorage.findById(genreForTest.getId());
        assertTrue(deletedGenre.isEmpty());
    }

    @Test
    void findAllGenre() {
        Genre genre1 = new Genre(1, "Комедия");
        Genre genre2 = new Genre(2, "Драма");
        Genre genre3 = new Genre(3, "Мультфильм");
        Genre genre4 = new Genre(4, "Триллер");
        Genre genre5 = new Genre(5, "Документальный");
        Genre genre6 = new Genre(6, "Боевик");

        List<Genre> genres = genreStorage.findAll();

        assertEquals(List.of(genre1, genre2, genre3, genre4, genre5, genre6), genres);
    }

    @Test
    void findByIdGenre() {
        genreForTest = genreStorage.create(genreForTest);

        Optional<Genre> createdGenre = genreStorage.findById(genreForTest.getId());
        assertTrue(createdGenre.isPresent());
        assertEquals(genreForTest, createdGenre.get());
    }

    @Test
    @Sql("classpath:genre/sql/data/create_data_for_genre.sql")
    void findByFilmIdGenre() {
        int filmId = 1;
        Genre genre1 = new Genre(1, "Комедия");
        Genre genre2 = new Genre(2, "Драма");
        Genre genre3 = new Genre(3, "Мультфильм");

        List<Genre> filmGenres = genreStorage.findByFilmId(filmId);

        assertEquals(List.of(genre1, genre2, genre3), filmGenres);
    }
}
