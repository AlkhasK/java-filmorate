package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        String sql = "insert into USERS (USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY) " +
                "values (:userEmail, :userLogin, :userName, :userBirthday)";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("userEmail", user.getEmail())
                .addValue("userLogin", user.getLogin())
                .addValue("userName", user.getName())
                .addValue("userBirthday", user.getBirthday());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, namedParameters, keyHolder, new String[]{"USER_ID"});
        int userId = keyHolder.getKey().intValue();
        user.setId(userId);
        return user;
    }

    @Override
    public User update(User user) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("userEmail", user.getEmail())
                .addValue("userLogin", user.getLogin())
                .addValue("userName", user.getName())
                .addValue("userBirthday", user.getBirthday())
                .addValue("userId", user.getId());
        String sql = "update USERS set USER_EMAIL = :userEmail, USER_LOGIN = :userLogin, USER_NAME = :userName, " +
                "USER_BIRTHDAY = :userBirthday where USER_ID = :userId";
        int rowsAffected = jdbcTemplate.update(sql, parameterSource);
        if (rowsAffected == 0) {
            throw new EntityNotFoundException("No entry user with id : " + user.getId());
        }
        return user;
    }

    @Override
    public void delete(int id) {
        String sql = "delete from USERS where USER_ID = :userId";
        int rowsAffected = jdbcTemplate.update(sql, new MapSqlParameterSource().addValue("userId", id));
        if (rowsAffected == 0) {
            throw new EntityNotFoundException("No entry user with id : " + id);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "select USER_ID, USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY from USERS";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "select USER_ID, USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY " +
                "from USERS where USER_ID = :userId";
        return jdbcTemplate.query(sql, new MapSqlParameterSource().addValue("userId", id), this::mapRowToUser).stream().findFirst();
    }

    @Override
    public List<User> findFriends(User user) {
        String sql = "select u.USER_ID, u.USER_EMAIL, u.USER_LOGIN, u.USER_NAME, u.USER_BIRTHDAY " +
                "from FRIENDS f, USERS u " +
                "where f.USER_ID = :userId and f.FRIEND_ID = u.USER_ID " +
                "union " +
                "select u.USER_ID, u.USER_EMAIL, u.USER_LOGIN, u.USER_NAME, u.USER_BIRTHDAY " +
                "from FRIENDS f, USERS u " +
                "where f.FRIEND_ID = :userId and f.IS_CONFIRMED = true and f.USER_ID = u.USER_ID";
        return jdbcTemplate.query(sql, new MapSqlParameterSource().addValue("userId", user.getId()),
                this::mapRowToUser);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        Integer userId = rs.getInt("USER_ID");
        String userEmail = rs.getString("USER_EMAIL");
        String userLogin = rs.getString("USER_LOGIN");
        String userName = rs.getString("USER_NAME");
        LocalDate userBirthday = rs.getObject("USER_BIRTHDAY", LocalDate.class);
        return new User(userId, userEmail, userLogin, userName, userBirthday);
    }

    public List<User> commonFriends(User user1, User user2) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("userId1", user1.getId())
                .addValue("userId2", user2.getId());
        String sql = "select u.USER_ID, u.USER_EMAIL, u.USER_LOGIN, u.USER_NAME, u.USER_BIRTHDAY " +
                "from FRIENDS f1, FRIENDS f2, USERS u " +
                "where (f1.USER_ID = :userId1 and f2.USER_ID = :userId2 and f1.FRIEND_ID = f2.FRIEND_ID " +
                "and f1.FRIEND_ID = u.USER_ID) " +
                "or (f1.USER_ID = :userId1 and (f2.FRIEND_ID = :userId2 and f2.IS_CONFIRMED = true) " +
                "and f1.FRIEND_ID = f2.USER_ID and f1.FRIEND_ID = u.USER_ID) " +
                "union " +
                "select u.USER_ID, u.USER_EMAIL, u.USER_LOGIN, u.USER_NAME, u.USER_BIRTHDAY " +
                "from FRIENDS f1, FRIENDS f2, USERS u " +
                "where ((f1.FRIEND_ID = :userId1 and f1.IS_CONFIRMED = true) and (f2.FRIEND_ID = :userId2 " +
                "and f2.IS_CONFIRMED = true) and f1.USER_ID = f2.USER_ID and f1.USER_ID = u.USER_ID) " +
                "or (((f1.FRIEND_ID = :userId1 and f1.IS_CONFIRMED = true) and f2.USER_ID = :userId2 " +
                "and f1.USER_ID = f2.FRIEND_ID) and f1.USER_ID = u.USER_ID)";
        return jdbcTemplate.query(sql, parameterSource, this::mapRowToUser);
    }
}
