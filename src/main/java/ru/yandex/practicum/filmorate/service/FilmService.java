package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.dal.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.MpaDbStorage;


import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.FilmDirector;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmDirectorStorage;

import java.util.ArrayList;
import java.util.Comparator;

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
    private final DirectorStorage directorStorage;
    private final FilmDirectorStorage filmDirectorStorage;


    public Collection<Film> getFilmsAll() {
        Collection<Film> films = filmStorage.getAll();
        readGenres(films);
        readDirectors(films);

        return films;
    }

    public Collection<Film> getFilmsPopular(Integer count) {
        int toIndex = FILMS_POPULAR_COUNT_DEFAULT;
        if (count != null && count > 0) {
            toIndex = count;
        }

        Collection<Film> films = filmStorage.getPopular(toIndex);
        readGenres(films);
        readDirectors(films);

        return films;
    }

    public Film getFilm(Long id) {
        checkFilmId(id);
        Film film = filmStorage.get(id);
        checkFilm(film, id);

        readGenres(List.of(film));
        readDirectors(List.of(film));

        return film;
    }

    public Film addFilm(Film film) {
        checkMpa(film);
        checkGenre(film);
        checkDirector(film);

        film = filmStorage.add(film);
        updateGenres(film);
        updateDirectors(film);

        return film;
    }

    public Film updateFilm(Film newFilm) {
        checkMpa(newFilm);
        checkGenre(newFilm);
        checkDirector(newFilm);

        Film filmUpdate = filmStorage.update(newFilm);
        checkFilm(filmUpdate, newFilm.getId());

        deleteGenres(newFilm);
        updateGenres(newFilm);
        updateDirectors(newFilm);

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
        Map<Long, Genre> genreMap = genreStorage.getAllInRange(
                film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()))
                .stream().collect(
                        Collectors.toMap(Genre::getId, Function.identity()));

        for (Genre genre: film.getGenres()) {
            if (genreMap.get(genre.getId()) == null) {
                throw new NotFoundException(String.format("Жанр с id = %d не найден", genre.getId()));
            }
        }
    }

    protected void readGenres(Collection<Film> films) {
        Map<Long, Set<Long>> filmGenreMap = filmGenreStorage.getAllInRange(
                films.stream().map(Film::getId).collect(Collectors.toSet()))
                .stream().collect(
                        Collectors.groupingBy(FilmGenre::getFilmId,
                                Collectors.mapping(FilmGenre::getGenreId, Collectors.toSet())));

        Map<Long, Genre> genreMap = genreStorage.getAllInRange(
                filmGenreMap.values().stream().flatMap(Set::stream).collect(Collectors.toSet()))
                .stream().collect(
                        Collectors.toMap(Genre::getId, Function.identity()));

        films.forEach(film -> {
            Set<Long> genreIdSet = filmGenreMap.get(film.getId());
            if (genreIdSet != null) {
                film.setGenres(
                        genreIdSet.stream().map(genreMap::get).filter(Objects::nonNull)
                                .collect(Collectors.toSet())
                );
            }
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

    protected void checkDirector(Film film) {
        if (film.getDirectors() == null || film.getDirectors().isEmpty()) {
            return;
        }

        Map<Long, Director> directorMap = directorStorage.getAllInRange(
                        film.getDirectors().stream().map(Director::getId).collect(Collectors.toSet()))
                .stream().collect(
                        Collectors.toMap(Director::getId, Function.identity()));

        for (Director director : film.getDirectors()) {
            if (directorMap.get(director.getId()) == null) {
                throw new NotFoundException(String.format("Режиссер с id = %d не найден", director.getId()));
            }
        }
    }

    protected void readDirectors(Collection<Film> films) {
        Map<Long, Set<Long>> filmDirectorMap = filmDirectorStorage.getAllInRange(
                        films.stream().map(Film::getId).collect(Collectors.toSet()))
                .stream().collect(
                        Collectors.groupingBy(FilmDirector::getFilmId,
                                Collectors.mapping(FilmDirector::getDirectorId, Collectors.toSet())));

        Map<Long, Director> directorMap = directorStorage.getAllInRange(
                        filmDirectorMap.values().stream().flatMap(Set::stream).collect(Collectors.toSet()))
                .stream().collect(
                        Collectors.toMap(Director::getId, Function.identity()));

        films.forEach(film -> {
            Set<Long> directorIdSet = filmDirectorMap.get(film.getId());
            if (directorIdSet != null) {
                film.setDirectors(
                        directorIdSet.stream().map(directorMap::get).filter(Objects::nonNull)
                                .collect(Collectors.toSet())
                );
            }
        });
    }

    protected void updateDirectors(Film film) {
        if (film.getDirectors() == null) {
            return;
        }
        for (Director director : film.getDirectors()) {
            FilmDirector filmDirector = new FilmDirector();
            filmDirector.setFilmId(film.getId());
            filmDirector.setDirectorId(director.getId());
            filmDirectorStorage.add(filmDirector);
        }
    }

    protected void deleteDirectors(Film film) {
        filmDirectorStorage.deleteAllByFilmId(film.getId());
    }

    public Collection<Film> getFilmsByDirector(Long directorId, String sortBy) {

        if (directorStorage.get(directorId) == null) {
            throw new NotFoundException(String.format("Режиссер с id = %d не найден", directorId));
        }

        Collection<Long> filmIds = filmDirectorStorage.getFilmIdsByDirectorId(directorId);

        if (filmIds.isEmpty()) {
            return new ArrayList<>();
        }

        Collection<Film> films = filmStorage.getAllInRange(new HashSet<>(filmIds));  // ЭТА СТРОКА

        readGenres(films);
        readDirectors(films);

        if ("year".equalsIgnoreCase(sortBy)) {
            return films.stream()
                    .sorted(Comparator.comparing(Film::getReleaseDate))
                    .collect(Collectors.toList());
        } else if ("likes".equalsIgnoreCase(sortBy)) {
            return films.stream()
                    .sorted((f1, f2) -> {
                        int likes1 = likeStorage.getAll(f1.getId()).size();
                        int likes2 = likeStorage.getAll(f2.getId()).size();
                        return Integer.compare(likes2, likes1);
                    })
                    .collect(Collectors.toList());
        } else {
            throw new ValidationException("Некорректный параметр sortBy. Допустимые значения: year, likes");
        }
    }
}
