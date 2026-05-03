package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final int MAX_LENGTH_DESCRIPTION = 200;
    private final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12,28);

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        log.info("Post film", film);

        try {
            return create(film);
        } catch (ValidationException exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }

    public Film create(Film film) throws ValidationException {
        checkFilm(film);

        film.setId(getNextId());
        films.put(film.getId(), film);

        return film;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void checkFilm(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() != null &&
                film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    @PutMapping
    public Film putFilm(@Valid @RequestBody Film newFilm) {
        log.info("Put film", newFilm);

        try {
            return update(newFilm);
        } catch (ValidationException exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }

    public Film update(Film newFilm) throws ValidationException {
        if (newFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        if (films.containsKey(newFilm.getId())) {
            checkFilm(newFilm);

            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());

            return oldFilm;
        }

        throw new ValidationException("Фильм с id = " + newFilm.getId() + " не найден");
    }
}
