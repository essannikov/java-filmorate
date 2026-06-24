package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public abstract class BaseDbStorage<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;
    protected final Class<T> entityType;

    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, Object... params) {
        try {
            return jdbc.query(query, mapper, params);
        } catch (EmptyResultDataAccessException ignored) {
            return new ArrayList<>();
        }
    }

    public <P> List<T> findManyInRange(String query, Set<P> values, String paramName) {
        if (values == null || values.isEmpty()) {
            return new ArrayList<>();
        }

        NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(jdbc);
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue(paramName, values);

        try {
            return namedTemplate.query(query, parameters, mapper);
        } catch (EmptyResultDataAccessException ignored) {
            return new ArrayList<>();
        }
    }

    protected boolean delete(String query, long id) {
        int rowsDeleted = jdbc.update(query, id);
        return rowsDeleted > 0;
    }

    protected boolean deleteAll(String query) {
        int rowsDeleted = jdbc.update(query);
        return rowsDeleted > 0;
    }

    protected long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps; }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    protected boolean update(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        return rowsUpdated > 0;
    }
}
