package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Friend {
    @NotNull
    private Long userId;
    @NotNull
    private Long friendId;
}
