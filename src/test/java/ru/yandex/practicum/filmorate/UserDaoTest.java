package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDaoTest extends AbstractDaoTest {

    @Autowired
    @Qualifier("UserDbStorage")
    private UserStorage userStorage;

    @Test
    @Sql("classpath:user/sql/data/create_data_for_users.sql")
    void testFindUserById() {
        Optional<User> user = userStorage.findById(1);
        assertThat(user).isPresent()
                .hasValueSatisfying(u -> assertThat(u)
                        .hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    void testCreateUser() {
        String userEmail = "some@email.ru";
        String userLogin = "login";
        String userName = "name";
        LocalDate userBirthday = LocalDate.now();
        User user = User.builder()
                .email(userEmail)
                .login(userLogin)
                .name(userName)
                .birthday(userBirthday).build();
        user = userStorage.create(user);

        Optional<User> createdUser = userStorage.findById(user.getId());
        assertThat(createdUser).isPresent()
                .hasValue(user);
    }

    @Test
    @Sql("classpath:user/sql/data/create_data_for_users.sql")
    void testUpdateUser() {
        User user = userStorage.findById(1).orElse(new User());

        user.setName("Updated name");
        userStorage.update(user);

        assertThat(userStorage.findById(1)).isPresent()
                .hasValue(user);
    }

    @Test
    @Sql("classpath:user/sql/data/create_data_for_users.sql")
    void testDeleteUser() {
        userStorage.delete(1);

        assertThat(userStorage.findById(1)).isEmpty();
    }

    @Test
    @Sql("classpath:user/sql/data/create_data_for_users.sql")
    void testFindAllUser() {
        assertThat(userStorage.findAll())
                .hasSize(4)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(1, 2, 3, 4);
    }

    @Test
    @Sql({"classpath:user/sql/data/create_data_for_users.sql",
            "classpath:user/sql/data/create_data_for_user_friends.sql"})
    void testFindFriendsUser() {
        User user = userStorage.findById(1).orElse(new User());

        assertThat(userStorage.findFriends(user))
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(2, 3);
    }

    @Test
    @Sql({"classpath:user/sql/data/create_data_for_users.sql",
            "classpath:user/sql/data/create_data_for_user_common_friends.sql"})
    void testCommonFriendUser() {
        User user1 = userStorage.findById(1).orElse(new User());
        User user2 = userStorage.findById(2).orElse(new User());

        assertThat(userStorage.commonFriends(user1, user2))
                .hasSize(1)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(3);
    }
}
