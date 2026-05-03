package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.check.FilmReleaseDate;
import ru.yandex.practicum.filmorate.check.OnCreate;
import ru.yandex.practicum.filmorate.check.OnUpdate;

import java.time.LocalDate;

@Data
public class Film {
    @NotNull(groups = {OnUpdate.class})
    private Long id;
    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    private String name;
    @Size(min = 1, max = 200, groups = {OnCreate.class, OnUpdate.class})
    private String description;
    @FilmReleaseDate(groups = {OnCreate.class, OnUpdate.class})
    private LocalDate releaseDate;
    @Positive(groups = {OnCreate.class, OnUpdate.class})
    private Long duration;
}
