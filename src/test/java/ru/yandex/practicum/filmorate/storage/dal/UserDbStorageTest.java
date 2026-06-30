package ru.yandex.practicum.filmorate.storage.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    private final UserDbStorage userStorage;
    private User user;

    @BeforeEach
    public void beforeEach() {
        userStorage.deleteAll();

        user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000,1,1));
    }

    @Test
    public void testCreateUser() {
        user = userStorage.add(user);
        assertNotNull(user.getId(), "User is not created");

        User userDb = userStorage.get(user.getId());
        assertNotNull(userDb, "User not found");
        assertEquals(user, userDb, "User data incorrect");
    }

    @Test
    public void testUpdateUser() {
        user = userStorage.add(user);
        assertNotNull(user.getId(), "User is not created");

        user.setEmail("mailNew@mail.ru");
        user.setLogin("loginNew");
        user.setName("nameNew");
        user.setBirthday(LocalDate.of(2001,11,11));
        userStorage.update(user);

        User userDb = userStorage.get(user.getId());
        assertNotNull(userDb, "User not found");
        assertEquals(user, userDb, "User data is incorrect");
    }

    @Test
    public void testDeleteUser() {
        user = userStorage.add(user);
        assertNotNull(user.getId(), "User is not created");

        userStorage.delete(user.getId());

        User userDb = userStorage.get(user.getId());
        assertNull(userDb, "User is not deleted");
    }

    @Test
    public void testFindUserById() {
        user = userStorage.add(user);
        assertNotNull(user.getId(), "User is not created");

        User userDb = userStorage.get(user.getId());
        assertNotNull(userDb, "User not found");
        assertEquals(user, userDb, "User data incorrect");
    }

    @Test
    public void testFindUsers() {
        user = userStorage.add(user);
        assertNotNull(user.getId(), "User is not created");

        List<User> userList = new ArrayList<>();
        userList.add(user);

        List<User> userListDb = userStorage.getAll().stream().toList();

        assertNotNull(userListDb, "Users not found");
        assertThat(userListDb).containsExactlyElementsOf(userList);
    }
}