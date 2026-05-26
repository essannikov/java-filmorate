package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private static final int FILMS_POPULAR_COUNT_DEFAULT = 10;

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public boolean addLike(Long id, Long userId) {
        Film film = filmStorage.get(id);
        User user = userStorage.get(userId);

        if (film == null) {
            throw new NotFoundException(String.format("Фильм с id = %d не найден", id));
        }
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }

        return film.getLikes().add(userId);
    }

    public boolean deleteLike(Long id, Long userId) {
        Film film = filmStorage.get(id);
        User user = userStorage.get(userId);

        if (film == null) {
            throw new NotFoundException(String.format("Фильм с id = %d не найден", id));
        }
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }

        return film.getLikes().remove(userId);
    }

    public List<Film> getPopular(Integer count) {
        int toIndex = 0;

        if (count == null || count <= 0) {
            toIndex = FILMS_POPULAR_COUNT_DEFAULT;
        } else {
            toIndex = count;
        }

        List<Film> films = filmStorage.getAll().stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .toList();

        toIndex = Math.min(toIndex, films.size());

        return films.subList(0, toIndex);
    }
}
