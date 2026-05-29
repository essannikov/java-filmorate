package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public Collection<User> getAll();

    public User get(Long id);

    public User add(User user);

    public User update(User newUser);

    public User delete(Long id);
}
