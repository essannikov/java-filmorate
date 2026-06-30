package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendDbStorageTest {
    private final UserDbStorage userStorage;
    private final FriendDbStorage friendStorage;
    private User user1;
    private User user2;
    private Friend friend;

    @BeforeEach
    public void beforeEach() {
        userStorage.deleteAll();
        friendStorage.deleteAll();

        user1 = new User();
        user1.setEmail("mail@mail.ru");
        user1.setLogin("login");
        user1.setName("name");
        user1.setBirthday(LocalDate.of(2000,1,1));
        userStorage.add(user1);

        user2 = new User();
        user2.setEmail("mailTest@mail.ru");
        user2.setLogin("loginTest");
        user2.setName("nameTest");
        user2.setBirthday(LocalDate.of(2001,11,11));
        userStorage.add(user2);

        friend = new Friend();
        friend.setUserId(user1.getId());
        friend.setFriendId(user2.getId());
    }

    @Test
    public void testCreateFriend() {
        friendStorage.add(friend);

        Friend friendDb = friendStorage.get(user1.getId(), user2.getId());
        assertNotNull(friendDb, "Friend not found");
        assertEquals(friend, friendDb, "Friend data incorrect");
    }

    @Test
    public void testDeleteFriend() {
        friendStorage.add(friend);

        Friend friendDb = friendStorage.get(user1.getId(), user2.getId());
        assertNotNull(friendDb, "Friend not found");
        assertEquals(friend, friendDb, "Friend data incorrect");

        friendStorage.delete(user1.getId(),user2.getId());

        friendDb = friendStorage.get(user1.getId(), user2.getId());
        assertNull(friendDb, "Friend is not deleted");
    }

    @Test
    public void testFindUsers() {
        friendStorage.add(friend);

        Friend friendDb = friendStorage.get(user1.getId(), user2.getId());
        assertNotNull(friendDb, "Friend not found");
        assertEquals(friend, friendDb, "Friend data incorrect");

        List<Friend> friendList = new ArrayList<>();
        friendList.add(friend);

        List<Friend> friendListDb = friendStorage.getAll(user1.getId()).stream().toList();

        assertNotNull(friendListDb, "Friends not found");
        assertThat(friendListDb).containsExactlyElementsOf(friendList);
    }
}
