package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmDaoTest extends AbstractDaoTest {

    @Autowired
    @Qualifier("FilmDbStorage")
    private FilmStorage filmStorage;

    private Film filmForTest;

    @BeforeEach
    public void createFilmForTest() {
        String name = "Some name";
        String descr = "Very good description";
        LocalDate releaseDate = LocalDate.now();
        Duration duration = Duration.ofSeconds(7200);
        MPA mpa = new MPA(1, "A");
        List<Genre> genres = List.of(new Genre(1, "Comedy"));
        filmForTest = Film.builder()
                .name(name)
                .description(descr)
                .releaseDate(releaseDate)
                .duration(duration)
                .genres(genres)
                .mpa(mpa).build();
    }

    @Test
    void createFilm() {
        Film film = filmStorage.create(filmForTest);

        Optional<Film> createdFilm = filmStorage.findById(filmForTest.getId());
        assertTrue(createdFilm.isPresent());
        assertEquals(film, createdFilm.get());
    }

    @Test
    void updateFilm() {
        Film film = filmStorage.create(filmForTest);

        film.setName("Updated name");
        filmStorage.update(film);

        Optional<Film> updatedFilm = filmStorage.findById(filmForTest.getId());
        assertTrue(updatedFilm.isPresent());
        assertEquals(film, updatedFilm.get());
    }

    @Test
    void getFilmById() {
        Film film = filmStorage.create(filmForTest);

        Optional<Film> createdFilm = filmStorage.findById(filmForTest.getId());

        assertTrue(createdFilm.isPresent());
        assertEquals(film, createdFilm.get());
    }

    @Test
    void deleteFilm() {
        filmStorage.create(filmForTest);

        filmStorage.delete(filmForTest.getId());

        assertTrue(filmStorage.findById(filmForTest.getId()).isEmpty());
    }

    @Test
    void findAllFilms() {
        Film film = filmStorage.create(filmForTest);

        assertEquals(List.of(film), filmStorage.findAll());
    }

    @Test
    @Sql("classpath:film/sql/data/create_data_for_popular_films.sql")
    void getPopularFilm() {
        int expectedFilmListSize = 3;
        Film film1 = filmStorage.findById(1).orElse(new Film());
        Film film2 = filmStorage.findById(2).orElse(new Film());
        Film film3 = filmStorage.findById(3).orElse(new Film());

        assertEquals(List.of(film3, film2, film1), filmStorage.popularFilms(expectedFilmListSize));
    }
}
