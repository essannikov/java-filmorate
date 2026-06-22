package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class InMemoryLikeStorage implements LikeStorage {
    private final InMemoryFilmStorage filmStorage;

    @Override
    public Collection<Like> getAll(Long filmId) {
        return filmStorage.get(filmId).getLikes().stream().map(userId -> get(filmId, userId)).toList();
    }

    @Override
    public Like get(Long filmId, Long userId) {
        if (filmStorage.get(filmId).getLikes().contains(userId)) {
            Like like = new Like();
            like.setFilmId(filmId);
            like.setUserId(userId);
            return like;
        }
        return null;
    }

    @Override
    public Like add(Like like) {
        filmStorage.get(like.getFilmId()).getLikes().add(like.getUserId());
        return like;
    }

    @Override
    public Like delete(Long filmId, Long userId) {
        filmStorage.get(filmId).getLikes().remove(userId);
        Like like = new Like();
        like.setFilmId(filmId);
        like.setUserId(userId);
        return like;
    }

    @Override
    public boolean deleteAll() {
        filmStorage.getAll().forEach(film -> film.getLikes().clear());
        return true;
    }
}
