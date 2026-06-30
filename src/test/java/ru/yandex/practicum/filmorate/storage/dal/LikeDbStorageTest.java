package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

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
public class LikeDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final LikeDbStorage likeStorage;
    private Film film;
    private User user;
    private Like like;

    @BeforeEach
    public void beforeEach() {
        filmStorage.deleteAll();
        userStorage.deleteAll();

        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2000,1,1));
        film.setDuration(100L);
        film.setMpa(new Mpa(1L));
        filmStorage.add(film);

        user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000,1,1));
        userStorage.add(user);

        like = new Like();
        like.setFilmId(film.getId());
        like.setUserId(user.getId());
    }

    @Test
    public void testCreateLike() {
        likeStorage.add(like);

        Like likeDb = likeStorage.get(film.getId(), user.getId());
        assertNotNull(likeDb, "Like not found");
        assertEquals(like, likeDb, "Like data incorrect");
    }

    @Test
    public void testDeleteLike() {
        likeStorage.add(like);

        Like likeDb = likeStorage.get(film.getId(), user.getId());
        assertNotNull(likeDb, "Like not found");
        assertEquals(like, likeDb, "Like data incorrect");

        likeStorage.delete(film.getId(),user.getId());

        likeDb = likeStorage.get(film.getId(), user.getId());
        assertNull(likeDb, "Like is not deleted");
    }

    @Test
    public void testFindUsers() {
        likeStorage.add(like);

        Like likeDb = likeStorage.get(film.getId(), user.getId());
        assertNotNull(likeDb, "Like not found");
        assertEquals(like, likeDb, "Like data incorrect");

        List<Like> likeList = new ArrayList<>();
        likeList.add(like);

        List<Like> likeListDb = likeStorage.getAll(film.getId()).stream().toList();

        assertNotNull(likeListDb, "Likes not found");
        assertThat(likeListDb).containsExactlyElementsOf(likeList);
    }
}
