package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private static final byte REVIEW_ESTIMATE_USEFUL = 1;
    private static final byte REVIEW_ESTIMATE_USELESS = -1;
    private static final Integer DEFAULT_COUNT = 10;

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final ReviewLikeStorage reviewLikeStorage;
    private final FeedStorage feedStorage;

    public Collection<Review> getReviewAll() {
        return reviewStorage.getAll();
    }

    public Collection<Review> getAllByFilmId(Optional<Long> filmId, Optional<Integer> count) {
        return reviewStorage.getAllByFilmId(filmId.orElse(null), count.orElse(DEFAULT_COUNT));
    }

    public Review getReview(Long id) {
        checkReviewId(id);
        Review review = reviewStorage.get(id);
        checkReview(review, id);

        return review;
    }

    public Review addReview(Review review) {
        checkUserId(review.getUserId());
        User user = userStorage.get(review.getUserId());
        checkUser(user, review.getUserId());

        checkFilmId(review.getFilmId());
        Film film = filmStorage.get(review.getFilmId());
        checkFilm(film, review.getFilmId());

        Review reviewAdd = reviewStorage.add(review);
        if (reviewAdd != null) {
            addFeed(reviewAdd.getUserId(), Operation.ADD, reviewAdd.getReviewId());
        }
        return reviewAdd;
    }

    public Review updateReview(Review newReview) {
        checkUserId(newReview.getUserId());
        User user = userStorage.get(newReview.getUserId());
        checkUser(user, newReview.getUserId());

        checkFilmId(newReview.getFilmId());
        Film film = filmStorage.get(newReview.getFilmId());
        checkFilm(film, newReview.getFilmId());

        checkReviewId(newReview.getReviewId());
        Review reviewUpdate = reviewStorage.update(newReview);
        checkReview(reviewUpdate, newReview.getReviewId());

        if (reviewUpdate != null) {
            addFeed(reviewUpdate.getUserId(), Operation.UPDATE, reviewUpdate.getReviewId());
        }
        return reviewUpdate;
    }

    public boolean deleteReview(Long id) {
        checkReviewId(id);
        Review review = reviewStorage.get(id);
        checkReview(review, id);

        Review reviewDelete = reviewStorage.delete(id);
        if (reviewDelete != null) {
            addFeed(reviewDelete.getUserId(), Operation.REMOVE, reviewDelete.getReviewId());
        }

        return reviewDelete != null;
    }

    public boolean addReviewEstimateUseful(Long id, Long userId) {
        return addReviewEstimate(id, userId, REVIEW_ESTIMATE_USEFUL);
    }

    public boolean addReviewEstimateUseless(Long id, Long userId) {
        return addReviewEstimate(id, userId, REVIEW_ESTIMATE_USELESS);
    }

    public boolean deleteReviewEstimate(Long id, Long userId) {
        checkReviewId(id);
        Review review = reviewStorage.get(id);
        checkReview(review, id);

        checkUserId(userId);
        User user = userStorage.get(userId);
        checkUser(user, userId);

        return reviewLikeStorage.delete(id, userId) != null;
    }

    protected boolean addReviewEstimate(Long id, Long userId, Byte estimate) {
        checkReviewId(id);
        Review review = reviewStorage.get(id);
        checkReview(review, id);

        checkUserId(userId);
        User user = userStorage.get(userId);
        checkUser(user, userId);

        ReviewLike reviewLike = reviewLikeStorage.get(id, userId);
        if (reviewLike != null) {
            reviewLike.setUseful(estimate);
            return reviewLikeStorage.update(reviewLike) != null;
        } else {
            reviewLike = new ReviewLike();
            reviewLike.setReviewId(id);
            reviewLike.setUserId(userId);
            reviewLike.setUseful(estimate);
            return reviewLikeStorage.add(reviewLike) != null;
        }
    }

    protected void checkReviewId(Long id) {
        if (id == null) {
            throw new ValidationException("Не задан id отзыва");
        }
    }

    protected void checkUserId(Long id) {
        if (id == null) {
            throw new ValidationException("Не задан id пользователя");
        }
    }

    protected void checkFilmId(Long id) {
        if (id == null) {
            throw new ValidationException("Не задан id фильма");
        }
    }

    protected void checkReview(Review review, Long id) {
        if (review == null) {
            throw new NotFoundException(String.format("Отзыв с id = %d не найден", id));
        }
    }

    protected void checkUser(User user, Long id) {
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
    }

    protected void checkFilm(Film film, Long id) {
        if (film == null) {
            throw new NotFoundException(String.format("Фильм с id = %d не найден", id));
        }
    }

    protected void addFeed(Long userId, Operation operation, Long entityId) {
        Feed feed = new Feed();
        feed.setTimestamp(Timestamp.from(Instant.now()).getTime());
        feed.setUserId(userId);
        feed.setEventType(EventType.REVIEW);
        feed.setOperation(operation);
        feed.setEntityId(entityId);
        feedStorage.add(feed);
    }
}
