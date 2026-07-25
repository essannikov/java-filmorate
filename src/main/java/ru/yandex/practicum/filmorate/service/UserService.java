package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final FeedStorage feedStorage;

    public Collection<User> getUserAll() {
        return userStorage.getAll();
    }

    public User getUser(Long id) {
        checkUserId(id);
        User user = userStorage.get(id);
        checkUser(user, id);

        return user;
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

    @Transactional
    public User deleteUser(Long id) {
        checkUserId(id);
        User user = userStorage.get(id);
        checkUser(user, id);

        friendStorage.deleteFriends(id);

        User deletedUser = userStorage.delete(id);
        if (deletedUser == null) {
            throw new NotFoundException(String.format("Не удалось удалить пользователя с id = %d", id));
        }

        return deletedUser;
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
        if (friendStorage.add(friend) == null) {
            return false;
        }

        addFeed(id, Operation.ADD, friendId);
        return true;
    }

    public boolean deleteFriend(Long id, Long friendId) {
        checkUserId(id);
        checkUserId(friendId);
        User user = userStorage.get(id);
        User friendUser = userStorage.get(friendId);
        checkUser(user, id);
        checkUser(friendUser, friendId);

        if (friendStorage.delete(id, friendId) == null) {
            return false;
        }

        addFeed(id, Operation.REMOVE, friendId);
        return true;
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

    public Collection<Feed> getFeeds(Long userId) {
        return feedStorage.getAllByUserId(userId);
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

    protected void addFeed(Long userId, Operation operation, Long entityId) {
        Feed feed = new Feed();
        feed.setTimestamp(Timestamp.from(Instant.now()).getTime());
        feed.setUserId(userId);
        feed.setEventType(EventType.FRIEND);
        feed.setOperation(operation);
        feed.setEntityId(entityId);
        feedStorage.add(feed);
    }
}