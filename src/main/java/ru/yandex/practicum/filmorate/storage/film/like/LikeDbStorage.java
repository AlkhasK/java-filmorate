package ru.yandex.practicum.filmorate.storage.film.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Like;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("LikeDbStorage")
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Like create(Like like) {
        String sql = "insert into LIKES (FILM_ID, USER_ID) values(?, ?)";
        jdbcTemplate.update(sql, like.getFilmId(), like.getUserId());
        return like;
    }

    @Override
    public void delete(Like like) {
        String sql = "delete from LIKES where FILM_ID = ? and USER_ID = ?";
        int rowsAffected = jdbcTemplate.update(sql, like.getFilmId(), like.getUserId());
        if (rowsAffected == 0) {
            throw new EntityNotFoundException(String.format("No entity like with film id: %s and user id : %s",
                    like.getFilmId(), like.getUserId()));
        }
    }

    @Override
    public List<Like> findByFilmId(int filmId) {
        String sql = "select FILM_ID, USER_ID from LIKES where FILM_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToLike, filmId);
    }

    private Like mapRowToLike(ResultSet rs, int rowNum) throws SQLException {
        Integer filmId = rs.getInt("FILM_ID");
        Integer userId = rs.getInt("USER_ID");
        return new Like(filmId, userId);
    }

    @Override
    public List<Like> findAll() {
        String sql = "select FILM_ID, USER_ID from LIKES";
        return jdbcTemplate.query(sql, this::mapRowToLike);
    }

    @Override
    public Optional<Like> findById(Integer filmId, Integer userId) {
        String sql = "select FILM_ID, USER_ID from LIKES where FILM_ID = ? and USER_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToLike, filmId, userId).stream()
                .findFirst();
    }

}
