package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.genre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.film.genre.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreStorage filmGenreStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmGenreStorage filmGenreStorage, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmGenreStorage = filmGenreStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public Film create(Film film) {
        Mpa filmMPA = film.getMpa();
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("FILM_NAME", film.getName())
                .addValue("FILM_DESCRIPTION", film.getDescription())
                .addValue("FILM_RELEASE_DATE", film.getReleaseDate())
                .addValue("FILM_DURATION", film.getDuration())
                .addValue("FILM_MPA", filmMPA.getId());
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");
        int filmId = simpleJdbcInsert.executeAndReturnKey(namedParameters).intValue();
        film.setId(filmId);
        if (film.getGenres() != null) {
            createGenres(film);
        }
        return film;
    }

    private void createGenres(Film film) {
        List<Map.Entry<Integer, Integer>> filmGenreEntries = film.getGenres().stream()
                .map(genre -> new AbstractMap.SimpleEntry<>(film.getId(), genre.getId()))
                .distinct()
                .collect(Collectors.toList());
        filmGenreStorage.createBatch(filmGenreEntries);
    }

    @Override
    public Film update(Film film) {
        Mpa filmMPA = film.getMpa();
        String sql = "update FILMS set FILM_NAME = ?, FILM_DESCRIPTION = ?, FILM_RELEASE_DATE = ?, " +
                "FILM_DURATION = ?, FILM_MPA = ? where FILM_ID = ?";
        int rowsAffected = jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration().getSeconds(), filmMPA.getId(), film.getId());
        if (rowsAffected == 0) {
            throw new EntityNotFoundException("No entity film with id : " + film.getId());
        }
        filmGenreStorage.delete(film.getId());
        if (film.getGenres() != null) {
            createGenres(film);
        }
        return film;
    }

    @Override
    public void delete(int id) {
        String sql = "delete from FILMS where FILM_ID = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        if (rowsAffected == 0) {
            throw new EntityNotFoundException("No entity film with id : " + id);
        }
        filmGenreStorage.delete(id);
    }

    @Override
    public List<Film> findAll() {
        String sql = "select f.FILM_ID, f.FILM_NAME, f.FILM_DESCRIPTION, f.FILM_RELEASE_DATE, " +
                "f.FILM_DURATION, f.FILM_MPA, m.MPA_NAME from FILMS f left join MPA m on f.FILM_MPA = m.MPA_ID";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);
        return setFilmGenre(films);
    }

    private List<Film> setFilmGenre(List<Film> films) {
        List<Integer> filmIds = films.stream().map(Film::getId).collect(Collectors.toList());
        Map<Integer, List<Genre>> filmGenres = genreStorage.findByFilmIds(filmIds);
        films.forEach(f -> f.setGenres(
                filmGenres.getOrDefault(f.getId(), Collections.emptyList()))
        );
        return films;
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        int filmId = rs.getInt("FILM_ID");
        String filmName = rs.getString("FILM_NAME");
        String filmDescr = rs.getString("FILM_DESCRIPTION");
        LocalDate filmReleaseDate = rs.getObject("FILM_RELEASE_DATE", LocalDate.class);
        Duration filmDuration = Duration.ofSeconds(rs.getLong("FILM_DURATION"));
        int filmMpaId = rs.getInt("FILM_MPA");
        String mpaName = rs.getString("MPA_NAME");
        Mpa mpa = (filmMpaId == 0) ? null : new Mpa(filmMpaId, mpaName);
        return Film.builder()
                .id(filmId)
                .name(filmName)
                .description(filmDescr)
                .releaseDate(filmReleaseDate)
                .duration(filmDuration)
                .mpa(mpa).build();
    }

    @Override
    public Optional<Film> findById(int id) {
        String sql = "select f.FILM_ID, f.FILM_NAME, f.FILM_DESCRIPTION, f.FILM_RELEASE_DATE, f.FILM_DURATION, " +
                "f.FILM_MPA, m.MPA_NAME from FILMS f left join MPA m on f.FILM_MPA = m.MPA_ID  where f.FILM_ID = ?";
        Optional<Film> film = jdbcTemplate.query(sql, this::mapRowToFilm, id).stream()
                .findFirst();
        film.ifPresent(f -> f.setGenres(genreStorage.findByFilmId(f.getId())));
        return film;
    }

    @Override
    public List<Film> popularFilms(int count) {
        String sql = "select f.FILM_ID, f.FILM_NAME, f.FILM_DESCRIPTION, f.FILM_RELEASE_DATE, " +
                "f.FILM_DURATION, f.FILM_MPA, m.MPA_NAME from FILMS f " +
                "left join MPA m on f.FILM_MPA = m.MPA_ID " +
                "left join LIKES l on f.FILM_ID = l.FILM_ID " +
                "group by f.FILM_ID order by count(l.USER_ID) desc limit ?";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, count);
        return setFilmGenre(films);
    }
}
