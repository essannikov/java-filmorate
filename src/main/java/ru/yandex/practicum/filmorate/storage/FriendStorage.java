package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friend;

import java.util.Collection;

public interface FriendStorage {
    public Collection<Friend> getAll(Long userId);

    public Friend get(Long userId, Long friendId);

    public Friend add(Friend friend);

    public Friend delete(Long userId, Long friendId);

    public boolean deleteAll();
}