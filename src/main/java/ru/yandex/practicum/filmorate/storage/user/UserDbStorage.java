package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
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

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("USER_EMAIL", user.getEmail())
                .addValue("USER_LOGIN", user.getLogin())
                .addValue("USER_NAME", user.getName())
                .addValue("USER_BIRTHDAY", user.getBirthday());
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");
        int userId = simpleJdbcInsert.executeAndReturnKey(namedParameters).intValue();
        user.setId(userId);
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "update USERS set USER_EMAIL = ?, USER_LOGIN = ?, USER_NAME = ?, " +
                "USER_BIRTHDAY = ? where USER_ID = ?";
        int rowsAffected = jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), user.getId());
        if (rowsAffected == 0) {
            throw new EntityNotFoundException("No entry user with id : " + user.getId());
        }
        return user;
    }

    @Override
    public void delete(int id) {
        String sql = "delete from USERS where USER_ID = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
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
                "from USERS where USER_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, id).stream().findFirst();
    }

    @Override
    public List<User> findFriends(User user) {
        String sql = "select u.USER_ID, u.USER_EMAIL, u.USER_LOGIN, u.USER_NAME, u.USER_BIRTHDAY " +
                "from USERS u " +
                "where u.USER_ID in (" +
                "select f1.FRIEND_ID " +
                "from FRIENDS f1 " +
                "where f1.USER_ID = ? " +
                "union " +
                "select f2.USER_ID " +
                "from FRIENDS f2 " +
                "where f2.FRIEND_ID = ? and f2.IS_CONFIRMED = true)";
        return jdbcTemplate.query(sql, this::mapRowToUser, user.getId(), user.getId());
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
        String sql = "select distinct u.USER_ID, u.USER_EMAIL, u.USER_LOGIN, u.USER_NAME, u.USER_BIRTHDAY" +
                " from USERS u " +
                "where u.USER_ID in ((select FRIEND_ID from FRIENDS where USER_ID = ? union select USER_ID " +
                "from FRIENDS where FRIEND_ID = ? and IS_CONFIRMED = true) " +
                "intersect (select FRIEND_ID from FRIENDS where USER_ID = ? union select USER_ID " +
                "from FRIENDS where FRIEND_ID = ? and IS_CONFIRMED = true))";
        return jdbcTemplate.query(sql, this::mapRowToUser, user1.getId(), user1.getId(), user2.getId(), user2.getId());
    }
}
