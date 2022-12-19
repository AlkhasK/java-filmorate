package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Update film : {}", film);
        return filmService.update(film);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Create film : {}", film);
        return filmService.create(film);
    }

    @GetMapping("/{id}")
    public Film find(@PathVariable int id) {
        return filmService.getFilm(id);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable int filmId, @PathVariable int userId) {
        log.info("PUT: add like to film id : {} by user id: {}", filmId, userId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLike(@PathVariable int filmId, @PathVariable int userId) {
        log.info("DELETE: remove like from film id : {} created by user id: {}", filmId, userId);
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }
}
