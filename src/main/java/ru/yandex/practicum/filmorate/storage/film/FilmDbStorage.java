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
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.genre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.film.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.film.mpa.MPAStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreStorage filmGenreStorage;
    private final MPAStorage mpaStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmGenreStorage filmGenreStorage,
                         MPAStorage mpaStorage, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmGenreStorage = filmGenreStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public Film create(Film film) {
        MPA filmMPA = film.getMpa();
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
            film.getGenres().stream()
                    .map(Genre::getId)
                    .forEach(genreId -> filmGenreStorage.create(filmId, genreId));
        }
        return film;
    }


    @Override
    public Film update(Film film) {
        MPA filmMPA = film.getMpa();
        String sql = "update FILMS set FILM_NAME = ?, FILM_DESCRIPTION = ?, FILM_RELEASE_DATE = ?, " +
                "FILM_DURATION = ?, FILM_MPA = ? where FILM_ID = ?";
        int rowsAffected = jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration().getSeconds(), filmMPA.getId(), film.getId());
        if (rowsAffected == 0) {
            throw new EntityNotFoundException("No entity film with id : " + film.getId());
        }
        filmGenreStorage.delete(film.getId());
        if (film.getGenres() != null) {
            film.getGenres().stream()
                    .map(Genre::getId)
                    .distinct()
                    .forEach(genreId -> filmGenreStorage.create(film.getId(), genreId));
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
        String sql = "select FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, " +
                "FILM_MPA from FILMS";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        int filmId = rs.getInt("FILM_ID");
        String filmName = rs.getString("FILM_NAME");
        String filmDescr = rs.getString("FILM_DESCRIPTION");
        LocalDate filmReleaseDate = rs.getObject("FILM_RELEASE_DATE", LocalDate.class);
        Duration filmDuration = Duration.ofSeconds(rs.getLong("FILM_DURATION"));
        int filmMpaId = rs.getInt("FILM_MPA");
        Optional<MPA> filmMpa = mpaStorage.findById(filmMpaId);
        if (filmMpa.isEmpty()) {
            throw new EntityNotFoundException("No MPA entity with id: " + filmMpaId);
        }
        List<Genre> filmGenres = genreStorage.findByFilmId(filmId);
        return Film.builder()
                .id(filmId)
                .name(filmName)
                .description(filmDescr)
                .releaseDate(filmReleaseDate)
                .duration(filmDuration)
                .mpa(filmMpa.get())
                .genres(filmGenres).build();
    }

    @Override
    public Optional<Film> findById(int id) {
        String sql = "select FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, " +
                "FILM_MPA from FILMS where FILM_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, id).stream()
                .findFirst();
    }

    @Override
    public List<Film> popularFilms(int count) {
        String sql = "select f.FILM_ID, f.FILM_NAME, f.FILM_DESCRIPTION, f.FILM_RELEASE_DATE, " +
                "f.FILM_DURATION, f.FILM_MPA from FILMS f " +
                "left join LIKES l on f.FILM_ID = l.FILM_ID " +
                "group by f.FILM_ID order by count(l.USER_ID) desc limit ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }
}
