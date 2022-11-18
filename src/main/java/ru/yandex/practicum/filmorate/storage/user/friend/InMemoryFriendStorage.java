package ru.yandex.practicum.filmorate.storage.user.friend;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryFriendStorage implements FriendStorage {

    private final Map<Integer, Set<Friend>> storage = new HashMap<>();

    @Override
    public void add(Friend friend) {
        Integer userId = friend.getUserId();
        Set<Friend> friends = Optional.ofNullable(storage.get(userId)).orElse(new HashSet<>());
        friends.add(friend);
        storage.put(userId, friends);
    }

    @Override
    public void delete(Friend friend) {
        Integer userId = friend.getUserId();
        Set<Friend> friends = storage.get(userId);
        if (friends == null) {
            return;
        }
        friends.remove(friend);
    }

    @Override
    public List<Friend> findAll(User user) {
        Integer userId = user.getId();
        Set<Friend> friends = Optional.ofNullable(storage.get(userId)).orElse(new HashSet<>());
        return new ArrayList<>(friends);
    }

    @Override
    public Optional<Friend> get(Integer userId, Integer friendId) {
        Optional<Set<Friend>> friends = Optional.ofNullable(storage.get(userId));
        return friends.orElse(Collections.emptySet()).stream()
                .filter(f -> f.getFriendId().equals(friendId))
                .findFirst();
    }
}
