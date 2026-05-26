package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.check.OnCreate;
import ru.yandex.practicum.filmorate.check.OnUpdate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userStorage.getAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        if (id == null) {
            throw new ValidationException("Не задан id пользователя");
        }

        User user = userStorage.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        return userStorage.get(id);
    }

    @PostMapping
    public User postUser(@Validated(OnCreate.class) @RequestBody User user) {
        log.info("Post user", user);

        return userStorage.add(user);
    }

    @PutMapping
    public User putUser(@Validated(OnUpdate.class) @RequestBody User newUser) {
        log.info("Put user", newUser);

        User modifyUser = userStorage.modify(newUser);
        if (modifyUser == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", newUser.getId()));
        }

        return modifyUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public boolean putFriend(@PathVariable Long id,
                             @PathVariable Long friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public boolean deleteFriend(@PathVariable Long id,
                                @PathVariable Long friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id,
                                       @PathVariable Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
