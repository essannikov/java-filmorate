package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmRowMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private static final String FIND_BY_ID_QUERY =
            "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.NAME AS MPA_NAME " +
            "FROM films AS f " +
            "LEFT OUTER JOIN mpa AS m " +
            "ON m.ID = f.MPA_ID " +
            "WHERE f.ID  = ?";
    private static final String FIND_ALL_QUERY =
            "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.NAME AS MPA_NAME " +
                    "FROM films AS f " +
                    "LEFT OUTER JOIN mpa AS m " +
                    "ON m.ID = f.MPA_ID";
    private static final String FIND_POPULAR_QUERY =
            "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.NAME AS MPA_NAME, COUNT(ls.USER_ID) AS count_likes " +
                    "FROM films AS f " +
                    "LEFT OUTER JOIN mpa AS m ON m.ID = f.MPA_ID " +
                    "LEFT OUTER JOIN likes AS ls ON ls.FILM_ID = f.ID " +
                    "GROUP BY f.ID , f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.NAME " +
                    "ORDER BY COUNT(ls.USER_ID) DESC " +
                    "LIMIT ?";
    private static final String INSERT_QUERY =
            "INSERT INTO films(name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
            "WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String DELETE_QUERY_ALL = "DELETE FROM films";

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper) {
        super(jdbc, mapper, Film.class);
    }

    @Override
    public Collection<Film> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Collection<Film> getPopular(Integer count, Long genreId, Integer year) {
        StringBuilder sql = new StringBuilder(
                "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.NAME AS MPA_NAME, COUNT(ls.USER_ID) AS count_likes " +
                        "FROM films AS f " +
                        "LEFT OUTER JOIN mpa AS m ON m.ID = f.MPA_ID " +
                        "LEFT OUTER JOIN likes AS ls ON ls.FILM_ID = f.ID "
        );

        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        //  жанр
        if (genreId != null) {
            sql.append("LEFT OUTER JOIN film_genre AS fg ON fg.film_id = f.id ");
            conditions.add("fg.genre_id = ?");
            params.add(genreId);
        }

        // год
        if (year != null) {
            conditions.add("YEAR(f.release_date) = ?");
            params.add(year);
        }

        if (!conditions.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", conditions));
        }

        sql.append("GROUP BY f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.NAME ");
        sql.append("ORDER BY COUNT(ls.USER_ID) DESC ");
        sql.append("LIMIT ?");
        params.add(count);

        return findMany(sql.toString(), params.toArray());
    }
    @Override
    public Film get(Long id) {
        return findOne(FIND_BY_ID_QUERY, id).orElse(null);
    }

    @Override
    public Film add(Film film) {
        Long id = insert(INSERT_QUERY,
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        film.setId(id);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (update(UPDATE_QUERY,
                newFilm.getName(), newFilm.getDescription(), newFilm.getReleaseDate(),
                newFilm.getDuration(), newFilm.getMpa().getId(), newFilm.getId())) {
            return newFilm;
        }
        return null;
    }

    @Override
    public Film delete(Long id) {
        Film filmDelete = get(id);
        if (delete(DELETE_QUERY, id)) {
            return filmDelete;
        }
        return null;
    }

    @Override
    public boolean deleteAll() {
        return deleteAll(DELETE_QUERY_ALL);
    }
}
