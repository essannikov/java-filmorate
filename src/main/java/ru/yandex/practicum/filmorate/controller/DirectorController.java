package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.check.OnCreate;
import ru.yandex.practicum.filmorate.check.OnUpdate;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
@Slf4j
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> findAll() {
        return directorService.getDirectorAll();
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable Long id) {
        return directorService.getDirector(id);
    }

    @PostMapping
    public Director postDirector(@Validated(OnCreate.class) @RequestBody Director director) {
        log.info("Post director: {}", director);
        return directorService.addDirector(director);
    }

    @PutMapping
    public Director putDirector(@Validated(OnUpdate.class) @RequestBody Director newDirector) {
        log.info("Put director: {}", newDirector);
        return directorService.updateDirector(newDirector);
    }

    @DeleteMapping("/{id}")
    public Director deleteDirector(@PathVariable Long id) {
        log.info("Delete director with id = {}", id);
        return directorService.deleteDirector(id);
    }
}