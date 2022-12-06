package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.controller.exception.FriendConfirmationException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.validate.ValidateUserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.friend.FriendStorage;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final ValidateUserService validateUserService;
    private final FriendStorage friendStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage, ValidateUserService validateUserService,
                       @Qualifier("FriendDbStorage") FriendStorage friendStorage) {
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
        Optional<User> user = userStorage.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("No user entity with id: " + userId);
        }
        return user.get();
    }

    public void addFriend(int userId, int friendId) {
        if (!validateUserService.isEntityExists(userId)) {
            throw new EntityNotFoundException("No user entity with id: " + userId);
        }
        if (!validateUserService.isEntityExists(friendId)) {
            throw new EntityNotFoundException("No user entity with id: " + friendId);
        }
        Optional<Friend> friend = friendStorage.findById(friendId, userId);
        if (friend.isPresent()) {
            confirmFriend(friend.get());
            return;
        }
        Friend newFriend = new Friend(userId, friendId, false);
        friendStorage.create(newFriend);
    }

    private void confirmFriend(Friend friend) {
        if (friend.getConfirmed()) {
            throw new FriendConfirmationException(
                    String.format("Friendship for user id : %s other user id: %s already confirmed",
                            friend.getUserId(), friend.getFriendId()));
        }
        friend.setConfirmed(true);
        friendStorage.update(friend);
    }

    public void deleteFriend(int userId, int friendId) {
        Optional<Friend> friendUserWithFriend = friendStorage.findById(userId, friendId);
        if (friendUserWithFriend.isPresent()) {
            friendStorage.delete(userId, friendId);
            return;
        }
        Optional<Friend> friendFriendWithUser = friendStorage.findById(friendId, userId);
        if (friendFriendWithUser.isPresent()) {
            friendStorage.delete(friendId, userId);
            return;
        }
        throw new EntityNotFoundException(String.format("No friend entity with user id : %s and other user id : %s",
                userId, friendId));
    }

    public List<User> getFriends(int userId) {
        User user = getUser(userId);
        return userStorage.findFriends(user);
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        Optional<User> user = userStorage.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("No user entity with id: " + userId);
        }
        Optional<User> otherUser = userStorage.findById(otherUserId);
        if (otherUser.isEmpty()) {
            throw new EntityNotFoundException("No user entity with id: " + otherUserId);
        }
        return userStorage.commonFriends(user.get(), otherUser.get());
    }
}
