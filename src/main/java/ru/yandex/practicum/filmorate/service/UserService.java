package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public Collection<User> getUserAll() {
        return userStorage.getAll();
    }

    public User getUser(Long id) {
        checkUserId(id);
        User user = userStorage.get(id);
        checkUser(user, id);

        return userStorage.get(id);
    }

    public User addUser(User user) {
        return userStorage.add(user);
    }

    public User updateUser(User newUser) {
        checkUserId(newUser.getId());
        User userUpdate = userStorage.update(newUser);
        checkUser(userUpdate, newUser.getId());

        return userUpdate;
    }

    public boolean addFriend(Long id, Long friendId) {
        checkUserId(id);
        checkUserId(friendId);

        User user = userStorage.get(id);
        User friendUser = userStorage.get(friendId);

        checkUser(user, id);
        checkUser(friendUser, friendId);

        Friend friend = new Friend();
        friend.setUserId(id);
        friend.setFriendId(friendId);
        return friendStorage.add(friend) != null;
    }

    public boolean deleteFriend(Long id, Long friendId) {
        checkUserId(id);
        checkUserId(friendId);

        User user = userStorage.get(id);
        User friendUser = userStorage.get(friendId);

        checkUser(user, id);
        checkUser(friendUser, friendId);

        return friendStorage.delete(id, friendId) != null;
    }

    public Collection<User> getFriends(Long id) {
        checkUserId(id);
        User user = userStorage.get(id);
        checkUser(user, id);

        return userStorage.getAllInRange(
                friendStorage.getAll(id).stream().map(Friend::getFriendId).collect(Collectors.toSet())
        );
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        checkUserId(id);
        checkUserId(otherId);

        User user = userStorage.get(id);
        User otherUser = userStorage.get(otherId);

        checkUser(user, id);
        checkUser(otherUser, otherId);

        Set<User> userFriends = new HashSet<>(userStorage.getAllInRange(
                friendStorage.getAll(id).stream().map(Friend::getFriendId).collect(Collectors.toSet())));
        Set<User> otherUserFriends = new HashSet<>(userStorage.getAllInRange(
                friendStorage.getAll(otherId).stream().map(Friend::getFriendId).collect(Collectors.toSet())));

        userFriends.retainAll(otherUserFriends);

        return userFriends;
    }

    protected void checkUserId(Long id) {
        if (id == null) {
            throw new ValidationException("Не задан id пользователя");
        }
    }

    protected void checkUser(User user, Long id) {
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
    }
}