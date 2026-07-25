package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.util.Collection;

@Repository
public class FeedDbStorage extends BaseDbStorage<Feed> implements FeedStorage {
    private static final String FIND_BY_ID_QUERY =
            "SELECT * FROM feeds WHERE id = ?";
    private static final String FIND_ALL_QUERY =
            "SELECT * FROM feeds";
    private static final String FIND_BY_USER_ID_QUERY =
            "SELECT * FROM feeds WHERE user_id = ?";
    private static final String INSERT_QUERY =
            "INSERT INTO feeds(timestamp, user_id, event_type, operation, entity_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM feeds WHERE id = ?";
    private static final String DELETE_QUERY_ALL = "DELETE FROM feeds";

    public FeedDbStorage(JdbcTemplate jdbc, RowMapper<Feed> mapper) {
        super(jdbc, mapper, Feed.class);
    }

    @Override
    public Collection<Feed> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Collection<Feed> getAllByUserId(Long userId) {
        return findMany(FIND_BY_USER_ID_QUERY, userId);
    }

    @Override
    public Feed get(Long id) {
        return findOne(FIND_BY_ID_QUERY, id).orElse(null);
    }

    @Override
    public Feed add(Feed feed) {
        Long id = insert(INSERT_QUERY,
                feed.getTimestamp(), feed.getUserId(), feed.getEventType().name(),
                feed.getOperation().name(), feed.getEntityId());
        feed.setEventId(id);
        return feed;
    }

    @Override
    public Feed delete(Long id) {
        Feed feedDelete = get(id);
        if (delete(DELETE_QUERY, id)) {
            return feedDelete;
        }
        return null;
    }

    @Override
    public boolean deleteAll() {
        return deleteAll(DELETE_QUERY_ALL);
    }
}
