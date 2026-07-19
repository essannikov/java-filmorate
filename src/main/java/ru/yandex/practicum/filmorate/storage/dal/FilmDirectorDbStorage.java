package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmDirector;
import ru.yandex.practicum.filmorate.storage.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmDirectorRowMapper;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class FilmDirectorDbStorage extends BaseDbStorage<FilmDirector> implements FilmDirectorStorage {
    private static final String FIND_ALL_DIRECTORS_QUERY =
            "SELECT * FROM film_director WHERE film_id = ?";
    private static final String FIND_ALL_IN_RANGE_QUERY =
            "SELECT * FROM film_director WHERE film_id IN (:filmIdSet)";
    private static final String FIND_FILM_IDS_BY_DIRECTOR_ID_QUERY =
            "SELECT film_id FROM film_director WHERE director_id = ?";
    private static final String FIND_BY_ID_QUERY =
            "SELECT * FROM film_director WHERE film_id = ? AND director_id = ?";
    private static final String INSERT_QUERY =
            "INSERT INTO film_director(film_id, director_id) VALUES (?, ?)";
    private static final String DELETE_QUERY =
            "DELETE FROM film_director WHERE film_id = ? AND director_id = ?";
    private static final String DELETE_QUERY_ALL =
            "DELETE FROM film_director";
    private static final String DELETE_QUERY_BY_FILM_ID =
            "DELETE FROM film_director WHERE film_id = ?";
    private static final String DELETE_QUERY_BY_DIRECTOR_ID =
            "DELETE FROM film_director WHERE director_id = ?";

    public FilmDirectorDbStorage(JdbcTemplate jdbc, FilmDirectorRowMapper mapper) {
        super(jdbc, mapper, FilmDirector.class);
    }

    @Override
    public Collection<FilmDirector> getAll(Long filmId) {
        return findMany(FIND_ALL_DIRECTORS_QUERY, filmId);
    }

    @Override
    public Collection<FilmDirector> getAllInRange(Set<Long> filmIdSet) {
        return findManyInRange(FIND_ALL_IN_RANGE_QUERY, filmIdSet, "filmIdSet");
    }

    @Override
    public Collection<Long> getFilmIdsByDirectorId(Long directorId) {
        return jdbc.queryForList(FIND_FILM_IDS_BY_DIRECTOR_ID_QUERY, Long.class, directorId);
    }

    @Override
    public FilmDirector get(Long filmId, Long directorId) {
        return findOne(FIND_BY_ID_QUERY, filmId, directorId).orElse(null);
    }

    @Override
    public FilmDirector add(FilmDirector filmDirector) {
        if (update(INSERT_QUERY, filmDirector.getFilmId(), filmDirector.getDirectorId())) {
            return filmDirector;
        }
        return null;
    }

    @Override
    public FilmDirector delete(Long filmId, Long directorId) {
        FilmDirector filmDirectorDelete = get(filmId, directorId);
        if (update(DELETE_QUERY, filmId, directorId)) {
            return filmDirectorDelete;
        }
        return null;
    }

    @Override
    public boolean deleteAll() {
        return deleteAll(DELETE_QUERY_ALL);
    }

    @Override
    public boolean deleteAllByFilmId(Long filmId) {
        return update(DELETE_QUERY_BY_FILM_ID, filmId);
    }

    @Override
    public boolean deleteAllByDirectorId(Long directorId) {
        return update(DELETE_QUERY_BY_DIRECTOR_ID, directorId);
    }
}