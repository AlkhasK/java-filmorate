package ru.yandex.practicum.filmorate.storage.film.mpa;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;
import java.util.Optional;

public interface MPAStorage {

    List<MPA> findAll();

    MPA create(MPA mpa);

    MPA update(MPA mpa);

    void delete(int id);

    Optional<MPA> findById(int id);
}
