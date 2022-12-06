package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDaoTest extends AbstractDaoTest {

    @Autowired
    @Qualifier("UserDbStorage")
    private UserStorage userStorage;

    private User userForTest;

    @BeforeEach
    public void createUserForTest() {
        String userEmail = "some@email.ru";
        String userLogin = "login";
        String userName = "name";
        LocalDate userBirthday = LocalDate.now();
        userForTest = User.builder()
                .email(userEmail)
                .login(userLogin)
                .name(userName)
                .birthday(userBirthday).build();
    }

    @Test
    void createUser() {
        User user = userStorage.create(userForTest);

        Optional<User> createdUser = userStorage.findById(user.getId());
        assertTrue(createdUser.isPresent());
        assertEquals(user, createdUser.get());
    }

    @Test
    void updateUser() {
        User user = userStorage.create(userForTest);

        user.setName("Updated name");
        userStorage.update(user);

        Optional<User> updatedUser = userStorage.findById(user.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals(user, updatedUser.get());
    }

    @Test
    void deleteUser() {
        User user = userStorage.create(userForTest);

        userStorage.delete(user.getId());

        Optional<User> deletedUser = userStorage.findById(user.getId());
        assertTrue(deletedUser.isEmpty());
    }

    @Test
    void findAllUser() {
        User user = userStorage.create(userForTest);

        List<User> users = userStorage.findAll();

        assertEquals(List.of(user), users);
    }

    @Test
    @Sql("classpath:user/sql/data/create_data_for_user_friends.sql")
    void findFriendsUser() {
        User user1 = userStorage.findById(1).orElse(new User());
        User user2 = userStorage.findById(2).orElse(new User());
        User user3 = userStorage.findById(3).orElse(new User());

        List<User> friends = userStorage.findFriends(user1);

        assertEquals(List.of(user2, user3), friends);
    }

    @Test
    @Sql("classpath:user/sql/data/create_data_for_user_common_friends.sql")
    void commonFriendUser() {
        User user1 = userStorage.findById(1).orElse(new User());
        User user2 = userStorage.findById(2).orElse(new User());
        User user3 = userStorage.findById(3).orElse(new User());

        List<User> commonFriends = userStorage.commonFriends(user1, user2);

        assertEquals(List.of(user3), commonFriends);
    }
}
