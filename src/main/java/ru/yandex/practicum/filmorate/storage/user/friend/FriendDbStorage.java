package ru.yandex.practicum.filmorate.storage.user.friend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Friend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
@Qualifier("FriendDbStorage")
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Friend create(Friend friend) {
        String sql = "insert into FRIENDS (USER_ID, FRIEND_ID, IS_CONFIRMED) values(?, ?, ?)";
        jdbcTemplate.update(sql, friend.getUserId(), friend.getFriendId(), friend.getConfirmed());
        return friend;
    }

    @Override
    public void delete(int userId, int friendId) {
        String sql = "delete from FRIENDS where USER_ID = ? and FRIEND_ID = ?";
        int rowsAffected = jdbcTemplate.update(sql, userId, friendId);
        if (rowsAffected == 0) {
            throw new EntityNotFoundException(
                    String.format("No entity friend with user id : %s and friend id : %s",
                            userId, friendId));
        }
    }

    private Friend mapRowToFriend(ResultSet rs, int rowNum) throws SQLException {
        Integer userId = rs.getInt("USER_ID");
        Integer friendId = rs.getInt("FRIEND_ID");
        Boolean confirmed = rs.getBoolean("IS_CONFIRMED");
        return new Friend(userId, friendId, confirmed);
    }

    @Override
    public Optional<Friend> findById(int userId, int friendId) {
        String sql = "select USER_ID, FRIEND_ID, IS_CONFIRMED from FRIENDS " +
                "where USER_ID = ? and FRIEND_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToFriend, userId, friendId).stream()
                .findFirst();
    }

    @Override
    public Friend update(Friend friend) {
        String sql = "update FRIENDS set IS_CONFIRMED = ? " +
                "where USER_ID = ? and FRIEND_ID = ?";
        int rowsAffected = jdbcTemplate.update(sql, friend.getConfirmed(), friend.getUserId(), friend.getFriendId());
        if (rowsAffected == 0) {
            throw new EntityNotFoundException(String.format("No entity friend with user id : %s and friend id : %s",
                    friend.getUserId(), friend.getFriendId()));
        }
        return friend;
    }
}
