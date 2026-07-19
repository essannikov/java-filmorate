package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmDirector;

import java.util.Collection;
import java.util.Set;

public interface FilmDirectorStorage {
    Collection<FilmDirector> getAll(Long filmId);

    Collection<FilmDirector> getAllInRange(Set<Long> filmIdSet);

    Collection<Long> getFilmIdsByDirectorId(Long directorId);

    FilmDirector get(Long filmId, Long directorId);

    FilmDirector add(FilmDirector filmDirector);

    FilmDirector delete(Long filmId, Long directorId);

    boolean deleteAll();

    boolean deleteAllByFilmId(Long filmId);

    boolean deleteAllByDirectorId(Long directorId);
}