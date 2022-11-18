package ru.yandex.practicum.filmorate.storage.user.friend;

import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FriendStorage {

    void add(Friend friend);

    void delete(Friend friend);

    List<Friend> findAll(User user);

    Optional<Friend> get(Integer userId, Integer friendId);
}
