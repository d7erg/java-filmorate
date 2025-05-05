package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.BaseTestSuite;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.test_data.UserTestData;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryUserStorageTest extends BaseTestSuite {

    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        user = UserTestData.validUserWithAllFieldsFilled();
        anotherUser = UserTestData.validUserWithoutName();

        userStorage.create(user);
        userStorage.create(anotherUser);
    }

    @Test
    public void shouldCreateUser() {
        User createdUser = userStorage.getUser(user.getId());
        assertNotNull(createdUser);
        assertEquals(user, createdUser);
    }

    @Test
    public void shouldGetAllUsers() {
        List<User> allUsers = userStorage.getUsers();

        assertEquals(2, allUsers.size());
        assertTrue(allUsers.contains(user));
        assertTrue(allUsers.contains(anotherUser));
    }

    @Test
    public void shouldGetUserById() {
        assertNotNull(userStorage.getUser(user.getId()));
    }

    @Test
    public void shouldUpdateUser() {

        User updatedUser = User.builder()
                .id(user.getId())
                .email("updated@example.com")
                .login("updated_login")
                .name("Updated Name")
                .birthday(LocalDate.of(2001, 1, 1))
                .build();

        userStorage.update(updatedUser);

        User resultUser = userStorage.getUser(user.getId());
        assertEquals("updated@example.com", resultUser.getEmail());
        assertEquals("updated_login", resultUser.getLogin());
        assertEquals("Updated Name", resultUser.getName());
        assertEquals(LocalDate.of(2001, 1, 1), resultUser.getBirthday());
    }

    @Test
    public void shouldDeleteUser() {
        userStorage.deleteUsers();
        assertNull(userStorage.getUser(user.getId()));
    }

    @Test
    public void shouldAutoGenerateIdOnCreate() {
        User newUser = User.builder()
                .id(user.getId())
                .email("updated@example.com")
                .login("updated_login")
                .name("Updated Name")
                .birthday(LocalDate.of(2001, 1, 1))
                .build();

        userStorage.create(newUser);
        assertNotNull(newUser.getId());
        assertTrue(newUser.getId() > 0);
    }

}