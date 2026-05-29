package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FilmService {
    private static final int FILMS_POPULAR_COUNT_DEFAULT = 10;

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> getFilmsAll() {
        return filmStorage.getAll();
    }

    public Collection<Film> getFilmsPopular(Integer count) {
        int toIndex = FILMS_POPULAR_COUNT_DEFAULT;

        if (count != null && count > 0) {
            toIndex = count;
        }

        return filmStorage.getPopular(toIndex);
    }

    public Film getFilm(Long id) {
        checkFilmId(id);
        Film film = filmStorage.get(id);
        checkFilm(film, id);

        return film;
    }

    public Film addFilm(Film film) {
        return filmStorage.add(film);
    }

    public Film updateFilm(Film newFilm) {
        Film filmUpdate = filmStorage.update(newFilm);
        checkFilm(filmUpdate, newFilm.getId());

        return filmUpdate;
    }

    public boolean addLike(Long id, Long userId) {
        checkFilmId(id);
        checkUserId(userId);

        Film film = filmStorage.get(id);
        User user = userStorage.get(userId);

        checkFilm(film, id);
        checkUser(user, userId);

        return film.getLikes().add(userId);
    }

    public boolean deleteLike(Long id, Long userId) {
        checkFilmId(id);
        checkUserId(userId);

        Film film = filmStorage.get(id);
        User user = userStorage.get(userId);

        checkFilm(film, id);
        checkUser(user, userId);

        return film.getLikes().remove(userId);
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
}
