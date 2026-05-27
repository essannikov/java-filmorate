package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    public Collection<Film> getAll();
    public Collection<Film> getPopular(Integer count);
    public Film get(Long id);
    public Film add(Film film);
    public Film update(Film newFilm);
    public Film delete(Long id);
}
