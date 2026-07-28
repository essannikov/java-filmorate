package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {
    public Collection<Review> getAll();

    public Collection<Review> getAllByFilmId(Long filmId, Integer count);

    public Review getByFilmIdUserId(Long filmId, Long userId);

    public Review get(Long id);

    public Review add(Review review);

    public Review update(Review newReview);

    public Review delete(Long id);

    public boolean deleteAll();
}
