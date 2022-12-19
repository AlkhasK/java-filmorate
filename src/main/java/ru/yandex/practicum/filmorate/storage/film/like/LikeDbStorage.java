package ru.yandex.practicum.filmorate.storage.film.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;

@Repository
@Qualifier("LikeDbStorage")
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(int filmId, int userId) {
        String sql = "insert into LIKES (FILM_ID, USER_ID) values(?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void delete(int filmId, int userId) {
        String sql = "delete from LIKES where FILM_ID = ? and USER_ID = ?";
        int rowsAffected = jdbcTemplate.update(sql, filmId, userId);
        if (rowsAffected == 0) {
            throw new EntityNotFoundException(String.format("No entity like with film id: %s and user id : %s",
                    filmId, userId));
        }
    }

}
