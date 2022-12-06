package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.storage.user.friend.FriendStorage;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FriendDaoTest extends AbstractDaoTest {

    @Autowired
    @Qualifier("FriendDbStorage")
    private FriendStorage friendStorage;

    private Friend friendForTest;

    @BeforeEach
    public void createFriendForTest() {
        friendForTest = new Friend(1, 2, false);
    }

    @Test
    @Sql("classpath:friend/sql/data/create_data_for_friends.sql")
    void createFriend() {
        friendStorage.create(friendForTest);

        Optional<Friend> createdFriend = friendStorage
                .findById(friendForTest.getUserId(), friendForTest.getFriendId());
        assertTrue(createdFriend.isPresent());
        assertEquals(friendForTest, createdFriend.get());
    }

    @Test
    @Sql("classpath:friend/sql/data/create_data_for_friends.sql")
    void updateFriend() {
        friendStorage.create(friendForTest);

        friendForTest.setConfirmed(true);
        friendStorage.update(friendForTest);

        Optional<Friend> updatedFriend = friendStorage
                .findById(friendForTest.getUserId(), friendForTest.getFriendId());
        assertTrue(updatedFriend.isPresent());
        assertEquals(friendForTest, updatedFriend.get());
    }

    @Test
    @Sql("classpath:friend/sql/data/create_data_for_friends.sql")
    void deleteFriend() {
        friendStorage.create(friendForTest);

        friendStorage.delete(friendForTest.getUserId(), friendForTest.getFriendId());

        Optional<Friend> deletedFriend = friendStorage
                .findById(friendForTest.getUserId(), friendForTest.getFriendId());
        assertTrue(deletedFriend.isEmpty());
    }

    @Test
    @Sql("classpath:friend/sql/data/create_data_for_friends.sql")
    void findByIdFriend() {
        friendStorage.create(friendForTest);

        Optional<Friend> createdFriend = friendStorage
                .findById(friendForTest.getUserId(), friendForTest.getFriendId());
        assertTrue(createdFriend.isPresent());
        assertEquals(friendForTest, createdFriend.get());
    }
}
