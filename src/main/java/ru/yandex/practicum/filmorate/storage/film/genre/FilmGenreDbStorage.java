package ru.yandex.practicum.filmorate.storage.film.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(int filmId, int genreId) {
        String sql = "insert into FILM_GENRES (FILM_ID, GENRE_ID) values(?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    @Override
    public void createBatch(List<Map.Entry<Integer, Integer>> filmGenre) {
        String sql = "insert into FILM_GENRES (FILM_ID, GENRE_ID) values(?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmGenre.get(i).getKey());
                ps.setInt(2, filmGenre.get(i).getValue());
            }

            @Override
            public int getBatchSize() {
                return filmGenre.size();
            }
        });
    }

    @Override
    public void delete(int filmId, int genreId) {
        String sql = "delete from FILM_GENRES where FILM_ID = ? and GENRE_ID = ?";
        int rowsAffected = jdbcTemplate.update(sql, filmId, genreId);
        if (rowsAffected == 0) {
            throw new EntityNotFoundException(
                    String.format("No film_genre entity with film id : %s and genre id : %s",
                            filmId, genreId));
        }
    }

    @Override
    public void delete(int filmId) {
        String sql = "delete from FILM_GENRES where FILM_ID = ?";
        jdbcTemplate.update(sql, filmId);
    }
}
