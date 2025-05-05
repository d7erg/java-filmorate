package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.BaseTestSuite;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.test_data.UserTestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest extends BaseTestSuite {

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        user = UserTestData.validUserWithAllFieldsFilled();
        anotherUser = UserTestData.validUserWithoutName();

        userService.create(user);
        userService.create(anotherUser);
    }

    @Test
    void shouldAddFriend() {
        userService.addFriend(user.getId(), anotherUser.getId());

        assertEquals(1, userService.getUserFriends(user.getId()).size());
        assertTrue(userService.getUserFriends(user.getId()).contains(anotherUser));

        assertEquals(1, userService.getUserFriends(anotherUser.getId()).size());
        assertTrue(userService.getUserFriends(anotherUser.getId()).contains(user));
    }

    @Test
    void shouldRemoveFriend() {
        userService.addFriend(user.getId(), anotherUser.getId());
        userService.removeFriend(user.getId(), anotherUser.getId());

        // Проверяем, что друзья удалены с обеих сторон
        assertTrue(userService.getUserFriends(user.getId()).isEmpty());
        assertTrue(userService.getUserFriends(anotherUser.getId()).isEmpty());
    }

    @Test
    public void shouldGetCommonFriends() {

        User thirdUser = UserTestData.anotherValidUser();

        userService.create(thirdUser);

        userService.addFriend(user.getId(), anotherUser.getId());
        userService.addFriend(anotherUser.getId(), user.getId());
        userService.addFriend(user.getId(), thirdUser.getId());
        userService.addFriend(anotherUser.getId(), thirdUser.getId());
        userService.addFriend(thirdUser.getId(), user.getId());
        userService.addFriend(thirdUser.getId(), anotherUser.getId());

        List<User> commonFriends = userService.getCommonFriends(user.getId(), anotherUser.getId());
        assertEquals(1, commonFriends.size());
        assertTrue(commonFriends.contains(thirdUser));
    }

    @Test
    void shouldThrowExceptionOnAddFriendForNonExistingUser() {
        assertThrows(NotFoundException.class, () ->
                userService.addFriend(100L, anotherUser.getId())
        );
    }

}