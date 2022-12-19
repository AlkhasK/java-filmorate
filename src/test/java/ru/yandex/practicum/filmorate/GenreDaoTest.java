package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.genre.GenreStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenreDaoTest extends AbstractDaoTest {

    @Autowired
    private GenreStorage genreStorage;

    @Test
    void testFindByIdGenre() {
        Optional<Genre> genre = genreStorage.findById(1);

        assertThat(genre)
                .isPresent()
                .hasValueSatisfying(g -> assertThat(g)
                        .hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void testUpdateGenre() {
        Genre genreForTest = new Genre(1, "Комедия");

        genreForTest.setName("Updated");
        genreForTest = genreStorage.update(genreForTest);

        Optional<Genre> updatedGenre = genreStorage.findById(genreForTest.getId());
        assertTrue(updatedGenre.isPresent());
        assertEquals(genreForTest, updatedGenre.get());
    }

    @Test
    void testDeleteGenre() {
        genreStorage.delete(1);

        assertThat(genreStorage.findById(1)).isEmpty();
    }

    @Test
    void testFindAllGenre() {
        assertThat(genreStorage.findAll())
                .hasSize(6)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(1, 2, 3, 4, 5, 6);
    }

    @Test
    @Sql("classpath:genre/sql/data/create_data_for_genre.sql")
    void testFindByFilmIdGenre() {
        int filmId = 1;

        assertThat(genreStorage.findByFilmId(filmId))
                .hasSize(3)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(1, 2, 3);
    }

    @Test
    @Sql("classpath:genre/sql/data/create_data_for_genre.sql")
    void testFindByFilmIds() {
        List<Integer> filmIds = List.of(1, 2);

        assertThat(genreStorage.findByFilmIds(filmIds))
                .hasSize(2)
                .hasEntrySatisfying(1, genres -> assertThat(genres)
                        .hasSize(3)
                        .extracting(Genre::getId)
                        .containsExactlyInAnyOrder(1, 2, 3))
                .hasEntrySatisfying(2, genres -> assertThat(genres)
                        .hasSize(3)
                        .extracting(Genre::getId)
                        .containsExactlyInAnyOrder(4, 5, 6));
    }
}
