package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;

public interface GenreStorage {
    public Collection<Genre> getAll();

    public Collection<Genre> getAllInRange(Set<Long> idSet);

    public Genre get(Long id);
}