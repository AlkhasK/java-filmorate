package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.validate.ValidateUserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.friend.FriendStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    private final ValidateUserService validateUserService;

    private final FriendStorage friendStorage;

    @Autowired
    public UserService(UserStorage userStorage, ValidateUserService validateUserService, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.validateUserService = validateUserService;
        this.friendStorage = friendStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User update(User user) {
        validateUserService.validateForUpdate(user);
        setNameIfEmpty(user);
        return userStorage.update(user);
    }

    public User create(User user) {
        setNameIfEmpty(user);
        return userStorage.create(user);
    }

    private void setNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            String login = user.getLogin();
            user.setName(login);
        }
    }

    public User getUser(int userId) {
        User user = userStorage.getById(userId);
        if (user == null) {
            throw new EntityNotFoundException("No user entity with id: " + userId);
        }
        return user;
    }

    public void addFriend(int userId, int friendId) {
        if (!validateUserService.isEntityExists(userId)) {
            throw new EntityNotFoundException("No user entity with id: " + userId);
        }
        if (!validateUserService.isEntityExists(friendId)) {
            throw new EntityNotFoundException("No user entity with id: " + friendId);
        }
        Friend friendUserWithFriend = new Friend(userId, friendId);
        Friend friendFriendWithUser = new Friend(friendId, userId);
        friendStorage.add(friendUserWithFriend);
        friendStorage.add(friendFriendWithUser);
    }

    public void deleteFriend(int userId, int friendId) {
        Optional<Friend> friendUserWithFriend = friendStorage.get(userId, friendId);
        if (friendUserWithFriend.isEmpty()) {
            throw new EntityNotFoundException(String.format("No friend entity with userId : %s and friendId : %s",
                    userId, friendId));
        }
        Optional<Friend> friendFriendWithUser = friendStorage.get(friendId, userId);
        if (friendFriendWithUser.isEmpty()) {
            throw new EntityNotFoundException(String.format("No friend entity with userId : %s and friendId : %s",
                    friendId, userId));
        }
        friendStorage.delete(friendUserWithFriend.get());
        friendStorage.delete(friendFriendWithUser.get());
    }

    public List<User> getFriends(int userId) {
        User user = getUser(userId);
        List<Friend> friends = friendStorage.findAll(user);
        return friends.stream()
                .map(f -> userStorage.getById(f.getFriendId()))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        List<User> userFriends = getFriends(userId);
        List<User> otherUserFriends = getFriends(otherUserId);
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toList());
    }
}
