package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.DirectorRowMapper;

import java.util.Collection;
import java.util.Set;

@Repository
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {
    private static final String FIND_ALL_QUERY =
            "SELECT * FROM directors";
    private static final String FIND_ALL_IN_RANGE_QUERY =
            "SELECT * FROM directors WHERE id IN (:idSet)";
    private static final String FIND_BY_ID_QUERY =
            "SELECT * FROM directors WHERE id = ?";
    private static final String INSERT_QUERY =
            "INSERT INTO directors(name) VALUES (?)";
    private static final String UPDATE_QUERY =
            "UPDATE directors SET name = ? WHERE id = ?";
    private static final String DELETE_QUERY =
            "DELETE FROM directors WHERE id = ?";
    private static final String DELETE_QUERY_ALL =
            "DELETE FROM directors";

    public DirectorDbStorage(JdbcTemplate jdbc, DirectorRowMapper mapper) {
        super(jdbc, mapper, Director.class);
    }

    @Override
    public Collection<Director> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Collection<Director> getAllInRange(Set<Long> idSet) {
        return findManyInRange(FIND_ALL_IN_RANGE_QUERY, idSet, "idSet");
    }

    @Override
    public Director get(Long id) {
        return findOne(FIND_BY_ID_QUERY, id).orElse(null);
    }

    @Override
    public Director add(Director director) {
        Long id = insert(INSERT_QUERY, director.getName());
        director.setId(id);
        return director;
    }

    @Override
    public Director update(Director newDirector) {
        if (update(UPDATE_QUERY, newDirector.getName(), newDirector.getId())) {
            return newDirector;
        }
        return null;
    }

    @Override
    public Director delete(Long id) {
        Director directorDelete = get(id);
        if (delete(DELETE_QUERY, id)) {
            return directorDelete;
        }
        return null;
    }

    @Override
    public boolean deleteAll() {
        return deleteAll(DELETE_QUERY_ALL);
    }
}