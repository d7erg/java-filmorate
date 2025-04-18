package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.BaseTestSuite;
import ru.yandex.practicum.filmorate.controller.test_data.FilmTestData;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest extends BaseTestSuite {

    private Film film;
    private Film anotherFilm;
    private final FilmController filmController = new FilmController();

    @Test
    void shouldCreateFilmWithValidData() {
        film = FilmTestData.validFilmWithAllFieldsFilled();
        ResponseEntity<Film> response = restTemplate.postForEntity(getFilmsUrl(), film, Film.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());

        assertEquals(film.getName(), response.getBody().getName());
        assertEquals(film.getDescription(), response.getBody().getDescription());
        assertEquals(film.getReleaseDate(), response.getBody().getReleaseDate());
        assertEquals(film.getDuration(), response.getBody().getDuration());

        ResponseEntity<Collection<Film>> getAllResponse = restTemplate.exchange(
                getFilmsUrl(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertNotNull(getAllResponse.getBody());
        assertTrue(getAllResponse.getBody().stream()
                .anyMatch(f -> f.getId().equals(response.getBody().getId())));
    }

    @Test
    void shouldCreateFilmWithoutDescription() {
        film = FilmTestData.validFilmWithoutDescription();
        ResponseEntity<Film> response = restTemplate.postForEntity(getFilmsUrl(), film, Film.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());

        assertEquals(film.getName(), response.getBody().getName());
        assertNull(response.getBody().getDescription());
        assertEquals(film.getReleaseDate(), response.getBody().getReleaseDate());
        assertEquals(film.getDuration(), response.getBody().getDuration());
    }

    @Test
    void shouldReturnBadRequestWhenCreateWithInvalidData() {
        film = FilmTestData.invalidFilmWithEmptyName();
        ResponseEntity<String> response = restTemplate.postForEntity(getFilmsUrl(), film, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldUpdateExistingFilm() {
        film = FilmTestData.validFilmWithAllFieldsFilled();
        ResponseEntity<Film> createResponse = restTemplate.postForEntity(getFilmsUrl(), film, Film.class);

        assertNotNull(createResponse.getBody());
        anotherFilm = FilmTestData.filmForUpdate(createResponse.getBody().getId());
        ResponseEntity<Film> updateResponse = restTemplate.exchange(
                getFilmsUrl(),
                HttpMethod.PUT,
                new HttpEntity<>(anotherFilm),
                Film.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals(createResponse.getBody().getId(), updateResponse.getBody().getId());
        assertNotEquals(film.getName(), updateResponse.getBody().getName());
        assertNotEquals(film.getDescription(), updateResponse.getBody().getDescription());
        assertNotEquals(film.getReleaseDate(), updateResponse.getBody().getReleaseDate());
        assertNotEquals(film.getDuration(), updateResponse.getBody().getDuration());
    }

    @Test
    void shouldFailUpdateWithNonExistingId() {
        film = FilmTestData.filmWithNonExistingId();
        ResponseEntity<String> response = restTemplate.exchange(
                getFilmsUrl(),
                HttpMethod.PUT,
                new HttpEntity<>(film),
                String.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void shouldFailUpdateWithoutId() {
        film = FilmTestData.filmWithoutIdForUpdate();
        ResponseEntity<String> response = restTemplate.exchange(
                getFilmsUrl(),
                HttpMethod.PUT,
                new HttpEntity<>(film),
                String.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void shouldAssignSequentialUniqueIdsToNewFilms() {
        Collection<Film> existingFilms = filmController.getAllFilms();
        assertTrue(existingFilms.isEmpty(), "Перед тестом должно быть 0 фильмов");

        film = FilmTestData.validFilmWithAllFieldsFilled();
        anotherFilm = FilmTestData.validFilmWithoutDescription();

        ResponseEntity<Film> response1 = restTemplate.postForEntity(getFilmsUrl(), film, Film.class);
        ResponseEntity<Film> response2 = restTemplate.postForEntity(getFilmsUrl(), anotherFilm, Film.class);

        assertEquals(HttpStatus.CREATED, response1.getStatusCode());
        assertEquals(HttpStatus.CREATED, response2.getStatusCode());

        assertNotEquals(
                Objects.requireNonNull(response1.getBody()).getId(),
                Objects.requireNonNull(response2.getBody()).getId(),
                "ID должны быть уникальными"
        );
        assertEquals(
                response1.getBody().getId() + 1,
                response2.getBody().getId(),
                "ID второго фильма должен быть на 1 больше"
        );
    }

    @Test
    void shouldGetAllFilms() {
        film = FilmTestData.validFilmWithAllFieldsFilled();
        anotherFilm = FilmTestData.validFilmWithoutDescription();

        restTemplate.postForEntity(getFilmsUrl(), film, Film.class);
        restTemplate.postForEntity(getFilmsUrl(), anotherFilm, Film.class);

        ResponseEntity<Collection<Film>> response = restTemplate.exchange(
                getFilmsUrl(),
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
    void shouldRejectEmptyFilmRequest() {
        ResponseEntity<String> response = restTemplate.postForEntity(getFilmsUrl(), null, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}