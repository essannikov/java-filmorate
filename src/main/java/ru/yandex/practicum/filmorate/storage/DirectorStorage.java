package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Set;

public interface DirectorStorage {
    Collection<Director> getAll();

    Collection<Director> getAllInRange(Set<Long> idSet);

    Director get(Long id);

    Director add(Director director);

    Director update(Director newDirector);

    Director delete(Long id);

    boolean deleteAll();
}