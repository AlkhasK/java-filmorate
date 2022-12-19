package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.genre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.film.genre.GenreStorage;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class FilmGenreDaoTest extends AbstractDaoTest {

    @Autowired
    private FilmGenreStorage filmGenreStorage;

    @Autowired
    private GenreStorage genreStorage;

    @Test
    @Sql("classpath:filmgenre/sql/data/create_data_for_film_genre.sql")
    void testCreateFilmGenreTest() {
        int filmId = 1;

        filmGenreStorage.create(filmId, 1);

        assertThat(genreStorage.findByFilmId(filmId))
                .hasSize(1)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(1);
    }

    @Test
    @Sql("classpath:filmgenre/sql/data/create_data_for_film_genre_delete.sql")
    void testDeleteByFilmIdFilmGenreTest() {
        int filmId = 1;

        filmGenreStorage.delete(filmId);

        assertThat(genreStorage.findByFilmId(filmId)).hasSize(0);
    }

    @Test
    @Sql("classpath:filmgenre/sql/data/create_data_for_film_genre_delete.sql")
    void testDeleteByIdFilmGenreTest() {
        int filmId = 1;

        filmGenreStorage.delete(filmId, 1);

        assertThat(genreStorage.findByFilmId(filmId)).hasSize(0);
    }

    @Test
    @Sql("classpath:filmgenre/sql/data/create_data_for_film_genre.sql")
    void testCreateBatch() {
        int filmId = 1;
        List<Map.Entry<Integer, Integer>> filmGenre = List.of(Map.entry(filmId, 1),
                Map.entry(filmId, 2), Map.entry(filmId, 3));

        filmGenreStorage.createBatch(filmGenre);

        assertThat(genreStorage.findByFilmId(filmId))
                .hasSize(3)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(1, 2, 3);
    }

}
