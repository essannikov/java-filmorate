package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.check.OnCreate;
import ru.yandex.practicum.filmorate.check.OnUpdate;

@Data
public class Director {
    @NotNull(groups = {OnUpdate.class})
    private Long id;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    private String name;
}