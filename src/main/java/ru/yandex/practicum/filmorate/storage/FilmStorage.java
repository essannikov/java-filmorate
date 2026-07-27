package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Set;

public interface FilmStorage {
    public Collection<Film> getAll();

    public Collection<Film> getPopular(Integer count, Long genreId, Integer year);

    public Collection<Film> getAllInRange(Set<Long> idSet);

    public Collection<Film> getCommon(Long userId, Long friendId);

    public Film get(Long id);

    public Film add(Film film);

    public Film update(Film newFilm);

    public Film delete(Long id);

    public boolean deleteAll();

}
