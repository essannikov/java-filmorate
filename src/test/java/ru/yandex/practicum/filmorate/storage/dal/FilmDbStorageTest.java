package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

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
}
