package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.Collection;

public interface FilmGenreStorage {
    public Collection<FilmGenre> getAll(Long filmId);

    public FilmGenre get(Long filmId, Long genreId);

    public FilmGenre add(FilmGenre filmGenre);

    public FilmGenre delete(Long filmId, Long genreId);

    public boolean deleteAll();
}