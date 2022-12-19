package ru.yandex.practicum.filmorate.service.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.genre.GenreStorage;

import java.util.List;

@Service
public class GenreService {

    private final GenreStorage genreStorage;

    @Autowired
    private GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Genre find(int genreId) {
        return genreStorage.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("No genre entity with id : " + genreId));
    }

}
