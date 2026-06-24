package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmGenreRowMapper;

import java.util.Collection;
import java.util.Set;

@Repository
public class FilmGenreDbStorage extends BaseDbStorage<FilmGenre> implements FilmGenreStorage {
    private static final String FIND_ALL_GENRE_QUERY =
            "SELECT * FROM film_genre WHERE film_id = ?";
    private static final String FIND_ALL_IN_RANGE_GENRE_QUERY =
            "SELECT * FROM film_genre WHERE film_id IN (:filmIdSet)";
    private static final String FIND_BY_ID_QUERY =
            "SELECT * FROM film_genre WHERE film_id = ? AND genre_id = ?";
    private static final String INSERT_QUERY =
            "INSERT INTO film_genre(film_id, genre_id) " +
                    "VALUES (?, ?)";
    private static final String DELETE_QUERY =
            "DELETE FROM film_genre WHERE film_id = ? AND genre_id = ?";
    private static final String DELETE_QUERY_ALL =
            "DELETE FROM film_genre";
    private static final String DELETE_QUERY_BY_FILM_ID =
            "DELETE FROM film_genre WHERE film_id = ?";

    public FilmGenreDbStorage(JdbcTemplate jdbc, FilmGenreRowMapper mapper) {
        super(jdbc, mapper, FilmGenre.class);
    }

    @Override
    public Collection<FilmGenre> getAll(Long filmId) {
        return findMany(FIND_ALL_GENRE_QUERY, filmId);
    }

    @Override
    public Collection<FilmGenre> getAllInRange(Set<Long> filmIdSet) {
        return findManyInRange(FIND_ALL_IN_RANGE_GENRE_QUERY, filmIdSet, "filmIdSet");
    }

    @Override
    public FilmGenre get(Long filmId, Long genreId) {
        return findOne(FIND_BY_ID_QUERY, filmId, genreId).orElse(null);
    }

    @Override
    public FilmGenre add(FilmGenre filmGenre) {
        if (update(INSERT_QUERY, filmGenre.getFilmId(), filmGenre.getGenreId())) {
            return filmGenre;
        }
        return null;
    }

    @Override
    public FilmGenre delete(Long filmId, Long genreId) {
        FilmGenre filmGenreDelete = get(filmId, genreId);
        if (update(DELETE_QUERY, filmId, genreId)) {
            return filmGenreDelete;
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
}