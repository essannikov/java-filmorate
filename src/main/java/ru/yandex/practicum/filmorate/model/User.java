package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import ru.yandex.practicum.filmorate.check.OnCreate;
import ru.yandex.practicum.filmorate.check.OnUpdate;

import java.time.LocalDate;

@Data
public class User {
    @NotNull(groups = {OnUpdate.class})
    private Long id;
    @Email(groups = {OnCreate.class, OnUpdate.class})
    private String email;
    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    private String login;
    private String name;
    @Past(groups = {OnCreate.class, OnUpdate.class})
    private LocalDate birthday;
}
