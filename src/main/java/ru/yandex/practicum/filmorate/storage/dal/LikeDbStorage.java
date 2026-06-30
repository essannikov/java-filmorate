package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.LikeRowMapper;

import java.util.Collection;

@Repository
public class LikeDbStorage extends BaseDbStorage<Like> implements LikeStorage {
    private static final String FIND_ALL_LIKES_QUERY =
            "SELECT * FROM likes WHERE film_id = ?";
    private static final String FIND_BY_ID_QUERY =
            "SELECT * FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String INSERT_QUERY =
            "INSERT INTO likes(film_id, user_id) " +
                    "VALUES (?, ?)";
    private static final String DELETE_QUERY =
            "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String DELETE_QUERY_ALL =
            "DELETE FROM likes";

    public LikeDbStorage(JdbcTemplate jdbc, LikeRowMapper mapper) {
        super(jdbc, mapper, Like.class);
    }

    @Override
    public Collection<Like> getAll(Long filmId) {
        return findMany(FIND_ALL_LIKES_QUERY, filmId);
    }

    @Override
    public Like get(Long filmId, Long userId) {
        return findOne(FIND_BY_ID_QUERY, filmId, userId).orElse(null);
    }

    @Override
    public Like add(Like like) {
        if (update(INSERT_QUERY, like.getFilmId(), like.getUserId())) {
            return like;
        }
        return null;
    }

    @Override
    public Like delete(Long filmId, Long userId) {
        Like likeDelete = get(filmId, userId);
        if (update(DELETE_QUERY, filmId, userId)) {
            return likeDelete;
        }
        return null;
    }

    @Override
    public boolean deleteAll() {
        return deleteAll(DELETE_QUERY_ALL);
    }
}
