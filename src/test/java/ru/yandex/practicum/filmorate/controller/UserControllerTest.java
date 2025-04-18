package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.BaseTestSuite;
import ru.yandex.practicum.filmorate.controller.test_data.UserTestData;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest extends BaseTestSuite {

    private User user;
    private User anotherUser;
    private final UserController userController = new UserController();

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

    /**
     * Проверяет, что каждому пользователю присваивается уникальный ID
     */

    @Test
    void shouldAssignSequentialUniqueIdsToNewUsers() {
        Collection<User> existingUsers = userController.getAllUsers();
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

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getStatusCode().value());
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
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
