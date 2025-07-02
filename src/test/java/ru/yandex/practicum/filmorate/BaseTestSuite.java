package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseTestSuite {

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected Validator validator;

    protected Set<ConstraintViolation<User>> userViolations;
    protected Set<ConstraintViolation<Film>> filmViolations;

    @LocalServerPort
    protected int port;

    protected User user;
    protected User anotherUser;

    protected Film film;
    protected Film anotherFilm;


    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @AfterEach
    void tearDown() {
        restTemplate.delete(getUsersUrl());
        restTemplate.delete(getFilmsUrl());
    }


    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }

    protected String getUsersUrl() {
        return getBaseUrl() + "/users";
    }

    protected String getFilmsUrl() {
        return getBaseUrl() + "/films";
    }

    protected <T> Set<ConstraintViolation<T>> validate(T object) {
        return validator.validate(object);
    }
}
