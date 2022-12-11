package ru.yandex.practicum.filmorate.storage.film.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class GenreDbStorage implements GenreStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(NamedParameterJdbcTemplate jdbcTemplate) {
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
        String sql = "insert into GENRES (GENRE_NAME) values (:genreName)";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("genreName", genre.getName());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, namedParameters, keyHolder, new String[]{"GENRE_ID"});
        int genreId = Optional.ofNullable(keyHolder.getKey()).orElse(0).intValue();
        genre.setId(genreId);
        return genre;
    }

    @Override
    public Genre update(Genre genre) {
        String sql = "update GENRES set GENRE_NAME = :genreName where GENRE_ID = :genreId";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("genreId", genre.getId())
                .addValue("genreName", genre.getName());
        int rowsAffected = jdbcTemplate.update(sql, namedParameters);
        if (rowsAffected == 0) {
            throw new EntityNotFoundException("No entity genre with id : " + genre.getId());
        }
        return genre;
    }

    @Override
    public void delete(int id) {
        String sql = "delete from GENRES where GENRE_ID = :genreId";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("genreId", id);
        int rowsAffected = jdbcTemplate.update(sql, namedParameters);
        if (rowsAffected == 0) {
            throw new EntityNotFoundException("No entity genre with id : " + id);
        }
    }

    @Override
    public Optional<Genre> findById(int id) {
        String sql = "select GENRE_ID, GENRE_NAME from GENRES where GENRE_ID = :genreId";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("genreId", id);
        return jdbcTemplate.query(sql, namedParameters, this::mapRowToGenre).stream()
                .findFirst();
    }

    @Override
    public List<Genre> findByFilmId(int filmId) {
        String sql = "select g.GENRE_ID, g.GENRE_NAME from FILM_GENRES f, GENRES g " +
                "where f.FILM_ID = :filmId and f.GENRE_ID = g.GENRE_ID";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("filmId", filmId);
        return jdbcTemplate.query(sql, namedParameters, this::mapRowToGenre);
    }

    @Override
    public Map<Integer, List<Genre>> findByFilmIds(List<Integer> filmIds) {
        String sql = "select fg.FILM_ID, g.GENRE_ID, g.GENRE_NAME " +
                "from FILM_GENRES fg, GENRES g " +
                "where fg.GENRE_ID = g.GENRE_ID and fg.FILM_ID in (:filmIds) " +
                "group by fg.FILM_ID, g.GENRE_ID";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("filmIds", filmIds);
        Stream<Map<String, Object>> stream = jdbcTemplate.queryForStream(sql, namedParameters,
                (rs, rowNum) -> Map.ofEntries(Map.entry("filmId", rs.getInt("FILM_ID")),
                        Map.entry("genreId", rs.getInt("GENRE_ID")),
                        Map.entry("genreName", rs.getString("GENRE_NAME"))));
        return stream.collect(Collectors.groupingBy(m -> (Integer) m.get("filmId"),
                Collectors.mapping(m -> new Genre((Integer) m.get("genreId"), (String) m.get("genreName")),
                        Collectors.toList())));
    }
}
