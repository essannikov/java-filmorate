package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        log.info("Post user", user);

        try {
            return create(user);
        } catch (ValidationException exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }

    public User create(User user) throws ValidationException {
        checkUser(user);

        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void checkUser(User user) throws ValidationException {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User newUser) {
        log.info("Put user", newUser);

        try {
            return update(newUser);
        } catch (ValidationException exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }

    public User update(User newUser) throws ValidationException {
        if (newUser.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        if (users.containsKey(newUser.getId())) {
            checkUser(newUser);

            User oldUser = users.get(newUser.getId());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());

            return oldUser;
        }

        throw new ValidationException("Пользователь с id = " + newUser.getId() + " не найден");
    }
}
