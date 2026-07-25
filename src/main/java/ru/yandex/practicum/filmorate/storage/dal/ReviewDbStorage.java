package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.Collection;

@Repository
public class ReviewDbStorage extends BaseDbStorage<Review> implements ReviewStorage {
    private static final String FIND_BY_ID_QUERY =
            "SELECT r.ID, r.CONTENT, r.IS_POSITIVE, r.USER_ID, r.FILM_ID, " +
                    "COALESCE(SUM(rl.USEFUL), 0) AS useful " +
                    "FROM reviews AS r " +
                    "LEFT OUTER JOIN review_like AS rl ON rl.REVIEW_ID = r.ID " +
                    "WHERE r.ID = ? " +
                    "GROUP BY r.ID, r.CONTENT, r.IS_POSITIVE, r.USER_ID, r.FILM_ID ";
    private static final String FIND_ALL_QUERY =
            "SELECT r.ID, r.CONTENT, r.IS_POSITIVE, r.USER_ID, r.FILM_ID, " +
                    "COALESCE(SUM(rl.USEFUL), 0) AS useful " +
                    "FROM reviews AS r " +
                    "LEFT OUTER JOIN review_like AS rl ON rl.REVIEW_ID = r.ID " +
                    "GROUP BY r.ID, r.CONTENT, r.IS_POSITIVE, r.USER_ID, r.FILM_ID " +
                    "ORDER BY SUM(rl.USEFUL) DESC ";
    private static final String FIND_ALL_BY_FILM_ID =
            "SELECT r.ID, r.CONTENT, r.IS_POSITIVE, r.USER_ID, r.FILM_ID, " +
                    "COALESCE(SUM(rl.USEFUL), 0) AS useful " +
                    "FROM reviews AS r " +
                    "LEFT OUTER JOIN review_like AS rl ON rl.REVIEW_ID = r.ID " +
                    "WHERE (? IS NULL OR r.FILM_ID = ?) " +
                    "GROUP BY r.ID, r.CONTENT, r.IS_POSITIVE, r.USER_ID, r.FILM_ID " +
                    "ORDER BY SUM(rl.USEFUL) DESC " +
                    "LIMIT ?";
    private static final String INSERT_QUERY =
            "INSERT INTO reviews(content, is_positive, user_id, film_id) " +
                    "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE reviews SET content = ?, is_positive = ?, user_id = ?, film_id = ? " +
                    "WHERE id = ?";
    private static final String DELETE_QUERY =
            "DELETE FROM reviews WHERE id = ?";
    private static final String DELETE_QUERY_ALL =
            "DELETE FROM reviews";

    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper, Review.class);
    }

    @Override
    public Collection<Review> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Collection<Review> getAllByFilmId(Long filmId, Integer count) {
        return findMany(FIND_ALL_BY_FILM_ID, filmId, filmId, count);
    }

    @Override
    public Review get(Long id) {
        return findOne(FIND_BY_ID_QUERY, id).orElse(null);
    }

    @Override
    public Review add(Review review) {
        Long id = insert(INSERT_QUERY,
                review.getContent(), review.getIsPositive(), review.getUserId(), review.getFilmId());
        review.setReviewId(id);
        return review;
    }

    @Override
    public Review update(Review newReview) {
        if (update(UPDATE_QUERY,
                newReview.getContent(), newReview.getIsPositive(), newReview.getUserId(), newReview.getFilmId(),
                newReview.getReviewId())) {
            return get(newReview.getReviewId());
        }
        return null;
    }

    @Override
    public Review delete(Long id) {
        Review reviewDelete = get(id);
        if (delete(DELETE_QUERY, id)) {
            return reviewDelete;
        }
        return null;
    }

    @Override
    public boolean deleteAll() {
        return deleteAll(DELETE_QUERY_ALL);
    }
}
