package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.BaseTestSuite;
import ru.yandex.practicum.filmorate.test_data.UserTestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest extends BaseTestSuite {

    private User user;

    @Test
    void shouldPassWhenAllFieldsAreValid() {
        user = UserTestData.validUserWithAllFieldsFilled();
        userViolations = validate(user);
        assertEquals(0, userViolations.size());
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsEmpty() {
        user = UserTestData.validUserWithoutName();
        userViolations = validate(user);
        assertEquals(0, userViolations.size());
    }

    @Test
    void shouldFailWhenEmailIsEmpty() {
        user = UserTestData.invalidUserWithEmptyEmail();
        userViolations = validate(user);
        assertEquals(1, userViolations.size());
        assertTrue(userViolations.iterator().next().getMessage().contains("Email не может быть пустым"));
    }

    @Test
    void shouldFailWhenInvalidEmailFormat() {
        user = UserTestData.invalidUserWithInvalidEmailFormat();
        userViolations = validate(user);
        assertEquals(1, userViolations.size());
        assertTrue(userViolations.iterator().next().getMessage().contains("Email должен содержать символ '@'"));
    }

    @Test
    void shouldFailWhenEmailContainsOnlyAtSymbol() {
        user = UserTestData.invalidUserWithAtSymbolOnlyEmail();
        userViolations = validate(user);
        assertEquals(1, userViolations.size());
        assertTrue(userViolations.iterator().next().getMessage().contains("Email должен содержать символ '@'"));
    }

    @Test
    void shouldFailWhenLoginIsEmpty() {
        user = UserTestData.invalidUserWithEmptyLogin();
        userViolations = validate(user);
        assertEquals(1, userViolations.size());
        assertTrue(userViolations.iterator().next().getMessage()
                .contains("Логин не может быть пустым или содержать пробелы"));
    }

    @Test
    void shouldFailWhenLoginContainsSpaces() {
        user = UserTestData.invalidUserWithWhitespaceInLogin();
        userViolations = validate(user);
        assertEquals(1, userViolations.size());
        assertTrue(userViolations.iterator().next().getMessage()
                .contains("Логин не может быть пустым или содержать пробелы"));
    }

    @Test
    void shouldFailWhenBirthdayIsNull() {
        user = UserTestData.invalidUserWithNullBirthday();
        userViolations = validate(user);
        assertEquals(1, userViolations.size());
        assertTrue(userViolations.iterator().next().getMessage().contains("Дата рождения не может быть пустой"));
    }

    @Test
    void shouldFailWhenBirthdayIsInFuture() {
        user = UserTestData.invalidUserWithFutureBirthday();
        userViolations = validate(user);
        assertEquals(1, userViolations.size());
        assertTrue(userViolations.iterator().next().getMessage().contains("Дата рождения не может быть в будущем"));
    }
}