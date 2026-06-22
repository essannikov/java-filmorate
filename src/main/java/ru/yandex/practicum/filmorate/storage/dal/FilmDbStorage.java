package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmRowMapper;

import java.util.Collection;
import java.util.stream.Collectors;

@Repository("filmDbStorage")
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_POPULAR_QUERY =
            "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, COUNT(ls.USER_ID) AS count_likes " +
                    "FROM films AS f " +
                    "LEFT OUTER JOIN likes AS ls ON ls.FILM_ID = f.ID " +
                    "GROUP BY f.ID , f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID " +
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

    private final FilmGenreDbStorage filmGenreStorage;
    private final MpaDbStorage mpaStorage;
    private final GenreDbStorage genreStorage;

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper, FilmGenreDbStorage filmGenreStorage, MpaDbStorage mpaStorage, GenreDbStorage genreStorage) {
        super(jdbc, mapper, Film.class);
        this.filmGenreStorage = filmGenreStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public Collection<Film> getAll() {
        Collection<Film> films = findMany(FIND_ALL_QUERY);
        films.forEach(film -> {
            readMpa(film);
            readGenres(film);
        });
        return films;
    }

    @Override
    public Collection<Film> getPopular(Integer count) {
        Collection<Film> films = findMany(FIND_POPULAR_QUERY, count);
        films.forEach(film -> {
            readMpa(film);
            readGenres(film);
        });
        return films;
    }

    @Override
    public Film get(Long id) {
        Film film = findOne(FIND_BY_ID_QUERY, id).orElse(null);
        if (film != null) {
            readMpa(film);
            readGenres(film);
        }
        return film;
    }

    @Override
    public Film add(Film film) {
        checkMpa(film);
        checkGenre(film);

        Long id = insert(INSERT_QUERY,
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        film.setId(id);
        updateGenres(film);

        return film;
    }

    @Override
    public Film update(Film newFilm) {
        checkMpa(newFilm);
        checkGenre(newFilm);

        if (update(UPDATE_QUERY,
                newFilm.getName(), newFilm.getDescription(), newFilm.getReleaseDate(),
                newFilm.getDuration(), newFilm.getMpa().getId(), newFilm.getId())) {
            deleteGenres(newFilm);
            updateGenres(newFilm);
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

    protected void readMpa(Film film) {
        film.getMpa().setName(mpaStorage.get(film.getMpa().getId()).getName());
    }

    protected void readGenres(Film film) {
        film.setGenres(
                filmGenreStorage.getAll(film.getId()).stream()
                        .map(fg -> genreStorage.get(fg.getGenreId())).collect(Collectors.toSet()));
    }

    protected void updateGenres(Film film) {
        for (Genre genre : film.getGenres()) {
            FilmGenre filmGenre = new FilmGenre();
            filmGenre.setFilmId(film.getId());
            filmGenre.setGenreId(genre.getId());
            filmGenreStorage.add(filmGenre);
        }
    }

    protected void deleteGenres(Film film) {
        filmGenreStorage.getAll(film.getId());
    }

    protected void checkMpa(Film film) {
        if (mpaStorage.get(film.getMpa().getId()) == null) {
            throw new NotFoundException(String.format("MPA с id = %d не найден", film.getMpa().getId()));
        }
    }

    protected void checkGenre(Film film) {
        for (Genre genre: film.getGenres()) {
            if (genreStorage.get(genre.getId()) == null) {
                throw new NotFoundException(String.format("Жанр с id = %d не найден", film.getMpa().getId()));
            }
        }
    }
}
