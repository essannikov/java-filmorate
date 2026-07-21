package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbStorageTest {
    private final ReviewDbStorage reviewStorage;
    private Review review;
    private final UserDbStorage userStorage;
    private User user;
    private final FilmDbStorage filmStorage;
    private final MpaDbStorage mpaStorage;
    private Film film;

    @BeforeEach
    public void beforeEach() {
        userStorage.deleteAll();
        user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000,1,1));
        userStorage.add(user);

        filmStorage.deleteAll();
        Mpa mpa = new Mpa(1L);
        mpa.setName(mpaStorage.get(mpa.getId()).getName());
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(2000,1,1));
        film.setDuration(100L);
        film.setMpa(mpa);
        filmStorage.add(film);

        reviewStorage.deleteAll();
        review = new Review();
        review.setContent("content");
        review.setIsPositive(true);
        review.setUserId(user.getId());
        review.setFilmId(film.getId());
        review.setUseful(0);
    }

    @Test
    public void testCreateReview() {
        review = reviewStorage.add(review);
        assertNotNull(review.getId(), "Review is not created");

        Review reviewDb = reviewStorage.get(review.getId());
        assertNotNull(reviewDb, "Review not found");
        assertEquals(review, reviewDb, "Review data incorrect");
    }

    @Test
    public void testUpdateReview() {
        review = reviewStorage.add(review);
        assertNotNull(review.getId(), "Review is not created");

        review.setContent("content new");
        review.setIsPositive(false);
        reviewStorage.update(review);

        Review reviewDb = reviewStorage.get(review.getId());
        assertNotNull(reviewDb, "Review not found");
        assertEquals(review, reviewDb, "Review data is incorrect");
    }

    @Test
    public void testDeleteReview() {
        review = reviewStorage.add(review);
        assertNotNull(review.getId(), "Review is not created");

        reviewStorage.delete(review.getId());

        Review reviewDb = reviewStorage.get(review.getId());
        assertNull(reviewDb, "Review is not deleted");
    }

    @Test
    public void testFindReviewsByFilmId() {
        review = reviewStorage.add(review);
        assertNotNull(review.getId(), "Review is not created");

        List<Review> reviewList = new ArrayList<>();
        reviewList.add(review);

        List<Review> reviewListDb = reviewStorage.getAllByFilmId(film.getId(), 1).stream().toList();

        assertNotNull(reviewListDb, "Reviews not found");
        assertThat(reviewListDb).containsExactlyElementsOf(reviewList);
    }
}
