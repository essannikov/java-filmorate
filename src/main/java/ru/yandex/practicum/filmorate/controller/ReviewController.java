package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.check.OnCreate;
import ru.yandex.practicum.filmorate.check.OnUpdate;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{id}")
    public Review getReview(@PathVariable Long id) {
        return reviewService.getReview(id);
    }

    @GetMapping
    public Collection<Review> findAllByFilmId(@RequestParam Optional<Long> filmId, @RequestParam Optional<Integer> count) {
        return reviewService.getAllByFilmId(filmId, count);
    }

    @PostMapping
    public Review postReview(@Validated(OnCreate.class) @RequestBody Review review) {
        log.info("Post review", review);
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review putReview(@Validated(OnUpdate.class) @RequestBody Review newReview) {
        log.info("Put review", newReview);
        return reviewService.updateReview(newReview);
    }

    @DeleteMapping("/{id}")
    public boolean deleteReview(@PathVariable Long id) {
        return reviewService.deleteReview(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean putReviewLike(@Validated(OnUpdate.class) @PathVariable Long id, @PathVariable Long userId) {
        return reviewService.addReviewEstimateUseful(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public boolean putReviewDislike(@Validated(OnUpdate.class) @PathVariable Long id, @PathVariable Long userId) {
        return reviewService.addReviewEstimateUseless(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteReviewLike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.deleteReviewEstimate(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public boolean deleteReviewDislike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.deleteReviewEstimate(id, userId);
    }
}
