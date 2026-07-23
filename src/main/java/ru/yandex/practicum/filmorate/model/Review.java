package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.check.OnCreate;
import ru.yandex.practicum.filmorate.check.OnUpdate;

@Data
public class Review {
    private Long reviewId;
    @Size(min = 1, max = 1000, groups = {OnCreate.class, OnUpdate.class})
    private String content;
    @NotNull(groups = {OnCreate.class, OnUpdate.class})
    private Boolean isPositive;
    @NotNull(groups = {OnCreate.class, OnUpdate.class})
    private Long userId;
    @NotNull(groups = {OnCreate.class, OnUpdate.class})
    private Long filmId;
    private Integer useful;
}
