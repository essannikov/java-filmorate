package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.dal.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.MpaDbStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private static final int FILMS_POPULAR_COUNT_DEFAULT = 10;

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final MpaDbStorage mpaStorage;
    private final GenreDbStorage genreStorage;

    public Collection<Film> getFilmsAll() {
        Collection<Film> films = filmStorage.getAll();
        readGenres(films);

        return films;
    }

    public Collection<Film> getFilmsPopular(Integer count) {
        int toIndex = FILMS_POPULAR_COUNT_DEFAULT;
        if (count != null && count > 0) {
            toIndex = count;
        }

        Collection<Film> films = filmStorage.getPopular(toIndex);
        readGenres(films);

        return films;
    }

    public Film getFilm(Long id) {
        checkFilmId(id);
        Film film = filmStorage.get(id);
        checkFilm(film, id);

        readGenres(List.of(film));

        return film;
    }

    public Film addFilm(Film film) {
        checkMpa(film);
        checkGenre(film);

        film = filmStorage.add(film);
        updateGenres(film);

        return film;
    }

    public Film updateFilm(Film newFilm) {
        checkMpa(newFilm);
        checkGenre(newFilm);

        Film filmUpdate = filmStorage.update(newFilm);
        checkFilm(filmUpdate, newFilm.getId());

        deleteGenres(newFilm);
        updateGenres(newFilm);

        return newFilm;
    }

    public boolean addLike(Long id, Long userId) {
        checkFilmId(id);
        checkUserId(userId);

        Film film = filmStorage.get(id);
        User user = userStorage.get(userId);

        checkFilm(film, id);
        checkUser(user, userId);

        Like like = new Like();
        like.setFilmId(id);
        like.setUserId(userId);
        return likeStorage.add(like) != null;
    }

    public boolean deleteLike(Long id, Long userId) {
        checkFilmId(id);
        checkUserId(userId);

        Film film = filmStorage.get(id);
        User user = userStorage.get(userId);

        checkFilm(film, id);
        checkUser(user, userId);

        return likeStorage.delete(id, userId) != null;
    }

    protected void checkFilmId(Long id) {
        if (id == null) {
            throw new ValidationException("Не задан id фильма");
        }
    }

    protected void checkUserId(Long id) {
        if (id == null) {
            throw new ValidationException("Не задан id пользователя");
        }
    }

    protected void checkFilm(Film film, Long id) {
        if (film == null) {
            throw new NotFoundException(String.format("Фильм с id = %d не найден", id));
        }
    }

    protected void checkUser(User user, Long id) {
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
    }

    protected void checkMpa(Film film) {
        if (mpaStorage.get(film.getMpa().getId()) == null) {
            throw new NotFoundException(String.format("MPA с id = %d не найден", film.getMpa().getId()));
        }
    }

    protected void checkGenre(Film film) {
        List<Genre> genreList = genreStorage.getAll().stream().toList();

        for (Genre genre: film.getGenres()) {
            genreList.stream()
                    .filter(g -> g.getId().equals(genre.getId()))
                    .findFirst()
                    .orElseThrow(() ->
                            new NotFoundException(String.format("Жанр с id = %d не найден", genre.getId())));
        }
    }

    protected void readGenres(Collection<Film> films) {
        List<FilmGenre> filmGenreList = filmGenreStorage.getAllInRange(
                films.stream().map(Film::getId).collect(Collectors.toSet()))
                .stream().toList();
        List<Genre> genreList = genreStorage.getAll().stream().toList();

        films.forEach(film -> {
            film.setGenres(
                    filmGenreList.stream()
                            .filter(filmGenre -> filmGenre.getFilmId().equals(film.getId()))
                            .map(filmGenre -> genreList.stream()
                                    .filter(genre -> genre.getId().equals(filmGenre.getGenreId()))
                                    .findFirst().orElse(null))
                            .collect(Collectors.toSet())
            );
        });
    }

    protected void updateGenres(Film film) {
        for (Genre genre : film.getGenres()) {
            FilmGenre filmGenre = new FilmGenre();
            filmGenre.setFilmId(film.getId());
            filmGenre.setGenreId(genre.getId());
            filmGenreStorage.add(filmGenre);
        }
    }

    protected void deleteGenres(Film film) {
        filmGenreStorage.deleteAllByFilmId(film.getId());
    }
}
