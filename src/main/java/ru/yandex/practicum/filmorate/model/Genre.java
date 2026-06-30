package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.check.OnUpdate;

@Data
public class Genre {
    @NotNull(groups = {OnUpdate.class})
    private final Long id;
    private String name;
}