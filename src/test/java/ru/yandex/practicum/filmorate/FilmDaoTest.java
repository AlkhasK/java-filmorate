package ru.yandex.practicum.filmorate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class FilmDaoTest extends AbstractDaoTest {

    @Autowired
    @Qualifier("FilmDbStorage")
    private FilmStorage filmStorage;

    @Test
    @Sql("classpath:film/sql/data/create_data_for_films.sql")
    void testFindFilmById() {
        Optional<Film> film = filmStorage.findById(1);

        assertThat(film)
                .isPresent()
                .hasValueSatisfying(f -> assertThat(f)
                        .hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    void testCreateFilm() {
        String name = "Some name";
        String descr = "Very good description";
        LocalDate releaseDate = LocalDate.now();
        Duration duration = Duration.ofSeconds(7200);
        Mpa mpa = new Mpa(1, "G");
        List<Genre> genres = List.of(new Genre(1, "Комедия"));
        Film film = Film.builder()
                .name(name)
                .description(descr)
                .releaseDate(releaseDate)
                .duration(duration)
                .genres(genres)
                .mpa(mpa).build();

        film = filmStorage.create(film);

        Optional<Film> createdFilm = filmStorage.findById(1);
        assertThat(createdFilm)
                .isPresent()
                .hasValue(film);
    }

    @Test
    @Sql("classpath:film/sql/data/create_data_for_films.sql")
    void testUpdateFilm() {
        Film film = filmStorage.findById(1).orElse(new Film());

        film.setName("Updated name");
        filmStorage.update(film);

        assertThat(filmStorage.findById(1))
                .isPresent()
                .hasValue(film);
    }

    @Test
    @Sql("classpath:film/sql/data/create_data_for_films.sql")
    void testDeleteFilm() {
        filmStorage.delete(1);

        assertThat(filmStorage.findById(1)).isEmpty();
    }

    @Test
    @Sql("classpath:film/sql/data/create_data_for_films.sql")
    void testFindAllFilms() {
        Assertions.assertThat(filmStorage.findAll())
                .hasSize(3)
                .extracting(Film::getId)
                .containsExactlyInAnyOrder(1, 2, 3);
    }

    @Test
    @Sql({"classpath:film/sql/data/create_data_for_films.sql",
            "classpath:film/sql/data/create_data_for_popular_films.sql"})
    void testPopularFilm() {
        int expectedFilmListSize = 3;

        assertThat(filmStorage.popularFilms(expectedFilmListSize))
                .hasSize(expectedFilmListSize)
                .extracting(Film::getId)
                .containsExactly(3, 2, 1);
    }
}
