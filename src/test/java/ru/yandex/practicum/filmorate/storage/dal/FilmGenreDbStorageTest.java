package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmGenreDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final GenreDbStorage genreStorage;
    private final FilmGenreDbStorage filmGenreStorage;
    private Film film;
    private Genre genre;
    private FilmGenre filmGenre;

    @BeforeEach
    public void beforeEach() {
        filmStorage.deleteAll();
        filmGenreStorage.deleteAll();

        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2000,1,1));
        film.setDuration(100L);
        film.setMpa(new Mpa(1L));
        filmStorage.add(film);

        genre = genreStorage.get(1L);

        filmGenre = new FilmGenre();
        filmGenre.setFilmId(film.getId());
        filmGenre.setGenreId(genre.getId());
    }

    @Test
    public void testCreateFilmGenre() {
        filmGenreStorage.add(filmGenre);

        FilmGenre filmGenreDb = filmGenreStorage.get(film.getId(), genre.getId());
        assertNotNull(filmGenreDb, "FilmGenre not found");
        assertEquals(filmGenre, filmGenreDb, "FilmGenre data incorrect");
    }

    @Test
    public void testDeleteFilmGenre() {
        filmGenreStorage.add(filmGenre);

        FilmGenre filmGenreDb = filmGenreStorage.get(film.getId(), genre.getId());
        assertNotNull(filmGenreDb, "FilmGenre not found");
        assertEquals(filmGenre, filmGenreDb, "FilmGenre data incorrect");

        filmGenreStorage.delete(film.getId(),genre.getId());

        filmGenreDb = filmGenreStorage.get(film.getId(), genre.getId());
        assertNull(filmGenreDb, "FilmGenre is not deleted");
    }

    @Test
    public void testFindUsers() {
        filmGenreStorage.add(filmGenre);

        FilmGenre filmGenreDb = filmGenreStorage.get(film.getId(), genre.getId());
        assertNotNull(filmGenreDb, "FilmGenre not found");
        assertEquals(filmGenre, filmGenreDb, "FilmGenre data incorrect");

        List<FilmGenre> filmGenreList = new ArrayList<>();
        filmGenreList.add(filmGenre);

        List<FilmGenre> filmGenreListDb = filmGenreStorage.getAll(film.getId()).stream().toList();

        assertNotNull(filmGenreListDb, "FilmGenres not found");
        assertThat(filmGenreListDb).containsExactlyElementsOf(filmGenreList);
    }
}
