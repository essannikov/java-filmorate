package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;

import java.util.Collection;

@Repository
public class ReviewLikeDbStorage extends BaseDbStorage<ReviewLike> implements ReviewLikeStorage {
    private static final String FIND_ALL_QUERY =
            "SELECT * FROM review_like";
    private static final String FIND_BY_ID_QUERY =
            "SELECT * FROM review_like WHERE review_id = ? AND user_id = ?";
    private static final String INSERT_QUERY =
            "INSERT INTO review_like(review_id, user_id, useful) " +
                    "VALUES (?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE review_like SET useful = ? " +
                    "WHERE review_id = ? AND user_id = ?";
    private static final String DELETE_QUERY =
            "DELETE FROM review_like WHERE review_id = ? AND user_id = ?";
    private static final String DELETE_QUERY_ALL =
            "DELETE FROM review_like";

    public ReviewLikeDbStorage(JdbcTemplate jdbc, RowMapper<ReviewLike> mapper) {
        super(jdbc, mapper, ReviewLike.class);
    }

    @Override
    public Collection<ReviewLike> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public ReviewLike get(Long reviewId, Long userId) {
        return findOne(FIND_BY_ID_QUERY, reviewId, userId).orElse(null);
    }

    @Override
    public ReviewLike add(ReviewLike reviewLike) {
        if (update(INSERT_QUERY, reviewLike.getReviewId(), reviewLike.getUserId(), reviewLike.getUseful())) {
            return reviewLike;
        }
        return null;
    }

    public ReviewLike update(ReviewLike newReviewLike) {
        if (update(UPDATE_QUERY,
                newReviewLike.getUseful(), newReviewLike.getReviewId(), newReviewLike.getUserId())) {
            return newReviewLike;
        }
        return null;
    }

    @Override
    public ReviewLike delete(Long reviewId, Long userId) {
        ReviewLike reviewLikeDelete = get(reviewId, userId);
        if (update(DELETE_QUERY, reviewId, userId)) {
            return reviewLikeDelete;
        }
        return null;
    }

    @Override
    public boolean deleteAll() {
        return deleteAll(DELETE_QUERY_ALL);
    }
}
