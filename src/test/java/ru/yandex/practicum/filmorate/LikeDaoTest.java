package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.storage.film.like.LikeStorage;

import static org.assertj.core.api.Assertions.assertThat;

public class LikeDaoTest extends AbstractDaoTest {

    @Autowired
    @Qualifier("LikeDbStorage")
    private LikeStorage likeStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String sqlSelectLike = "select FILM_ID, USER_ID from LIKES where FILM_ID = ? and USER_ID = ?";

    @Test
    @Sql("classpath:like/sql/data/create_data_for_likes.sql")
    void testCreateLike() {
        int filmId = 1;
        int userId = 1;

        likeStorage.create(filmId, userId);

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlSelectLike, filmId, userId);
        assertThat(rs.next()).isTrue();
    }

    @Test
    @Sql("classpath:like/sql/data/create_data_for_likes.sql")
    void testDeleteLike() {
        int filmId = 2;
        int userId = 1;

        likeStorage.delete(filmId, userId);

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlSelectLike, filmId, userId);
        assertThat(rs.next()).isFalse();
    }

}
