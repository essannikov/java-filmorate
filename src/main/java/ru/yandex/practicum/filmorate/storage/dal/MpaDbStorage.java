package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.MpaRowMapper;

import java.util.Collection;


@Repository
public class MpaDbStorage extends BaseDbStorage<Mpa> implements MpaStorage {
    private static final String FIND_ALL_QUERY =
            "SELECT * FROM mpa";
    private static final String FIND_BY_ID_QUERY =
            "SELECT * FROM mpa WHERE id = ?";

    public MpaDbStorage(JdbcTemplate jdbc, MpaRowMapper mapper) {
        super(jdbc, mapper, Mpa.class);
    }

    @Override
    public Collection<Mpa> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Mpa get(Long id) {
        return findOne(FIND_BY_ID_QUERY, id).orElse(null);
    }
}