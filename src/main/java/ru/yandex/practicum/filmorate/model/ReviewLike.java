package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewLike {
    @NotNull
    private Long reviewId;
    @NotNull
    private Long userId;
    @NotNull
    private Byte useful;
}
