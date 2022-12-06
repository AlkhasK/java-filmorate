package ru.yandex.practicum.filmorate.storage.film.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAll() {
        String sql = "select GENRE_ID, GENRE_NAME from GENRES";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        Integer genreId = rs.getInt("GENRE_ID");
        String genreName = rs.getString("GENRE_NAME");
        return new Genre(genreId, genreName);
    }

    @Override
    public Genre create(Genre genre) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("GENRE_NAME", genre.getName());
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("GENRES")
                .usingGeneratedKeyColumns("GENRE_ID");
        int genreId = simpleJdbcInsert.executeAndReturnKey(namedParameters).intValue();
        genre.setId(genreId);
        return genre;
    }

    @Override
    public Genre update(Genre genre) {
        String sql = "update GENRES set GENRE_NAME = ? where GENRE_ID = ?";
        int rowsAffected = jdbcTemplate.update(sql, genre.getName(), genre.getId());
        if (rowsAffected == 0) {
            throw new EntityNotFoundException("No entity genre with id : " + genre.getId());
        }
        return genre;
    }

    @Override
    public void delete(int id) {
        String sql = "delete from GENRES where GENRE_ID = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        if (rowsAffected == 0) {
            throw new EntityNotFoundException("No entity genre with id : " + id);
        }
    }

    @Override
    public Optional<Genre> findById(int id) {
        String sql = "select GENRE_ID, GENRE_NAME from GENRES where GENRE_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToGenre, id).stream()
                .findFirst();
    }

    @Override
    public List<Genre> findByFilmId(int filmId) {
        String sql = "select g.GENRE_ID, g.GENRE_NAME from FILM_GENRES f, GENRES g " +
                "where f.FILM_ID = ? and f.GENRE_ID = g.GENRE_ID";
        return jdbcTemplate.query(sql, this::mapRowToGenre, filmId);
    }
}
