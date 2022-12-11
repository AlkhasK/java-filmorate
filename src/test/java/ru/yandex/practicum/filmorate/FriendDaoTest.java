package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.storage.user.friend.FriendStorage;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FriendDaoTest extends AbstractDaoTest {

    @Autowired
    @Qualifier("FriendDbStorage")
    private FriendStorage friendStorage;

    @Test
    @Sql("classpath:friend/sql/data/create_data_for_friends.sql")
    void testFindByIdFriend() {
        Optional<Friend> friend = friendStorage.findById(1, 2);

        assertThat(friend)
                .isPresent()
                .hasValueSatisfying(f -> assertThat(f)
                        .hasFieldOrPropertyWithValue("userId", 1)
                        .hasFieldOrPropertyWithValue("friendId", 2)
                );
    }

    @Test
    @Sql("classpath:friend/sql/data/create_data_for_friends.sql")
    void testCreateFriend() {
        Friend friend = new Friend(3, 4, false);
        friend = friendStorage.create(friend);

        Optional<Friend> createdFriend = friendStorage.findById(3, 4);
        assertThat(createdFriend)
                .isPresent()
                .hasValue(friend);
    }

    @Test
    @Sql("classpath:friend/sql/data/create_data_for_friends.sql")
    void testUpdateFriend() {
        Friend friend = friendStorage.findById(1, 2)
                .orElse(new Friend());

        friend.setConfirmed(true);
        friendStorage.update(friend);

        assertThat(friendStorage.findById(1, 2))
                .isPresent()
                .hasValue(friend);
    }

    @Test
    @Sql("classpath:friend/sql/data/create_data_for_friends.sql")
    void testDeleteFriend() {
        friendStorage.delete(1, 2);

        assertThat(friendStorage.findById(1, 2))
                .isEmpty();
    }
}
