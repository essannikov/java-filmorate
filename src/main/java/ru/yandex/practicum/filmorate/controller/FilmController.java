package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.check.OnCreate;
import ru.yandex.practicum.filmorate.check.OnUpdate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        return filmStorage.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) {
        if (id == null) {
            throw new ValidationException("Не задан id фильма");
        }

        Film film = filmStorage.get(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }

        return film;
    }

    @PostMapping
    public Film postFilm(@Validated(OnCreate.class) @RequestBody Film film) {
        log.info("Post film", film);

        return filmStorage.add(film);
    }

    @PutMapping
    public Film putFilm(@Validated(OnUpdate.class) @RequestBody Film newFilm) {
        log.info("Put film", newFilm);

        Film modifyFilm = filmStorage.modify(newFilm);
        if (modifyFilm == null) {
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        return modifyFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean putLike(@PathVariable Long id,
                           @PathVariable Long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteLike(@PathVariable Long id,
                              @PathVariable Long userId) {
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam Integer count) {
        return filmService.getPopular(count);
    }
}
