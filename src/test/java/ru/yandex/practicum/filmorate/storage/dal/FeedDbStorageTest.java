package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FeedDbStorageTest {
    private final FeedDbStorage feedStorage;
    private Feed feed;
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
        reviewStorage.add(review);

        feedStorage.deleteAll();
        feed = new Feed();
        feed.setTimestamp(Timestamp.from(Instant.now()).getTime());
        feed.setUserId(user.getId());
        feed.setEntityId(review.getReviewId());
    }

    @Test
    public void testCreateFeed() {
        feed.setEventType(EventType.REVIEW);
        feed.setOperation(Operation.ADD);
        feed = feedStorage.add(feed);
        assertNotNull(feed.getEventId(), "Feed is not created");

        Feed feedDb = feedStorage.get(feed.getEventId());
        assertNotNull(feedDb, "Feed not found");
        assertEquals(feed, feedDb, "Feed data incorrect");
    }

    @Test
    public void testDeleteFeed() {
        feed.setEventType(EventType.REVIEW);
        feed.setOperation(Operation.ADD);
        feed = feedStorage.add(feed);
        assertNotNull(feed.getEventId(), "Feed is not created");

        feedStorage.delete(feed.getEventId());

        Feed feedDb = feedStorage.get(feed.getEventId());
        assertNull(feedDb, "Feed is not deleted");
    }
}
