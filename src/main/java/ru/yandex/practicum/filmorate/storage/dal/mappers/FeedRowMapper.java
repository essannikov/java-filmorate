package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class FeedRowMapper implements RowMapper<Feed> {
    @Override
    public Feed mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Feed feed = new Feed();
        feed.setEventId(resultSet.getLong("id"));
        feed.setTimestamp(resultSet.getLong("timestamp"));
        feed.setUserId(resultSet.getLong("user_id"));
        feed.setEventType(EventType.getEnumFromString(resultSet.getString("event_type")));
        feed.setOperation(Operation.getEnumFromString(resultSet.getString("operation")));
        feed.setEntityId(resultSet.getLong("entity_id"));
        return feed;
    }
}
