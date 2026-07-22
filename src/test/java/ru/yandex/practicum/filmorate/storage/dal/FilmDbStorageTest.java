package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import java.util.Collection;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Like;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import ru.yandex.practicum.filmorate.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final MpaDbStorage mpaStorage;
    private Film film;

    @Autowired
    private UserDbStorage userStorage;
    @Autowired
    private LikeDbStorage likeStorage;
    @Autowired
    private FilmGenreStorage filmGenreStorage;

    @BeforeEach
    public void beforeEach() {
        filmStorage.deleteAll();
        likeStorage.deleteAll();
        filmStorage.deleteAll();
        userStorage.deleteAll();

        Mpa mpa = new Mpa(1L);
        mpa.setName(mpaStorage.get(mpa.getId()).getName());

        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2000,1,1));
        film.setDuration(100L);
        film.setMpa(mpa);
    }

    @Test
    public void testCreateFilm() {
        film = filmStorage.add(film);
        assertNotNull(film.getId(), "Film is not created");

        Film filmDb = filmStorage.get(film.getId());
        assertNotNull(filmDb, "Film not found");
        assertEquals(film, filmDb, "Film data incorrect");
    }

    @Test
    public void testUpdateFilm() {
        film = filmStorage.add(film);
        assertNotNull(film.getId(), "Film is not created");

        Mpa mpa = new Mpa(2L);
        mpa.setName(mpaStorage.get(mpa.getId()).getName());

        film.setName("nameNew");
        film.setDescription("descriptionNew");
        film.setReleaseDate(LocalDate.of(2001,11,11));
        film.setDuration(101L);
        film.setMpa(mpa);
        filmStorage.update(film);

        Film filmDb = filmStorage.get(film.getId());
        assertNotNull(filmDb, "Film not found");
        assertEquals(film, filmDb, "Film data is incorrect");
    }

    @Test
    public void testDeleteFilm() {
        film = filmStorage.add(film);
        assertNotNull(film.getId(), "Film is not created");

        filmStorage.delete(film.getId());

        Film filmDb = filmStorage.get(film.getId());
        assertNull(filmDb, "Film is not deleted");
    }

    @Test
    public void testFindFilmById() {
        film = filmStorage.add(film);
        assertNotNull(film.getId(), "Film is not created");

        Film filmDb = filmStorage.get(film.getId());
        assertNotNull(filmDb, "Film not found");
        assertEquals(film, filmDb, "Film data incorrect");
    }

    @Test
    public void testFindFilms() {
        film = filmStorage.add(film);
        assertNotNull(film.getId(), "Film is not created");

        List<Film> filmList = new ArrayList<>();
        filmList.add(film);

        List<Film> filmListDb = filmStorage.getAll().stream().toList();

        assertNotNull(filmListDb, "Films not found");
        assertThat(filmListDb).containsExactlyElementsOf(filmList);
    }

    @Test
    public void testGetPopularWithGenreFilter() {
        // Создаем фильмы
        Film film1 = createFilm("Film 1", LocalDate.of(2000, 1, 1), 1L);
        Film film2 = createFilm("Film 2", LocalDate.of(2000, 1, 1), 2L);
        Film film3 = createFilm("Film 3", LocalDate.of(2001, 1, 1), 1L);

        // Добавляем лайки
        addLike(film1.getId(), 1L);
        addLike(film1.getId(), 2L);
        addLike(film2.getId(), 1L);

        // Получаем популярные фильмы по жанру 1
        Collection<Film> result = filmStorage.getPopular(10, 1L, null);

        // Проверяем
        assertNotNull(result);
        assertEquals(2, result.size()); // film1 и film3
        assertEquals(film1.getId(), result.iterator().next().getId()); // film1 самый популярный
    }

    @Test
    public void testGetPopularWithYearFilter() {
        // Создаем фильмы
        Film film1 = createFilm("Film 1", LocalDate.of(2000, 1, 1), 1L);
        Film film2 = createFilm("Film 2", LocalDate.of(2000, 1, 1), 2L);
        Film film3 = createFilm("Film 3", LocalDate.of(2001, 1, 1), 1L);

        // Добавляем лайки
        addLike(film1.getId(), 1L);
        addLike(film2.getId(), 1L);
        addLike(film2.getId(), 2L);
        addLike(film3.getId(), 1L);

        // Получаем популярные фильмы за 2000 год
        Collection<Film> result = filmStorage.getPopular(10, null, 2000);

        // Проверяем
        assertNotNull(result);
        assertEquals(2, result.size()); // film1 и film2
        assertEquals(film2.getId(), result.iterator().next().getId()); // film2 самый популярный
    }

    @Test
    public void testGetPopularWithGenreAndYearFilter() {
        // Создаем фильмы
        Film film1 = createFilm("Film 1", LocalDate.of(2000, 1, 1), 1L);
        Film film2 = createFilm("Film 2", LocalDate.of(2000, 1, 1), 2L);
        Film film3 = createFilm("Film 3", LocalDate.of(2001, 1, 1), 1L);

        // Добавляем лайки
        addLike(film1.getId(), 1L);
        addLike(film1.getId(), 2L);
        addLike(film2.getId(), 1L);
        addLike(film3.getId(), 1L);

        // Получаем популярные фильмы жанра 1 за 2000 год
        Collection<Film> result = filmStorage.getPopular(10, 1L, 2000);

        // Проверяем
        assertNotNull(result);
        assertEquals(1, result.size()); // только film1
        assertEquals(film1.getId(), result.iterator().next().getId());
    }

    @Test
    public void testGetPopularWithEmptyResult() {
        // Получаем популярные фильмы по несуществующему жанру
        Collection<Film> result = filmStorage.getPopular(10, 999L, null);

        // Проверяем
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private Film createFilm(String name, LocalDate releaseDate, Long genreId) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Description");
        film.setReleaseDate(releaseDate);
        film.setDuration(100L);
        film.setMpa(new Mpa(1L));
        filmStorage.add(film);

        // Добавляем жанр
        FilmGenre filmGenre = new FilmGenre();
        filmGenre.setFilmId(film.getId());
        filmGenre.setGenreId(genreId);
        filmGenreStorage.add(filmGenre);

        return film;
    }

    private void addLike(Long filmId, Long userId) {
        User user = new User();
        user.setEmail("user" + userId + "@mail.ru");
        user.setLogin("login" + userId);
        user.setName("User " + userId);
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userStorage.add(user);

        Like like = new Like();
        like.setFilmId(filmId);
        like.setUserId(user.getId());
        likeStorage.add(like);
    }
}
