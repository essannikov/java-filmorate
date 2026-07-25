package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.check.OnCreate;
import ru.yandex.practicum.filmorate.check.OnUpdate;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.time.LocalDateTime;

@Data
public class Feed {
    @NotNull(groups = {OnUpdate.class})
    private Long eventId;
    @NotNull(groups = {OnCreate.class, OnUpdate.class})
    private Long timestamp;
    @NotNull(groups = {OnCreate.class, OnUpdate.class})
    private Long userId;
    @NotNull(groups = {OnCreate.class, OnUpdate.class})
    private EventType eventType;
    @NotNull(groups = {OnCreate.class, OnUpdate.class})
    private Operation operation;
    @NotNull(groups = {OnCreate.class, OnUpdate.class})
    private Long entityId;
}
