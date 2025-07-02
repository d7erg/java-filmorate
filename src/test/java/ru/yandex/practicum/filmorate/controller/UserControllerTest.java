package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.BaseTestSuite;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.test_data.UserTestData;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class UserControllerTest extends BaseTestSuite {

    @Autowired
    private UserController userController;


    @Test
    void shouldCreateUserWithValidData() {
        user = UserTestData.validUserWithAllFieldsFilled();
        ResponseEntity<User> response = restTemplate.postForEntity(getUsersUrl(), user, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotEquals(
                null,
                Objects.requireNonNull(response.getBody()).getId(),
                "ID пользователя не должен быть null"
        );

        assertEquals(user.getEmail(), response.getBody().getEmail());
        assertEquals(user.getLogin(), response.getBody().getLogin());
        assertEquals(user.getBirthday(), response.getBody().getBirthday());

        User createdUser = response.getBody();

        ResponseEntity<Collection<User>> getAllResponse = restTemplate.exchange(
                getUsersUrl(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        Collection<User> allUsers = getAllResponse.getBody();
        assertTrue(Objects.requireNonNull(allUsers)
                .stream().anyMatch(u -> u.getId().equals(createdUser.getId())));
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsEmpty() {
        user = UserTestData.validUserWithoutName();
        ResponseEntity<User> response = restTemplate.postForEntity(getUsersUrl(), user, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(
                user.getLogin(),
                Objects.requireNonNull(response.getBody()).getName(),
                "Имя должно совпадать с логином, если не задано"
        );
    }

    @Test
    void shouldUpdateExistingUserSuccessfully() {
        user = UserTestData.validUserWithAllFieldsFilled();
        ResponseEntity<User> createResponse = restTemplate.postForEntity(getUsersUrl(), user, User.class);
        log.info(String.valueOf(createResponse));

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        User updatedUser = UserTestData.userForUpdate(user.getId());
        HttpEntity<User> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<User> updateResponse = restTemplate.exchange(
                getUsersUrl(), HttpMethod.PUT, requestEntity, User.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals(user.getId(), updateResponse.getBody().getId());
        assertNotEquals(user.getEmail(), updateResponse.getBody().getEmail());
        assertNotEquals(user.getLogin(), updateResponse.getBody().getLogin());
        assertNotEquals(user.getName(), updateResponse.getBody().getName());
    }


    @Test
    void shouldAssignSequentialUniqueIdsToNewUsers() {
        Collection<User> existingUsers = userController.getUsers();
        assertTrue(existingUsers.isEmpty(), "Перед тестом должно быть 0 пользователей");

        user = UserTestData.validUserWithAllFieldsFilled();
        anotherUser = UserTestData.validUserWithoutName();

        ResponseEntity<User> response1 = restTemplate.postForEntity(getUsersUrl(), user, User.class);
        ResponseEntity<User> response2 = restTemplate.postForEntity(getUsersUrl(), anotherUser, User.class);

        assertEquals(HttpStatus.CREATED, response1.getStatusCode());
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());

        assertNotEquals(
                Objects.requireNonNull(response1.getBody()).getId(),
                Objects.requireNonNull(response2.getBody()).getId(),
                "ID пользователей должны быть уникальными"
        );
        assertEquals(
                response1.getBody().getId() + 1,
                response2.getBody().getId(),
                "ID второго пользователя должен быть на 1 больше"
        );
    }

    @Test
    void shouldReturnAllUsers() {
        user = UserTestData.validUserWithAllFieldsFilled();
        anotherUser = UserTestData.validUserWithoutName();

        restTemplate.postForEntity(getUsersUrl(), user, User.class);
        restTemplate.postForEntity(getUsersUrl(), anotherUser, User.class);

        ResponseEntity<Collection<User>> response = restTemplate.exchange(
                getUsersUrl(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void shouldReturnServerErrorWhenUpdatingNonExistingUser() {
        user = UserTestData.userWithNonExistingId();
        HttpEntity<User> requestEntity = new HttpEntity<>(user);
        ResponseEntity<String> response = restTemplate.exchange(
                getUsersUrl(), HttpMethod.PUT, requestEntity, String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getStatusCode().value());
    }


    @Test
    void shouldFailUpdateWithoutId() {
        user = UserTestData.userWithoutIdForUpdate();
        ResponseEntity<String> response = restTemplate.exchange(
                getFilmsUrl(),
                HttpMethod.PUT,
                new HttpEntity<>(user),
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test
    void shouldReturnErrorCreationWhenEmailIsInvalid() {
        user = UserTestData.invalidUserWithEmptyEmail();

        ResponseEntity<String> response = restTemplate.postForEntity(getUsersUrl(), user, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void shouldRejectEmptyUserRequest() {
        ResponseEntity<String> response = restTemplate.postForEntity(getUsersUrl(), null, String.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }


    @Test
    void shouldAddFriendSuccessfully() {
        user = UserTestData.validUserWithAllFieldsFilled();
        anotherUser = UserTestData.validUserWithoutName();

        ResponseEntity<User> response1 = restTemplate.postForEntity(getUsersUrl(), user, User.class);
        ResponseEntity<User> response2 = restTemplate.postForEntity(getUsersUrl(), anotherUser, User.class);

        assertNotNull(response1.getBody());
        assertNotNull(response2.getBody());
        assertEquals(HttpStatus.CREATED, response1.getStatusCode());
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());

        ResponseEntity<Void> addFriendResponse = restTemplate.exchange(
                getUsersUrl() + "/" + response1.getBody().getId() + "/friends/" + response2.getBody().getId(),
                HttpMethod.PUT,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, addFriendResponse.getStatusCode());

        ResponseEntity<List<User>> friendsResponse1 = restTemplate.exchange(
                getUsersUrl() + "/" + response1.getBody().getId() + "/friends",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertNotNull(friendsResponse1.getBody());
        assertEquals(1, friendsResponse1.getBody().size());
        assertTrue(friendsResponse1.getBody().stream()
                .anyMatch(friend -> friend.getId().equals(response2.getBody().getId())));

        // Проверка друзей пользователя 2
        ResponseEntity<List<User>> friendsResponse2 = restTemplate.exchange(
                getUsersUrl() + "/" + response2.getBody().getId() + "/friends",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertNotNull(friendsResponse2.getBody());
        assertEquals(1, friendsResponse2.getBody().size());
        assertTrue(friendsResponse2.getBody().stream()
                .anyMatch(friend -> friend.getId().equals(response1.getBody().getId())));
    }

    @Test
    void shouldRemoveFriendSuccessfully() {
        user = UserTestData.validUserWithAllFieldsFilled();
        anotherUser = UserTestData.validUserWithoutName();

        ResponseEntity<User> response1 = restTemplate.postForEntity(getUsersUrl(), user, User.class);
        ResponseEntity<User> response2 = restTemplate.postForEntity(getUsersUrl(), anotherUser, User.class);

        assertNotNull(response1.getBody());
        assertNotNull(response2.getBody());
        restTemplate.exchange(
                getUsersUrl() + "/" + response1.getBody().getId() + "/friends/" + response2.getBody().getId(),
                HttpMethod.PUT,
                null,
                Void.class
        );

        ResponseEntity<Void> removeFriendResponse = restTemplate.exchange(
                getUsersUrl() + "/" + response1.getBody().getId() + "/friends/" + response2.getBody().getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, removeFriendResponse.getStatusCode());

        ResponseEntity<List<User>> friendsResponse = restTemplate.exchange(
                getUsersUrl() + "/" + response1.getBody().getId() + "/friends",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertNotNull(friendsResponse.getBody());
        assertFalse(friendsResponse.getBody().stream()
                .anyMatch(friend -> friend.getId().equals(response2.getBody().getId())));
    }

    @Test
    void shouldGetFriendsSuccessfully() {
        user = UserTestData.validUserWithAllFieldsFilled();
        anotherUser = UserTestData.validUserWithoutName();

        ResponseEntity<User> response1 = restTemplate.postForEntity(getUsersUrl(), user, User.class);
        ResponseEntity<User> response2 = restTemplate.postForEntity(getUsersUrl(), anotherUser, User.class);

        assertNotNull(response1.getBody());
        assertNotNull(response2.getBody());

        restTemplate.exchange(
                getUsersUrl() + "/" + response1.getBody().getId() + "/friends/" + response2.getBody().getId(),
                HttpMethod.PUT,
                null,
                Void.class
        );

        ResponseEntity<List<User>> friendsResponse = restTemplate.exchange(
                getUsersUrl() + "/" + response1.getBody().getId() + "/friends",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, friendsResponse.getStatusCode());
        assertNotNull(friendsResponse.getBody());
        assertEquals(1, friendsResponse.getBody().size());
        assertTrue(friendsResponse.getBody().stream()
                .anyMatch(friend -> friend.getId().equals(response2.getBody().getId())));
    }

    @Test
    void shouldGetCommonFriendsSuccessfully() {
        user = UserTestData.validUserWithAllFieldsFilled();
        anotherUser = UserTestData.validUserWithoutName();
        User thirdUser = UserTestData.anotherValidUser();

        ResponseEntity<User> response1 = restTemplate.postForEntity(getUsersUrl(), user, User.class);
        ResponseEntity<User> response2 = restTemplate.postForEntity(getUsersUrl(), anotherUser, User.class);
        ResponseEntity<User> response3 = restTemplate.postForEntity(getUsersUrl(), thirdUser, User.class);

        assertNotNull(response1.getBody());
        assertNotNull(response2.getBody());
        assertNotNull(response3.getBody());

        restTemplate.exchange(
                getUsersUrl() + "/" + response1.getBody().getId() + "/friends/" + response3.getBody().getId(),
                HttpMethod.PUT,
                null,
                Void.class
        );

        restTemplate.exchange(
                getUsersUrl() + "/" + response2.getBody().getId() + "/friends/" + response3.getBody().getId(),
                HttpMethod.PUT,
                null,
                Void.class
        );

        ResponseEntity<List<User>> commonFriendsResponse = restTemplate.exchange(
                getUsersUrl() + "/" + response1.getBody().getId()
                        + "/friends/common/" + response2.getBody().getId(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, commonFriendsResponse.getStatusCode());
        assertNotNull(commonFriendsResponse.getBody());
        assertEquals(1, commonFriendsResponse.getBody().size());
        assertTrue(commonFriendsResponse.getBody().stream()
                .anyMatch(friend -> friend.getId().equals(response3.getBody().getId())));
    }

}
