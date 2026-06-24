package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public Collection<Genre> getGenreAll() {
        return genreStorage.getAll();
    }

    public Genre getGenre(Long id) {
        checkGenreId(id);
        Genre genre = genreStorage.get(id);
        checkGenre(genre, id);

        return genre;
    }

    protected void checkGenreId(Long id) {
        if (id == null) {
            throw new ValidationException("Не задан id жанра");
        }
    }

    protected void checkGenre(Genre genre, Long id) {
        if (genre == null) {
            throw new NotFoundException(String.format("Жанр с id = %d не найден", id));
        }
    }
}
