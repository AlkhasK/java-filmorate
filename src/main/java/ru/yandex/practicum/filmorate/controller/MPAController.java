package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.mpa.MPAService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MPAController {

    private final MPAService mpaService;

    @Autowired
    private MPAController(MPAService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public List<MPA> findAll() {
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public MPA find(@PathVariable int id) {
        return mpaService.find(id);
    }

}
