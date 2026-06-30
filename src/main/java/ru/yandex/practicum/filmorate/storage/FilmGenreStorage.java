package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.Collection;
import java.util.Set;

public interface FilmGenreStorage {
    public Collection<FilmGenre> getAll(Long filmId);

    public Collection<FilmGenre> getAllInRange(Set<Long> filmIdSet);

    public FilmGenre get(Long filmId, Long genreId);

    public FilmGenre add(FilmGenre filmGenre);

    public FilmGenre delete(Long filmId, Long genreId);

    public boolean deleteAll();

    public boolean deleteAllByFilmId(Long filmId);
}