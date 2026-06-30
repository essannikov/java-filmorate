package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FriendRowMapper;

import java.util.Collection;

@Repository
public class FriendDbStorage extends BaseDbStorage<Friend> implements FriendStorage {
    private static final String FIND_ALL_FRIEND_QUERY =
            "SELECT * FROM friends WHERE user_id = ?";
    private static final String FIND_BY_USER_FRIEND_QUERY =
            "SELECT * FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String INSERT_QUERY =
            "INSERT INTO friends(user_id, friend_id) " +
                    "VALUES (?, ?)";
    private static final String DELETE_QUERY =
            "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_QUERY_ALL =
            "DELETE FROM friends";

    public FriendDbStorage(JdbcTemplate jdbc, FriendRowMapper mapper) {
        super(jdbc, mapper, Friend.class);
    }

    @Override
    public Collection<Friend> getAll(Long userId) {
        return findMany(FIND_ALL_FRIEND_QUERY, userId);
    }

    @Override
    public Friend get(Long userId, Long friendId) {
        return findOne(FIND_BY_USER_FRIEND_QUERY, userId, friendId).orElse(null);
    }

    @Override
    public Friend add(Friend friend) {
        if (update(INSERT_QUERY, friend.getUserId(), friend.getFriendId())) {
            return friend;
        }
        return null;
    }

    @Override
    public Friend delete(Long userId, Long friendId) {
        Friend friendDelete = get(userId, friendId);
        if (update(DELETE_QUERY, userId, friendId)) {
            return friendDelete;
        }
        return null;
    }

    @Override
    public boolean deleteAll() {
        return deleteAll(DELETE_QUERY_ALL);
    }
}