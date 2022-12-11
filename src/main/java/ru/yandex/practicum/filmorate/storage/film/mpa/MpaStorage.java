package ru.yandex.practicum.filmorate.storage.film.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {

    List<Mpa> findAll();

    Mpa create(Mpa mpa);

    Mpa update(Mpa mpa);

    void delete(int id);

    Optional<Mpa> findById(int id);
}
