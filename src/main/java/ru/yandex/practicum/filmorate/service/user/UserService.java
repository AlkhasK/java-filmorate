package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User update(User user) {
        log.info("Update film : {}", user);
        validateForUpdate(user);
        setNameIfEmpty(user);
        return userStorage.update(user);
    }

    public User create(User user) {
        log.info("Create film : {}", user);
        setNameIfEmpty(user);
        return userStorage.create(user);
    }

    private void validateForUpdate(User user) {
        if (!isEntityExists(user)) {
            log.warn("User with id: {} doesn't exists", user.getId());
            throw new EntityNotFoundException("No user entity with id: " + user.getId());
        }
    }

    private boolean isEntityExists(User user) {
        return userStorage.getById(user.getId()) != null;
    }

    private void setNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            String login = user.getLogin();
            user.setName(login);
        }
    }

    private void initFriends(User user) {
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
    }

    private void addFriend(User user, User friend) {
        Integer friendId = friend.getId();
        initFriends(user);
        user.getFriends().add(friendId);

        Integer userId = user.getId();
        initFriends(friend);
        friend.getFriends().add(userId);

        userStorage.update(user);
        userStorage.update(friend);
    }

    public void addFriend(int userId, int friendId) {
        log.info("Add friend id: {} to user id: {}", friendId, userId);
        User user = getUser(userId);
        User friend = getUser(friendId);
        addFriend(user, friend);
    }

    public User getUser(int userId) {
        log.info("Get user by id: {}", userId);
        User user = userStorage.getById(userId);
        if (user == null) {
            log.warn("User id: {} doesn't exists", userId);
            throw new EntityNotFoundException("No user entity with id: " + userId);
        }
        return user;
    }

    private void deleteFriend(User user, User friend) {
        Integer friendId = friend.getId();
        initFriends(user);
        user.getFriends().remove(friendId);

        Integer userId = user.getId();
        initFriends(friend);
        friend.getFriends().remove(userId);

        userStorage.update(user);
        userStorage.update(friend);
    }

    public void deleteFriend(int userId, int friendId) {
        log.info("Delete friend id: {} from user id: {}", friendId, userId);
        User user = getUser(userId);
        User friend = getUser(friendId);
        deleteFriend(user, friend);
    }

    private List<User> getFriends(User user) {
        return Optional.ofNullable(user.getFriends()).orElse(Collections.emptySet()).stream()
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getFriends(int userId) {
        log.info("Get friends for user id: {}", userId);
        User user = getUser(userId);
        return getFriends(user);
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        log.info("Get common friends for user id: {} and user id: {}", userId, otherUserId);
        List<User> userFriends = getFriends(userId);
        List<User> otherUserFriends = getFriends(otherUserId);
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toList());
    }
}
