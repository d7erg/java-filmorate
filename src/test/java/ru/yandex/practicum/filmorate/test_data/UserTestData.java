package ru.yandex.practicum.filmorate.test_data;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserTestData {

    // Валидные данные
    public static User validUserWithAllFieldsFilled() {
        return User.builder()
                .id(1L)
                .email("valid@example.com")
                .login("valid_login")
                .name("Valid User")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
    }

    public static User anotherValidUser() {
        return User.builder()
                .email("friend3@example.com")
                .login("friend3_login")
                .name("Friend Three") // если требуется
                .birthday(LocalDate.of(1990, 1, 1)) // если требуется
                .build();
    }

    public static User validUserWithoutName() {
        return User.builder()
                .email("noname@example.com")
                .login("no_name_login")
                .birthday(LocalDate.now().minusDays(1))
                .build();
    }

    // Невалидные данные
    public static User invalidUserWithEmptyEmail() {
        return User.builder()
                .email("")
                .login("empty_email")
                .birthday(LocalDate.of(2010, 1, 1))
                .build();
    }

    public static User invalidUserWithInvalidEmailFormat() {
        return User.builder()
                .email("invalid-email")
                .login("invalid_email")
                .birthday(LocalDate.of(2005, 5, 5))
                .build();
    }

    public static User invalidUserWithAtSymbolOnlyEmail() {
        return User.builder()
                .email("@")
                .login("invalid_email")
                .birthday(LocalDate.of(2005, 5, 5))
                .build();
    }

    public static User invalidUserWithEmptyLogin() {
        return User.builder()
                .email("empty@login.com")
                .login("")
                .birthday(LocalDate.of(1999, 12, 31))
                .build();
    }

    public static User invalidUserWithWhitespaceInLogin() {
        return User.builder()
                .email("whitespace@login.com")
                .login("login with space")
                .birthday(LocalDate.of(1980, 7, 15))
                .build();
    }

    public static User invalidUserWithNullBirthday() {
        return User.builder()
                .email("null@birthday.com")
                .login("null_birthday")
                .birthday(null)
                .build();
    }

    public static User invalidUserWithFutureBirthday() {
        return User.builder()
                .email("future@birthday.com")
                .login("future_birthday")
                .birthday(LocalDate.now().plusDays(1))
                .build();
    }


    // Данные для обновления
    public static User userForUpdate(Long existingId) {
        return User.builder()
                .id(existingId)
                .email("updated@example.com")
                .login("updated_login")
                .name("Updated Name")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();
    }


    public static User userWithNonExistingId() {
        return User.builder()
                .id(999L)
                .email("non@existing.com")
                .login("non_existing")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
    }

    public static User userWithoutIdForUpdate() {
        return User.builder()
                .email("without@id.com")
                .login("without_id")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
    }
}

