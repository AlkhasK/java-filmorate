package ru.yandex.practicum.filmorate.service.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.mpa.MPAStorage;

import java.util.List;

@Service
public class MPAService {

    private final MPAStorage mpaStorage;

    @Autowired
    private MPAService(MPAStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<MPA> findAll() {
        return mpaStorage.findAll();
    }

    public MPA find(int mpaId) {
        return mpaStorage.findById(mpaId)
                .orElseThrow(() -> new EntityNotFoundException("No MPA entity with id : " + mpaId));
    }

}
