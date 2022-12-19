package ru.yandex.practicum.filmorate.storage.user.friend;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Friend;

import java.util.*;

@Component
public class InMemoryFriendStorage implements FriendStorage {

    private final Map<Integer, Set<Friend>> storage = new HashMap<>();

    @Override
    public Friend create(Friend friend) {
        Integer userId = friend.getUserId();
        Set<Friend> friends = Optional.ofNullable(storage.get(userId)).orElse(new HashSet<>());
        friends.add(friend);
        storage.put(userId, friends);
        return friend;
    }

    @Override
    public void delete(int userId, int otherUserId) {
        Set<Friend> friends = storage.get(userId);
        if (friends == null) {
            throw new EntityNotFoundException("No entity friend: " + userId);
        }
        friends.remove(new Friend(userId, otherUserId, false));
    }

    @Override
    public Optional<Friend> findById(int userId, int friendId) {
        Optional<Set<Friend>> friends = Optional.ofNullable(storage.get(userId));
        return friends.orElse(Collections.emptySet()).stream()
                .filter(f -> f.getFriendId().equals(friendId))
                .findFirst();
    }

    @Override
    public Friend update(Friend friend) {
        Set<Friend> friends = Optional.ofNullable(storage.get(friend.getUserId())).orElse(Collections.emptySet());
        if (friends.isEmpty()) {
            throw new EntityNotFoundException("No entity friend: " + friend);
        }
        if (!friends.remove(friend)) {
            throw new EntityNotFoundException("No entity friend: " + friend);
        }
        friends.add(friend);
        return friend;
    }
}
