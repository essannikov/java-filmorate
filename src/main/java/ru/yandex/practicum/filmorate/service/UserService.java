package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public boolean addFriend(Long id, Long friendId) {
        User user = userStorage.get(id);
        User friendUser = userStorage.get(friendId);

        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
        if (friendUser == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", friendId));
        }

        return user.getFriends().add(friendId) && friendUser.getFriends().add(id);
    }

    public boolean deleteFriend(Long id, Long friendId) {
        User user = userStorage.get(id);
        User friendUser = userStorage.get(friendId);

        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
        if (friendUser == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", friendId));
        }

        return user.getFriends().remove(friendId) && friendUser.getFriends().remove(id);
    }

    public List<User> getFriends(Long id) {
        User user = userStorage.get(id);

        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }

        return user.getFriends().stream().map(userStorage::get).toList();
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        User user = userStorage.get(id);
        User otherUser = userStorage.get(otherId);

        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
        if (otherUser == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", otherId));
        }

        List<User> userFriendsList = new ArrayList<>(user.getFriends().stream().map(userStorage::get).toList());
        List<User> otherUserFriendsList = otherUser.getFriends().stream().map(userStorage::get).toList();
        userFriendsList.retainAll(otherUserFriendsList);

        return userFriendsList;
    }
}