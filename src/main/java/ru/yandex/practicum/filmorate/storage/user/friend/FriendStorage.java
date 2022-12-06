package ru.yandex.practicum.filmorate.storage.user.friend;

import ru.yandex.practicum.filmorate.model.Friend;

import java.util.Optional;

public interface FriendStorage {

    Friend create(Friend friend);

    Friend update(Friend friend);

    void delete(int userId, int friendId);

    Optional<Friend> findById(int userId, int friendId);
}
