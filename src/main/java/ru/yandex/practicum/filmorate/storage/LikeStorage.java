package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;
import java.util.Set;

public interface LikeStorage {
    public Collection<Like> getAll(Long filmId);

    public Collection<Like> getAllByUserId(Long userId);

    public Collection<Like> getAllInRangeFilmId(Set<Long> idSet);

    public Collection<Like> getAllInRangeUserId(Set<Long> idSet);

    public Like get(Long filmId, Long userId);

    public Like add(Like like);

    public Like delete(Long filmId, Long userId);

    public boolean deleteAll();
}