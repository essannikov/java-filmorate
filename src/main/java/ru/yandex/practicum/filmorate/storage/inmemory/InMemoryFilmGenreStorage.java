package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class InMemoryFilmGenreStorage implements FilmGenreStorage {
    private final InMemoryFilmStorage filmStorage;

    @Override
    public Collection<FilmGenre> getAll(Long filmId) {
        return filmStorage.get(filmId).getGenres().stream().map(genre -> get(filmId, genre.getId())).toList();
    }

    @Override
    public FilmGenre get(Long filmId, Long genreId) {
        if (filmStorage.get(filmId).getGenres().contains(genreId)) {
            FilmGenre filmGenre = new FilmGenre();
            filmGenre.setFilmId(filmId);
            filmGenre.setGenreId(genreId);
            return filmGenre;
        }
        return null;
    }

    @Override
    public FilmGenre add(FilmGenre filmGenre) {
        filmStorage.get(filmGenre.getFilmId()).getGenres().add(new Genre(filmGenre.getGenreId()));
        return filmGenre;
    }

    @Override
    public FilmGenre delete(Long filmId, Long genreId) {
        filmStorage.get(filmId).getGenres().remove(genreId);
        FilmGenre filmGenre = new FilmGenre();
        filmGenre.setFilmId(filmId);
        filmGenre.setGenreId(genreId);
        return filmGenre;
    }

    @Override
    public boolean deleteAll() {
        filmStorage.getAll().forEach(film -> film.getGenres().clear());
        return true;
    }
}