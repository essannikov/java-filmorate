package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.util.Collection;

public interface ReviewLikeStorage {
    public Collection<ReviewLike> getAll();

    public ReviewLike get(Long reviewId, Long userId);

    public ReviewLike add(ReviewLike reviewLike);

    public ReviewLike update(ReviewLike newReviewLike);

    public ReviewLike delete(Long reviewId, Long userId);

    public boolean deleteAll();
}
