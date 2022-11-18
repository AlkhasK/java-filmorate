package ru.yandex.practicum.filmorate.storage.film.like;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryLikeStorage implements LikeStorage {

    private final Map<Integer, Set<Like>> storage = new HashMap<>();

    @Override
    public void add(Like like) {
        Integer filmId = like.getFilmId();
        Set<Like> likes = Optional.ofNullable(storage.get(filmId)).orElse(new HashSet<>());
        likes.add(like);
        storage.put(filmId, likes);
    }

    @Override
    public void delete(Like like) {
        Integer filmId = like.getFilmId();
        Set<Like> likes = storage.get(filmId);
        if (likes == null) {
            return;
        }
        likes.remove(like);
    }

    @Override
    public List<Like> find(Film film) {
        Integer filmId = film.getId();
        Set<Like> likes = Optional.ofNullable(storage.get(filmId)).orElse(new HashSet<>());
        return new ArrayList<>(likes);
    }

    @Override
    public List<Like> findAll() {
        return storage.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Like> getById(Integer filmId, Integer userId) {
        Optional<Set<Like>> likes = Optional.ofNullable(storage.get(filmId));
        return likes.orElse(Collections.emptySet()).stream()
                .filter(l -> l.getUserId().equals(userId))
                .findFirst();
    }
}
