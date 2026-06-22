package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class InMemoryFriendStorage implements FriendStorage {
    private final InMemoryUserStorage userStorage;

    @Override
    public Collection<Friend> getAll(Long userId) {
        return userStorage.get(userId).getFriends().stream().map(friendId -> get(userId, friendId)).toList();
    }

    @Override
    public Friend get(Long userId, Long friendId) {
        if (userStorage.get(userId).getFriends().contains(friendId)) {
            Friend friend = new Friend();
            friend.setUserId(userId);
            friend.setFriendId(friendId);
            return friend;
        }
        return null;
    }

    @Override
    public Friend add(Friend friend) {
        userStorage.get(friend.getUserId()).getFriends().add(friend.getFriendId());
        return friend;
    }

    @Override
    public Friend delete(Long userId, Long friendId) {
        userStorage.get(userId).getFriends().remove(friendId);
        Friend friend = new Friend();
        friend.setUserId(userId);
        friend.setFriendId(friendId);
        return friend;
    }

    @Override
    public boolean deleteAll() {
        userStorage.getAll().forEach(user -> user.getFriends().clear());
        return true;
    }
}
