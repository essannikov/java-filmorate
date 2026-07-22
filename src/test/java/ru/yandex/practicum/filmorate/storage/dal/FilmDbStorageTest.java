package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Like;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @BeforeEach
    public void beforeEach() {
        filmStorage.deleteAll();

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
    public void testDeleteFilmWithLikes() {
        // Шаг 1. Создаем пользователя
        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userStorage.add(user);

        // Шаг 2. Создаем фильм
        Film film = new Film();
        film.setName("Film for delete");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100L);
        film.setMpa(new Mpa(1L));
        filmStorage.add(film);

        // Шаг 3. Добавляем лайк
        Like like = new Like();
        like.setFilmId(film.getId());
        like.setUserId(user.getId());
        likeStorage.add(like);

        // Шаг 4. Удаляем фильм
        filmStorage.delete(film.getId());

        // Шаг 5. Проверяем - фильм удален
        Film filmDb = filmStorage.get(film.getId());
        assertNull(filmDb, "Film should be deleted");

        // Шаг 6. Проверяем - лайки удалены
        Like likeDb = likeStorage.get(film.getId(), user.getId());
        assertNull(likeDb, "Likes should be deleted");
    }
}
