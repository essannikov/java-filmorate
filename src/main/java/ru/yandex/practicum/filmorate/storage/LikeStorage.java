package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;

public interface LikeStorage {
    public Collection<Like> getAll(Long filmId);

    public Like get(Long filmId, Long userId);

    public Like add(Like like);

    public Like delete(Long filmId, Long userId);

    public boolean deleteAll();
}